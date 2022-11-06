package exo1;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UnboundedSafeQueue<V> {
  private final ArrayDeque<V> fifo = new ArrayDeque<>();
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  public void add(V value) {
    lock.lock();
    try {
      fifo.add(value);
      condition.signal();
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
      return fifo.remove();
    } finally {
      lock.unlock();
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