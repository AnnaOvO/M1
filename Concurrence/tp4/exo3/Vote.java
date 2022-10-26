package fr.upem.concurrence.td04.exo3;

import java.util.HashMap;
import java.util.Objects;

public class Vote {

  private final HashMap<String, Integer> voter = new HashMap<String, Integer>();
  private final int voteNumber;
  private int computeVote; //nombre de vote en ce moment
  private final Object lock = new Object();

  public Vote(int i) {
    if (i <= 0){
      throw new IllegalArgumentException();
    }
    synchronized (lock) {
      this.voteNumber = i;
      lock.notifyAll();
    }
  }
  String vote(String someone) throws InterruptedException {
    Objects.requireNonNull(someone);
    synchronized (lock) {
      int voted = voter.containsKey(someone)? voter.get(someone) : 0;
      voter.put(someone, voted+1);
      this.computeVote++;
      while (!(computeVote >= voteNumber)){
        lock.wait();
      }
      lock.notifyAll(); //les autres vont voir s'il est le dernier
      return this.computeWinner();
    }
  }
  private String computeWinner() {
    var score = -1;
    String winner = null;
    for (var e : voter.entrySet()) {
      var key = e.getKey();
      var value = e.getValue();
      if (value > score || (value == score && key.compareTo(winner) < 0)) {
        winner = key;
        score = value;
      }
    }
    return winner;
  }

  public static void main(String[] args) throws InterruptedException {
    var vote = new Vote(4); //4 thread
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(2_000);
        System.out.println("The winner is " + vote.vote("un"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(1_500);
        System.out.println("The winner is " + vote.vote("zero"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(1_000);
        System.out.println("The winner is " + vote.vote("un"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println("The winner is " + vote.vote("zero"));
  }
}

