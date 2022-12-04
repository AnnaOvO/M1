package exo3;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ThePriceIsRight {

  private int currentThread = 0;
  private final int nbThread;
  private final int price;
  private final int[] proposals;
  private int indexOfMin;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition condition = lock.newCondition();
  public ThePriceIsRight(int price, int nbThread) {
    if (nbThread <= 0){
      throw new IllegalArgumentException();
    }
    this.price = price;
    this.nbThread = nbThread;
    this.proposals = new int[nbThread];

  }

  private int distance(int p) {
    return Math.abs(p - price);
  }

  public boolean propose(int proposal) {
    if (proposal <= 0){
      throw new IllegalArgumentException();
    }
    lock.lock();
    try {
      proposals[currentThread] = distance(proposal);
      var index = currentThread++;
      if (currentThread != proposals.length){
        while (currentThread != proposals.length){
          try{
            condition.await();
          } catch (InterruptedException e){
            proposals[index] = Integer.MAX_VALUE;
            return false;
          }
        }
      }
      else {
        var min = Arrays.stream(proposals)
            .map(this::distance)
            .min()
            .orElseThrow();
        indexOfMin = IntStream.range(0, proposals.length)
            .filter(i -> i == min).findFirst()
            .orElseThrow();
        condition.signalAll();
      }
      return index == indexOfMin;
    } finally {
      lock.unlock();
    }
  }
}
