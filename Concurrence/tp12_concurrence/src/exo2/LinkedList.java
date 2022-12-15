package exo2;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class LinkedList<E> {
  static final class Entry<E> {
    private final E element;
    private volatile Entry<E> next;

    Entry(E element) {
      this.element = element;
    }
  }

  private final Entry<E> head = new Entry<>(null); // fake first entry
  private static final VarHandle NEXT_HANDLE;
  static {
    var lookup = MethodHandles.lookup();
    try{
      NEXT_HANDLE = lookup.findVarHandle(Entry.class, "next", Entry.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public void addLast(E element) {
    var entry = new Entry<>(element);
    var current = head;
    for (;;) {
      var last = last(current);
      if (NEXT_HANDLE.compareAndSet(last, null, entry)) {
        return;
      }

      current = last;
    }
  }

  private static <E> Entry<E> last(Entry<E> entry){
    for(var e = entry;; e = e.next){
      if (e.next == null){
        return e;
      }
    }
  }
  public int size() {
    var count = 0;
    for (var e = head.next; e != null; e = e.next) {
      count++;
    }
    return count;
  }

  private static Runnable createRunnable(LinkedList<String> list, int id) {
    return () -> {
      for (var i = 0; i < 10_000; i++) {
        list.addLast(id + " " + i);
      }
    };
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    var threadCount = 5;
    var list = new LinkedList<String>();
    var tasks = IntStream.range(0, threadCount).mapToObj(id -> createRunnable(list, id)).map(Executors::callable)
        .toList();
    try (var executor = Executors.newFixedThreadPool(threadCount);){
      var futures = executor.invokeAll(tasks);
      executor.shutdown();
      for (var future : futures) {
        future.get();
      }
    }
    System.out.println(list.size());
  }
}