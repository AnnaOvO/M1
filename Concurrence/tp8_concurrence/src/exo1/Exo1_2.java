package exo1;

import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

public class Exo1_2 {
  /*
  public static boolean isPrime(long candidate) {
    if (candidate <= 1) {
      return false;
    }
    for (var i = 2; i <= Math.sqrt(candidate); i++) {
      if (candidate % i == 0 || !Thread.interrupted()) {
        return false;
      }
    }
    return true;
  }
  public static OptionalLong findPrime() {
    var generator = ThreadLocalRandom.current();
    for (;!Thread.interrupted();) {
      var candidate = generator.nextLong();
      if (isPrime(candidate)) {
        return OptionalLong.of(candidate);
      }
    }
    System.out.println("too late...");
    return OptionalLong.empty();
  }
  */
  public static boolean isPrime(long candidate) throws InterruptedException {
    if (candidate <= 1) {
      return false;
    }
    for (var i = 2; i <= Math.sqrt(candidate); i++) {
      if (Thread.interrupted()) {
        throw new InterruptedException("Interrupted !");
      }
      if (candidate % i == 0){
        return false;
      }
    }
    return true;
  }
  public static OptionalLong findPrime() throws InterruptedException {
    var generator = ThreadLocalRandom.current();
    for (;!Thread.interrupted();) {
      var candidate = generator.nextLong();
      try {
        if (isPrime(candidate)) {
          return OptionalLong.of(candidate);
        }
      } catch (InterruptedException e){
        break;
      }
    }
    System.out.println("too late...");
    return OptionalLong.empty();
  }

  public static void main(String[] args) throws InterruptedException {
    var t2 = Thread.ofPlatform().start(() -> {
      try {
        System.out.println("Found a random prime : " + findPrime().orElseThrow());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Thread.sleep(3_000); //ici le thread main attend 5 sec et interrupt le thread t2
    t2.interrupt(); // le thread t2 est interrupté
  }
}

