package fr.uge.concurrence.exo4;

import java.util.Objects;
import java.util.Random;

import static fr.uge.concurrence.exo4.FindPrime.isPrime;

public class RendezVous<V> {

  private V value;
  private final Object lock = new Object();

  public void set(V value) {
    Objects.requireNonNull(value);
    synchronized (lock) {
      this.value = value;
    }
  }

  public V get() throws InterruptedException {
    for (;;) {
      synchronized (lock) {
        if (value != null) return value;
        //Thread.sleep(1); // then comment this line !
      }
    }
  }
}