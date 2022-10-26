package fr.uge.concurrence.exo3;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class HelloListBug {
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
/*
* Réponses :
* La taille de la liste changera chaque fois.
* On peut voir le code de add(). Dans le cas où la capacité initiale est fixée et assez grande, grow() n'est jamais appelée. Sinon, cela arrive quand le tableau elementData est rempli (size+1).
*
* On suppose que pour la taille de elementData est 128.
* T1 est schédulé, il fait l'appel list.add(?). Supposons qu'à ce moment là size (et donc le paramètre s) vaut 13.
* T1 est dé-schédulé entre elementData[s] = e et size = s + 1.
* T2 est schédulé fait l'appel list.add(?). Le champs size vaut 13 et T2 garde la main pendant quelques temps. Il arrive au moment où size vaut 128 Il rentre dans list.add(?, elementData, 128).
* Le paramètre s vaut 128, donc T2 passe le test du if et devrait ensuite appeler grow() puis faire appel à grow(129) (c'est à dire size+1). Mais il est dé-schédulé juste avant le grow().
* T1 est schédulé, il écrit size = 14 puis il est dé-schédulé.
* T2 est schédulé, il appelle grow(15) (c'est à dire size+1), ce qui remplace elementData par un tableau de taille 30 (environ).
* Puis il effectue l'instruction elementData[128] = ? alors que le tableau n'a que 30 cases, ce qui lève une ArrayIndexOutOfBoundsException.
*/