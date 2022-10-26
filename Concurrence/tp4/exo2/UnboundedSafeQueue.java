package fr.upem.concurrence.td04.exo2;

import java.util.ArrayList;
import java.util.Objects;

public class UnboundedSafeQueue<V> {
  private final ArrayList<V> name = new ArrayList<>();
  private final Object lock = new Object();

  public void add(V value){
    Objects.requireNonNull(value);
    synchronized (lock) {
      name.add(value);
      lock.notify();
    }
  }

  public V take() throws InterruptedException{
    synchronized (lock) {
      while (name.isEmpty()) {
        lock.wait();
      }
      return name.remove(0);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var name = new UnboundedSafeQueue<String>();
    for (var i = 0; i<3; i++) {
      Thread.ofPlatform().start(() -> {
        try {
          while (true) {
            Thread.sleep(2_000);
            name.add(Thread.currentThread().getName());
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
