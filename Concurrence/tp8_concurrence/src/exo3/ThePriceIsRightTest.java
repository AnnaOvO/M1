package exo3;
import static org.junit.Assert.*;

import org.junit.Test;

public class ThePriceIsRightTest {
  @Test(timeout = 5_000)
  public void test102IsCloserThan97To100() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> assertTrue(tpir.propose(102)));
    assertFalse(tpir.propose(97));
    thread.join();
  }

  @Test(timeout = 5_000)
  public void testWithASlowThread1() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
      assertFalse(tpir.propose(105));
    });
    assertTrue(tpir.propose(102));
    thread.join();
  }

  @Test(timeout = 5_000)
  public void testWithASlowThread2() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> {
      assertFalse(tpir.propose(110));
    });
    Thread.sleep(1000);
    assertTrue(tpir.propose(107));
    thread.join();
  }

  @Test(timeout = 5_000)
  public void testWithALotOfThreads() throws InterruptedException {
    Thread[] threads = new Thread[50];
    ThePriceIsRight tpir = new ThePriceIsRight(100, 1 + threads.length);
    for (int i = 0; i < threads.length; i++) {
      int id = i;
      threads[i] = Thread.ofPlatform().start(() -> {
        assertFalse(tpir.propose(110 + id));
      });
    }
    assertTrue(tpir.propose(93));
    for (Thread thread : threads) {
      thread.join();
    }
  }

  @Test(timeout = 5_000)
  public void testWithTwoThreadsThatProposeTheSamePrice() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> {
      assertTrue(tpir.propose(999));
    });
    Thread.sleep(1_000);
    assertFalse(tpir.propose(999));
    thread.join();
  }

  @Test(timeout = 5_000)
  public void testWithMoreThreadsThanTheNumberOfParty() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 1);
    assertTrue(tpir.propose(200));
    assertFalse(tpir.propose(100));
  }

  @Test(timeout = 5_000)
  public void testWithMoreThreadsThanTheNumberOfParty2() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> assertTrue(tpir.propose(150)));
    assertFalse(tpir.propose(200));
    thread.join();
    assertFalse(tpir.propose(100));
  }

  @Test(timeout = 5_000)
  public void testWithAThreadWhichIsInterrupted() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 3);
    Thread thread1 = Thread.ofPlatform().start(() -> {
      assertFalse(tpir.propose(100));
    });
    Thread thread2 = Thread.ofPlatform().start(() -> {
      assertTrue(tpir.propose(110));
    });
    Thread.sleep(1_000);
    thread1.interrupt();
    thread1.join();
    thread2.join();
  }

  @Test(timeout = 5_000)
  public void testWithOnlyOneThreadWhichIsInterrupted() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> {
      assertFalse(tpir.propose(110));
    });
    Thread.sleep(1_000);
    thread.interrupt();
    thread.join();
  }

  @Test(timeout = 5_000)
  public void testWithTwoThreadsThatProposeTheSameValueOneIsInterrupted() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 3);
    Thread thread1 = Thread.ofPlatform().start(() -> {
      assertFalse(tpir.propose(200));
    });
    Thread thread2 = Thread.ofPlatform().start(() -> {
      assertTrue(tpir.propose(200));
    });
    Thread.sleep(1_000);
    thread1.interrupt();
    thread1.join();
    thread2.join();
  }

  @Test(timeout = 5_000)
  public void testWhenTheFirstThreadIsInterruptedAndAnotherThreadComesLater() throws InterruptedException {
    ThePriceIsRight tpir = new ThePriceIsRight(100, 2);
    Thread thread = Thread.ofPlatform().start(() -> {
      assertFalse(tpir.propose(90));
    });
    thread.interrupt();
    thread.join();
    assertFalse(tpir.propose(100));
  }
}