package exo3;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class LinkedListLockFree<E> {
  private final AtomicReference<Link<E>> head = new AtomicReference<>();

  private record Link<E>(E value, Link<E> next) {
    private Link {
      Objects.requireNonNull(value);
    }
  }

  public void addFirst(E value) {
    Objects.requireNonNull(value);
    for (;;) {
      var current = head.get();   // volatile read
      if (current == null) {
        head.set(new Link<>(value, null));  // volatile write
        return;
      }
      if (head.compareAndSet(current, new Link<>(value, current))) {    // volatile read/write
        return;
      }
    }
  }

  public void forEach(Consumer<? super E> consumer) {
    Objects.requireNonNull(consumer);
    for (var current = head.get(); current != null; current = current.next) {
      consumer.accept(current.value());
    }
  }

  public E pollFirst() {
    var current = head.get();
    if (current == null) {
      return null;
    }
    head.updateAndGet(e -> e.next);
    return current.value();
  }

  public static void main(String[] args) {
    var list = new LinkedListLockFree<String>();
    list.addFirst("Noel");
    list.addFirst("papa");
    list.addFirst("petit");
    list.forEach(System.out::println);

    System.out.println("poll : " + list.pollFirst());
    list.forEach(System.out::println);
  }
}