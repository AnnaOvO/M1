package fr.uge.concurrence;

public class Exchanger<T> {
  private T value1; //null
  private T value2; //null
  private static Object lock = new Object();
  private enum State { STATE1, STATE2 }
  private State state;
  /* Q1
  public T exchange(T value) throws InterruptedException {
    synchronized (lock) {
      if (value1 == null) { //state1
        value1 = value;
        while (value2 == null){
          lock.wait();
        }
        return value2;
      }
      // state2
      value2 = value;
      lock.notify();
      return value1;
    }
  }
   */
  /* Q2 */
  public T exchange(T value) throws InterruptedException {
    synchronized (lock) {
      if (state == State.STATE1) { //state1
        value1 = value;
        while (state == State.STATE2) {
          lock.wait();
        }
        return value2;
      }
      // state2
      state = State.STATE2;
      value2 = value;
      lock.notify();
      return value1;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var exchanger = new java.util.concurrent.Exchanger<String>();
    Thread.ofPlatform().start(() -> {
      try {
        System.out.println("thread 1 " + exchanger.exchange("foo1"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println("main " + exchanger.exchange(null));
  }
}

/*
 * Quel est l'affichage attendu ?
 * On s'attend "thread 1 null" et "main foo1"
 *
 * Comment faire pour distinguer le premier et le second appel à la méthode exchange ?
 *
 */