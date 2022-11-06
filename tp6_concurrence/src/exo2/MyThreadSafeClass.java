package exo2;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class MyThreadSafeClass {
  private final ArrayBlockingQueue<Long> blockingQueue;
  public MyThreadSafeClass(int primeCount){
    blockingQueue = new ArrayBlockingQueue<>(primeCount); // pas besoin de borner parce qu'il est final
  }
  private static final ReentrantLock lock = new ReentrantLock();
  private static final Condition condition = lock.newCondition();

  public void addPrime(long prime) throws InterruptedException {
    blockingQueue.put(prime);
  }

  public long sum() throws InterruptedException {
    var sum = 0;
    for (var i = 0; i < 10; i++){
      sum += blockingQueue.take();
    }
    return sum;
  }
  public static boolean isPrime(long l) {
    if (l <= 1) {
      return false;
    }
    lock.lock();
    try {
      for (long i = 2L; i <= l / 2; i++) {
        if (l % i == 0) {
          return false;
        }
      }
      return true;
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    IntStream.range(0, 5).forEach(i ->{
      Thread.ofPlatform().start(()->{
        var random = new Random();
        var nbThread = 5;
        for (;;) {
          long nb = 1_000_000_000L + (random.nextLong() % 1_000_000_000L);
          if (isPrime(nb)) {
            /* todo */
          }
        }
      });
    });
  }
}
