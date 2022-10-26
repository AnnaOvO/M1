# Concurrence TP2 : Data race, entrelacement et JIT
## WANG Chen APP2

### **Exercice 0 :**

* Récupérez la classe Counter.java et exécutez-la plusieurs fois.
```java
public class Counter {
    private int value;

public void addALot() {
    for (var i = 0; i < 100_000; i++) {
        this.value++;
    }
}

    public static void main(String[] args) throws InterruptedException {
        var counter = new Counter();
        var thread1 = Thread.ofPlatform().start(counter::addALot);
        var thread2 = Thread.ofPlatform().start(counter::addALot);
        thread1.join();
        thread2.join();
        System.out.println(counter.value);
    }
}
```
* Essayez d'expliquer ce que vous observez.

* Est-il possible que ce code affiche moins que 100 000 ? Expliquer précisément pourquoi.

**Réponses :**

On a observé que parfois c'est 200_000 et parfois moins de 200_000. 
Parce que les 2 threads ne sont pas exécutés en meme temps, thread2 peut
déscheduler le thread1 donc  la valeur peut être moin de 200_000.

***
### **Exercice 1 :**

* Récupérer la classe StopThreadBug.java. Avant de l'exécuter, essayer de comprendre quel est le comportement espéré.
Où se trouve la data-race ?

```java
public class StopThreadBug implements Runnable {
  private boolean stop = false;

  public void stop() {
    stop = true;
  }

  @Override
  public void run() {
    while (!stop) {
      System.out.println("Up");
    }
    System.out.print("Done");
  }

  public static void main(String[] args) throws InterruptedException {
    var stopThreadBug = new StopThreadBug();
    Thread.ofPlatform().start(stopThreadBug::run);
    Thread.sleep(5_000);
    System.out.println("Trying to tell the thread to stop");
    stopThreadBug.stop();
  }
}
```
* Exécuter la classe plusieurs fois. Qu'observez-vous ?

* Modifiez la classe StopThreadBug.java pour supprimer l'affichage dans la boucle du thread. Exécuter la classe à nouveau plusieurs fois. Essayez d'expliquer ce comportement.

* Le code avec l'affichage va-t-il toujours finir par arrêter le thread ?

**Réponse :**

Le résultat est toujours le même:
```
Up
...
Up
Trying to tell the thread to stop
Up
Done
Process finished with exit code 0
```

La fonction exécute sans jamais arrêter.

Non, parce que nous avons le possibilité de rencontrer une exception.

***

### **Exercice 2 :**
```java
class ExempleReordering {
  int a = 0;
  int b = 0;

  public static void main(String[] args) {
    var e = new ExempleReordering();
    Thread.ofPlatform().start(() -> {
      System.out.println("a = " + e.a + "  b = " + e.b);
    });
    e.a = 1;
    e.b = 2;
  }
}
```
* Quand on exécute le code précédent, quels peuvent être les différents affichages constatés ?

**Réponses :**
```
Le résultat peut être des combinaison de a et b tel que
a    b
0    0
1    0
1    2
0    2
```

```java
class ExampleLongAffectation {
  long l = -1L;

  public static void main(String[] args) {
    var e = new ExampleLongAffectation();
    Thread.ofPlatform().start(() -> {
      System.out.println("l = " + e.l);
    });
    e.l = 0;
  }
}
```
* Quand on exécute le code précédent, quels peuvent être les différents affichages constatés ?

**Réponses :**
```
-1 (1111 1111)    0 (0000 0000)
   (1111 0000)
   (0000 1111)
 ```

***

### **Exercice 3 :**
* On souhaite modifier l'exercice 2 du TP précédent (celui sur l'utilisation de la méthode join). Pour mémoire, le code du main que vous avez écrit devrait ressembler à ça :

```java
public static void main(String[] args) throws InterruptedException {
    var nbThreads = 4;
    var threads = new Thread[nbThreads];

    IntStream.range(0, nbThreads).forEach(j -> {
    Runnable runnable = () -> {
    for (var i = 0; i < 5000; i++) {
    System.out.println("hello " + j + " " + i);
    }
    };

    threads[j] = Thread.ofPlatform().start(runnable);
    });

    for (var thread : threads) {
    thread.join();
    }

    System.out.println("le programme est fini");
    }
```
* Désormais, au lieu d'afficher les nombres, on veut les stocker dans une unique ArrayList (une seule liste pour tous les threads) de capacité initiale 20000 = 4 * 5000.

```var list = new ArrayList<Integer>(5000 * nbThreads);```


* Recopiez cette classe dans une nouvelle classe HelloListBug puis modifiez-la pour y ajouter les nombres dans la liste au lieu de les afficher. Faites afficher la taille finale de la liste, une fois tous les threads terminés.

    **(Attention, les thread ne doivent plus faire d'affichage!)**


* Exécuter le programme plusieurs fois et noter les différents affichages.


* Expliquer comment la taille de la liste peut être plus petite que le nombre total d'appels à la méthode add.

  **(Pour (vraiment) comprendre, il faut regarder le code de la méthode ArrayList.add. )**

**Réponse :**

Parfois 20000 parfois moin de 20000.
Parce que les 4 threads peuvent être déscheduler, dans ce cas là on a moin de élément dans la liste.