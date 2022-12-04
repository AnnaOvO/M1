package fr.upem.concurrence.td07;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Semaphore {

  private int nbPermit;
  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();

  public Semaphore(int nbPermit) {
    this.nbPermit = nbPermit;
  }

  public void release(){
    lock.lock();
    try {
      nbPermit++;
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  public boolean tryAcquire(){
    lock.lock();
    try {
      if (nbPermit >= 1){
        nbPermit--;
        return true;
      }
      return false;
    } finally {
      lock.unlock();
    }
  }

  public void acquire() throws InterruptedException {
    lock.lock();
    try {
      while (nbPermit <= 0){
        condition.await();
      }
      nbPermit--;
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    var semaphore = new Semaphore(5);
    IntStream.range(0, 10).forEach(i->{
      Thread.ofPlatform().start(()->{
        try {
          semaphore.acquire();
          System.out.println("Thread " + i + " acquire permit");
          Thread.sleep(1_000);
          System.out.println("Thread " + i + " release permit");
          semaphore.release();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    });
  }
}
