package fr.upem.concurrence.td04.exo1;

import java.util.Objects;

public class RendezVous<V> {

  private V value;
  private boolean done;
  private final Object lock = new Object();

  public void set(V value) {
    Objects.requireNonNull(value);
    synchronized (lock) {
      this.value = value;
      lock.notify();
    }
  }

  public V get() throws InterruptedException {
    synchronized (lock) {
      while (!done) {
        //Thread.sleep(1);
        lock.wait();
      }
      return value;
    }
  }
  public static void main(String[] args) throws InterruptedException {
    var rdv = new RendezVous<String>();
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(20_000);
        rdv.set("Message");
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println(rdv.get());
  }
}
//sur treminal avec la commande top, et on voit que ça prends plus de sources