import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

public class Prime {
	
	public static boolean isPrime(long candidate) {
    if (candidate <= 1) {
      return false;
    }
    for (var i = 2; i <= Math.sqrt(candidate); i++) {
      if (candidate % i == 0) {
        return false;
      }
    }
    return true;
  }

  public static OptionalLong findPrime() {
    var generator = ThreadLocalRandom.current();
    for (;;) {
      var candidate = generator.nextLong();
      if (isPrime(candidate)) {
        return OptionalLong.of(candidate);
      }
    }
  }

	public static void main(String[] args) {
		Thread.ofPlatform().start(() -> {
      System.out.println("Found a random prime : " + findPrime().orElseThrow());
    });
	}

}
