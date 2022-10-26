package tp2;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class HelloListBug {
  public static void main(String[] args) throws InterruptedException {
    var nbThreads = 4;
    var threads = new Thread[nbThreads];
    var list = new ArrayList<Integer>(5000 * nbThreads);

    IntStream.range(0, nbThreads).forEach(j -> {
      Runnable runnable = () -> {
        for (var i = 0; i < 5000; i++) {
          //System.out.println("hello " + j + " " + i); //thread safe
          list.add(i);
        }
      };

      threads[j] = Thread.ofPlatform().start(runnable);
    });

    for (var thread : threads) {
      thread.join();
    }
    System.out.println(list.size());
    System.out.println("le programme est fini");
  }
}
/*
* Parfois 20000 parfois moin de 20000.
* Parce que les 4 threads peuvent être déscheduler, dans ce cas là
* on a moin de élément dans la liste */