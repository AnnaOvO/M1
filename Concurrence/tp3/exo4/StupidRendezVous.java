package fr.uge.concurrence.exo4;

import java.util.Objects;
import java.util.Random;

import static fr.uge.concurrence.exo4.FindPrime.isPrime;

public class StupidRendezVous<V> {
  private V value;

  public void set(V value) {
    Objects.requireNonNull(value);
    this.value = value;
  }

  public V get() throws InterruptedException {
    while (value == null) {
      //Thread.sleep(1); // then comment this line !
    }
    return value;
  }
}
/*
* Le programme se déroule sans arrêt
* JIT il va stocker la variable à local et il va jamais vérifier le value est null ou pas.
* */