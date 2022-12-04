package fr.upem.concurrence.td07;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadSafeClass {

  private final int primeCount;
  private final ArrayList<Long> primes;
  private final ReentrantLock lock;
  private final Condition condition;


  public MyThreadSafeClass(int primeCount) {
    if (primeCount <= 0) {
      throw new IllegalArgumentException();
    }
    this.primeCount = primeCount;
    primes = new ArrayList<>();
    lock = new ReentrantLock();
    condition = lock.newCondition();
  }


  public void addPrime(long prime){
    lock.lock();
    try {
      if (primes.size() >= primeCount){
        throw new IllegalArgumentException();
      }
      primes.add(prime);
      condition.signal();
    } finally {
      lock.unlock();
    }
  }

  public long sum() throws InterruptedException{
    lock.lock();
    try {
      while (primes.size() < primeCount){
        condition.await();
      }
      return primes.stream().mapToLong(Long::longValue).sum();
    } finally {
      lock.unlock();
    }
  }

}
