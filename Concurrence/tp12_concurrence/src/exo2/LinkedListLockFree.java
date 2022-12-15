package exo2;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class LinkedListLockFree<E> {
  private static final class Entry<E> {
    private final E element;
    private volatile Entry<E> next;

    Entry(E element) {
      this.element = element;
    }
  }

  private Entry<E> head = new Entry<>(null); // fake first entry
  private volatile Entry<E> tail = head;
  private static final VarHandle NEXT_HANDLE;
  private static final VarHandle TAIL_HANDLE;

  static {
    var lookup = MethodHandles.lookup();
    try {
      NEXT_HANDLE = lookup.findVarHandle(Entry.class, "next", Entry.class);
      TAIL_HANDLE = lookup.findVarHandle(LinkedListLockFree.class, "tail", Entry.class);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private static <E> Entry<E> getLastValue(Entry<E> current) {
    for (var entry = current;; entry = entry.next) {    // volatile read
      if (entry.next == null) {   // volatile read
        return entry;
      }
    }
  }

  public void addLast(E element) {
    Objects.requireNonNull(element);
    var entry = new Entry<>(element);
    var oldTail = tail;
    var current = oldTail;
    for(;;) {
      if (NEXT_HANDLE.compareAndSet(current, null, entry)) {
        TAIL_HANDLE.compareAndSet(this, oldTail, entry);
        return;
      }
      current = getLastValue(current);
    }
  }

  public int size() {
    var count = 0;
    for (var e = head.next; e != null; e = e.next) {
      count++;
    }
    return count;
  }

  private static Runnable createRunnable(LinkedListLockFree<String> list, int id) {
    return () -> {
      for (var i = 0; i < 10_000; i++) {
        list.addLast(id + " " + i);
      }
    };
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    var threadCount = 5;
    var list = new LinkedListLockFree<String>();
    var tasks = IntStream.range(0, threadCount).mapToObj(id -> createRunnable(list, id)).map(Executors::callable)
        .toList();
    try (var executor = Executors.newFixedThreadPool(threadCount)) {
      var futures = executor.invokeAll(tasks);
      executor.shutdown();
      for (var future : futures) {
        future.get();
      }
    }
    System.out.println(list.size());
  }
}