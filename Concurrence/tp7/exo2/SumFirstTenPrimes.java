package fr.upem.concurrence.td07;

import java.util.Random;
import java.util.stream.IntStream;

public class SumFirstTenPrimes {

  public static boolean isPrime(long l) {
    if (l <= 1) {
      return false;
    }
    for (long i = 2L; i <= l / 2; i++) {
      if (l % i == 0) {
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) throws InterruptedException {
    var myThreadSafeClass = new MyThreadSafeClass(10);

    IntStream.range(0, 5).forEach(i ->{
      Thread.ofPlatform().daemon().start(() -> {
        var random = new Random();
        for (;;) {
          long nb = 1_000_000_000L + (random.nextLong(1_000_000_000L));
          if (isPrime(nb)) {
            myThreadSafeClass.addPrime(nb);
          }
        }
      });
    });
    System.out.println(myThreadSafeClass.sum());

  }
}
