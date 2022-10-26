# Concurrence TP3 : Synchronized
## WANG Chen APP2

### **Exercice 1 :**

* Dans cet exercice, on reprend le code du dernier exercice de la séance précédente. Le but était de démarrer 4 threads qui ajoutent chacun dans une même ArrayList les nombres de 0 à 5000. Vous devez avoir obtenu un code qui ressemble à ça :

```java
public class fr.uge.concurrence.exo1.HelloListBug {
  public static void main(String[] args) throws InterruptedException {
    var nbThreads = 4;
    var threads = new Thread[nbThreads];

    var list = new ArrayList<Integer>(5_000 * nbThreads);

    IntStream.range(0, nbThreads).forEach(j -> {
      Runnable runnable = () -> {
        for (var i = 0; i < 5_000; i++) {
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
```

* Commencer par mette l'instruction d'ajout dans un bloc synchronisé, afin de constater qu'a priori, on ne perd plus de valeurs à cause de l'entrelacement des instructions. 
Rappeler quelles doivent être les propriétés de l'objet qui sert de lock.

**Réponse :**
```java
public class fr.uge.concurrence.exo1.HelloListBug {
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
```
On utilise lock si : 1) Forte contention (bcp de thread en attente). 2) Operations autres que prendre/rendre un jeton
Un verrou est un outil utilisé pour contrôler l'accès à une ressource partagée par plusieurs threads, généralement un verrou fournit un accès exclusif à une ressource partagée, un seul thread peut acquérir un verrou à la fois, et tout accès à une ressource partagée nécessite le verrou pour être acquis en premier. Cependant, certains verrous peuvent permettre un accès simultané aux ressources partagées, comme le verrou en lecture-écriture de ReadWriteLock.

* Il est fort probable que votre code ne respecte pas les bonnes pratiques vues en cours. En particulier, il n'y a probablement pas d'encapsulation du verrou. Pour palier cela, réaliser une classe ThreadSafeList qui implémente une liste thread-safe permettant d'ajouter d'un élément et de récupérer la taille de la liste. Utiliser cette classe pour écrire une classe HelloListFixedBetter.


Au lieu d'afficher la taille de la liste, on veut afficher le contenu de la liste. Modifier la classe ThreadSafeList pour permettre cette évolution.

**Réponses :**

```java
package fr.uge.concurrence;

import java.util.ArrayList;

public class ThreadSafeList {

  private final ArrayList<Integer> list;

  private final Object lock = new Object();

  public ThreadSafeList(int capacity){
    list = new ArrayList<>(capacity);
  }

  public void add(int value){
    synchronized (lock){
      list.add(value);
    }
  }

  public int size(){
    return list.size();
  }
}
```

***
### **Exercice 2 :**

*  Une école possède un tableau d'honneur (où l'on met le nom et le prénom de l'élève le plus méritant) qui peut être mis à jour de façon informatique. Il n'en faut pas plus pour que Mickey Mouse et Donald Duck, nos deux apprentis hackers, écrivent un petit programme qui met à jour automatiquement le tableau d'honneur avec leurs noms : HonorBoard.java.

*  Expliquer pourquoi la classe HonorBoard n'est pas thread-safe.
   Si vous ne voyez pas, faites un grep "Mickey Duck" sur la sortie du programme et donner un scénario pouvant mener à cet affichage.
   Rappel général : un test qui plante indique un problème, un test qui ne plante pas n'indique rien du tout.

**Réponse :**

Parce que l'affichage peut être firstname1+lastname1 ou firstname2+lastname1 ou firstname1+lastname2 ou firstname2+lastname2. L'ordre d'affichage n'est pas assuré.

* Modifier le code de la classe HonorBoard pour la rendre thread-safe .
Vérifier avec grep sur la sortie comme précédemment (pendant plusieurs minutes).

**Réponse :**
```java
public class HonorBoard {
  private String firstName;
  private String lastName;
  private final Object lock = new Object();

  public void set(String firstName, String lastName) {
    synchronized (lock){
      this.firstName = firstName;
      this.lastName = lastName;
    }
  }
  public String firstName() {
    synchronized (lock){
      return firstName;
    }
  }
  public String lastName() {
    synchronized (lock) {
      return lastName;
    }
  }
  @Override
  public String toString() {
    synchronized (lock) {
      return firstName + ' ' + lastName;
    }
  }

  public static void main(String[] args) {
    var board = new HonorBoard();
    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Mickey", "Mouse");
      }
    });

    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Donald", "Duck");
      }
    });

    Thread.ofPlatform().start(() -> {
      for(;;) {
        //System.out.println(board);
        System.out.println(board.firstName() + ' ' + board.lastName());
      }
    });
  }
}
```
Maintenant que votre classe est thread-safe, peut-on remplacer la ligne :

```
System.out.println(board);
```
par la ligne :
```
System.out.println(board.firstName() + ' ' + board.lastName());
```
avec les deux accesseurs définis comme d'habitude?

```
  public String firstName() {
    return firstName;
  }
  public String lastName() {
    return lastName;
  }
```

**Réponse :**

On peut pas assurer l'affichage est bon, parce que le getter n'assure pas la class est thread-safe.
```
//1 : Set firstName et lastName en Mickey Mouse
//2 : Rentre dans system.out.println(firstname() + "" + lastname()) et affiche le firstname courant (Mickey)
//3 : Set firstname et lastname en Donald Duck (setter fait ça!!)
//2 : continu et affiche le lastname courrant (Duck)
```
***

### **Exercice 3 :**

On reprend une dernière fois le code qui entrelace les accès à une ArrayList et cette fois-ci on ne fixe pas la capacité initiale de la liste :

```java
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
```

* Exécuter le programme plusieurs fois. Quel est le nouveau comportement observé ? Expliquer quel est le problème. Là encore, il faut regarder le code de la méthode ArrayList.add.

**Réponses :**

La taille de la liste changera chaque fois.
On peut voir le code de add(). Dans le cas où la capacité initiale est fixée et assez grande, grow() n'est jamais appelée. Sinon, cela arrive quand le tableau elementData est rempli (size+1).

* On suppose que pour la taille de elementData est 128.
* T1 est schédulé, il fait l'appel list.add(?). Supposons qu'à ce moment là size (et donc le paramètre s) vaut 13.
* T1 est dé-schédulé entre elementData[s] = e et size = s + 1.
* T2 est schédulé fait l'appel list.add(?). Le champs size vaut 13 et T2 garde la main pendant quelques temps. Il arrive au moment où size vaut 128 Il rentre dans list.add(?, elementData, 128).
* Le paramètre s vaut 128, donc T2 passe le test du if et devrait ensuite appeler grow() puis faire appel à grow(129) (c'est à dire size+1). Mais il est dé-schédulé juste avant le grow().
* T1 est schédulé, il écrit size = 14 puis il est dé-schédulé.
* T2 est schédulé, il appelle grow(15) (c'est à dire size+1), ce qui remplace elementData par un tableau de taille 30 (environ).
* Puis il effectue l'instruction elementData[128] = ? alors que le tableau n'a que 30 cases, ce qui lève une ArrayIndexOutOfBoundsException.


***

### **Exercice 4 :**
* Dans cet exercice, on souhaite écrire une classe RendezVous thread-safe qui permet de réaliser le passage d'une valeur entre des threads. C'est une première tentative qui ne fonctionnera pas de réaliser une méthode bloquante. Nous verrons au prochain cours comment résoudre ce problème de façon satisfaisante.

* Dans le programme FindPrime.java, plusieurs threads sont démarrés et tirent des grands nombres au hasard jusqu'à en trouver un qui soit premier. Le principe est que le main attend jusqu'à ce que l'un des threads ait trouvé un nombre premier. Pour réaliser le passage de la valeur entre les threads qui cherchent des nombres premiers et le main, nous allons écrire une classe RendezVous.


```java
public static void main(String[] args) throws InterruptedException {
  var nbThreads = 4;
  var rdv = new RendezVous<Long>();
  for (var i = 0; i < nbThreads; i++) {
    var fi = i;
    Thread.ofPlatform().deamon().start(() -> {
      var random = new Random();
      for (;;) {
        var nb = 10_000_000_000L + (random.nextLong() % 10_000_000_000L);
        if (isPrime(nb)) {
          rdv.set(nb);
          System.out.println("A prime number was found in thread " + fi);
          return;
        }
      }
    });
  }
  Long prime = rdv.get();
  System.out.println("I found a large prime number : " + prime);
}
```

La classe RendezVous est sensée être une classe thread-safe qui offre un méthode set permettant de proposer une valeur et une méthode get qui ''bloque'' jusqu'à ce qu'une valeur ait été proposée et la renvoie lorsque c'est le cas.

On commence avec une très mauvaise tentative dans la classe StupidRendezVous.java:
```java
/**
 * Note: this code does several stupid things !
 */
package fr.upem.concurrence.td03;

import java.util.Objects;

public class StupidRendezVous<V> {
  private V value;

  public void set(V value) {
    Objects.requireNonNull(value);
    this.value = value;
  }

  public V get() throws InterruptedException {
    while (value == null) {
      Thread.sleep(1); // then comment this line !
    }
    return value;
  }
}
```

* Que se passe-t-il lorsqu'on exécute ce code ?

**Réponse :**

Le programme se déroule sans arrêt

* Commenter l'instruction Thread.sleep(1) dans la méthode get puis ré-exécuter le code. Que se passe-t-il ? Expliquer où est le bug ?

**Réponse :**

JIT il va stocker la variable à local et il va jamais vérifier le value est null ou pas.


* Écrire une classe thread-safe RendezVous sur le même principe que la classe StupidRendezVous mais qui fonctionne correctement, que l'instruction Thread.sleep(1) soit commentée ou non.

* Regarder l'utilisation du CPU par votre programme avec la commande top. Votre code fait de l'attente active ce qui n'est pas une solution acceptable, mais vous n'avez pas les outils pour corriger cela pour l'instant. Nous verrons au prochain cours comment réaliser une méthode bloquante sans faire de l'attente active.


**Réponse :**

```java
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
```

***