package exo3;

import java.util.Objects;
import java.util.function.Consumer;

public class LinkedList<E> {

  private record Link<E>(E value, Link<E> next) {
    private Link {
      Objects.requireNonNull(value);
    }
  }

  private Link<E> head;

  /**
   * Add the non-null value at the start of the list
   *
   * @param value
   */
  public void addFirst(E value) {
    Objects.requireNonNull(value);
    head = new Link<>(value, head);
  }

  /**
   * applies the consumer the elements of the list in order
   *
   * @param consumer
   */
  public void forEach(Consumer<? super E> consumer) {
    Objects.requireNonNull(consumer);
    for (var currentLink = head; currentLink != null; currentLink = currentLink.next) {
      consumer.accept(currentLink.value);
    }
  }

  /**
   * Removes the first value of the list and returns it if the list not empty. If the list is empty, returns null.
   * @return the value at the start of the list if the list is not empty and null if the list is empty
   */
  public E pollFirst() {
    if (head == null) {
      return null;
    }
    var value = head.value;
    head = head.next;
    return value;
  }

  public static void main(String[] args) {
    var list = new LinkedList<String>();
    list.addFirst("Noel");
    list.addFirst("papa");
    list.addFirst("petit");
    list.forEach(System.out::println);
  }

}