package fr.upem.concurrence.td04.exo2;

import java.util.ArrayList;
import java.util.Objects;

public class BoundedSafeQueue<V> {
  private final ArrayList<V> list = new ArrayList<>();
  private final int capacity;
  private final Object lock = new Object();

  public BoundedSafeQueue(int capacity) {
    this.capacity = capacity;
  }

  public void put(V value) throws InterruptedException {
    Objects.requireNonNull(value);
    synchronized (lock) {
      while(list.size() >= capacity){
        lock.wait();
      }
      lock.notifyAll();
      list.add(value);
    }
  }
  public V take() throws InterruptedException{
    synchronized (lock) {
      while (list.isEmpty()) {
        lock.wait();
      }
      lock.notifyAll();
      return list.remove(0);
    }
  }
  public static void main(String[] args) throws InterruptedException {
    var name = new BoundedSafeQueue<String>(5);
    for (var i = 0; i<3; i++) {
      Thread.ofPlatform().start(() -> {
        try {
          while (true) {
            Thread.sleep(2_000);
            name.put(Thread.currentThread().getName());
          }
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
    }
    while (true) {
      System.out.println(name.take());
    }
  }
}
