package exo1;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedSafeQueue<V> {
  private final ArrayDeque<V> fifo = new ArrayDeque<>();
  private final int capacity;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  public BoundedSafeQueue(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException();
    }
    this.capacity = capacity; //pas besoin de synchro parce que ici est final.
  }

  public void put(V value) throws InterruptedException {
    lock.lock();
    try {
      while (fifo.size() == capacity) {
        condition.await();
      }
      fifo.add(value);
      condition.signalAll();

    } finally {
      lock.unlock();
    }
  }

  public V take() throws InterruptedException {
    lock.lock();
    try {
      while (fifo.isEmpty()) {
        condition.await();
      }
      condition.signal();
      return fifo.remove();
    } finally {
      lock.unlock();
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
/*
* Pourquoi utilise-t-on noitfyAll et non pas notify ici ?
* Parce qu'il y a peut avoir les autres threads attendre ici
* */