package fr.uge.concurrence.exo1;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class HelloListBug {
  public static void main(String[] args) throws InterruptedException {
    var nbThreads = 4;
    var threads = new Thread[nbThreads];

    var list = new ArrayList<Integer>();
    var lock = new Object();

    IntStream.range(0, nbThreads).forEach(j -> {
      Runnable runnable = () -> {
        for (var i = 0; i < 5_000; i++) {
          synchronized (lock){
            list.add(i);
          }
        }
      };

      threads[j] = Thread.ofPlatform().start(runnable);
    });

    for (Thread thread : threads) {
      thread.join();
    }

    int size;
    synchronized (lock){
      size = list.size();
    }
    System.out.println("taille de la liste:" + list.size());
  }
}

/* Exo3
public class fr.uge.concurrence.exo1.HelloListBug {
  public static void main(String[] args) throws InterruptedException {
    var nbThreads = 4;
    var threads = new Thread[nbThreads];

    var list = new ArrayList<Integer>();

    IntStream.range(0, nbThreads).forEach(j -> {
      Runnable runnable = () -> {
        for (var i = 0; i < 5000; i++) {
          list.add(i);
        }
      };

      threads[j] = Thread.ofPlatform().start(runnable);
    });

    for (Thread thread : threads) {
      thread.join();
    }
    System.out.println("taille de la liste:" + list.size());
  }
}

//La taille de la liste changera chaque fois.

 */