package fr.uge.fifo;

import java.util.ArrayList;
import java.util.Objects;

public class Fifo<E> {
  private final ArrayList<E> array = new ArrayList<E>();
  private int capacity;
  private int size = 0;

  public Fifo(int capacity){
    if (capacity <= 0){
      throw new IllegalArgumentException();
    }
    this.capacity = capacity;
  }

  public void offer(E value) {
    Objects.requireNonNull(value);
    if (capacity <= 0 || size >= capacity){
      throw new IllegalStateException();
    }
    array.add(value);
    size++;
  }

  public E poll() {
    if (size <= 0){
      throw new IllegalStateException();
    }
    var e = array.remove(0);
    size--;
    return e;
  }
}
