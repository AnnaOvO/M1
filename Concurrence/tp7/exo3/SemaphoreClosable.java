package fr.upem.concurrence.td07;

import java.nio.channels.AsynchronousCloseException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class SemaphoreClosable {

  private int nbPermit;
  private final Lock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  private int nbThreadWait;
  private boolean isClose;

  public SemaphoreClosable(int nbPermit) {
    this.nbPermit = nbPermit;
    nbThreadWait = 0;
    isClose = false;
  }

  public void release(){
    lock.lock();
    try {
      if (isClose){
        return;
      }
      nbPermit++;
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  public boolean tryAcquire(){
    lock.lock();
    if (isClose){
      throw new IllegalStateException();
    }
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

  public void acquire() throws InterruptedException, AsynchronousCloseException {
    lock.lock();
    try {
      if (isClose){
        throw new IllegalStateException();
      }
      nbThreadWait++;
      while (nbPermit <= 0){
        if (isClose){
          throw new AsynchronousCloseException();
        }
        condition.await();
      }
      nbThreadWait--;
      nbPermit--;
    } finally {
      lock.unlock();
    }
  }

  public int waitingForPermits(){
    lock.lock();
    try {
      return nbThreadWait;
    } finally {
      lock.unlock();
    }
  }

  public void close(){
    lock.lock();
    try {
      isClose = true;
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var semaphore = new SemaphoreClosable(5);
    IntStream.range(0, 10).forEach(i->{
      Thread.ofPlatform().start(()->{
        try {
          semaphore.acquire();
          System.out.println("Thread " + i + " acquire permit");
          Thread.sleep(1_000);
          System.out.println("Thread " + i + " release permit");
          semaphore.release();
        } catch (InterruptedException | AsynchronousCloseException e) {
          Thread.currentThread().interrupt();
        }
      });

    });
    //Thread.sleep(1_000);
    //System.out.println("Number of thread who waiting for permits " + semaphore.waitingForPermits());
    Thread.sleep(10_000);
    if (semaphore.waitingForPermits() == 0){
      semaphore.close();
    }
  }
}

/**
 * Entre le "if (semaphore.waitingForPermits() == 0)" et "semaphore.close()", il peut avoir un appelle à la méthode acquire
 * il faut que la méthode close fasse la condition "if (semaphore.waitingForPermits() == 0)".
 */
