## TP 10 : Structure de données spécialisée pour les types primitifs
### Chen WANG App2

Le but de ce TP noté est d'implanter une structure de données appelée NumericVec qui est une séquence de valeurs numériques d'entiers, d'entiers long ou de nombres flottants.
L'intérêt de cette structure de données est d'avoir une représentation très compacte en mémoire des données sans pour autant dupliquer le code pour chaque type primitif.

***

**Exercice 1 - Maven**

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>fr.uge.numeric</groupId>
    <artifactId>numeric</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
      <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
      <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.9.0</version>
        <scope>test</scope>
      </dependency>
    </dependencies>

    <build>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.10.1</version>
          <configuration>
            <release>19</release>
          </configuration>
        </plugin>

        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>3.0.0-M7</version>
        </plugin>
      </plugins>
    </build>
  </project>
```
***

**Exercice 2 - NumericVec**

La classe fr.uge.numeric.NumericVec est un conteneur de valeurs numériques homogènes de type int, long ou double mais qui en interne stocke les valeurs dans un tableau de long.
En effet, un entier long 64 bits (long) peut contenir un int 32 bits ; il suffit de faire cast en int si on veut obtenir la valeur comme un int. Un entier long 64 bits (long) peut aussi contenir un nombre flottant 64 bits (double) car c'est la même taille en mémoire, il faut être capable pour cela de voir un double comme un long sans changer la représentation bit à bit. La méthode Double.doubleToRawLongBits(double valeur) renvoie le long correspondant bit à bit au double passé en paramètre et la méthode Double.longBitsToDouble(long value) fait l'opération inverse.
La classe NumericVec permet de créer des séquences d'int, de long ou de double de la façon suivante.

    NumericVec<Integer> seq1 = NumericVec.ints(1, 4, 7);  // séquence d'entiers 32 bits
    NumericVec<Long> seq2 = NumericVec.longs(123L, -45L);  // séquence d'entiers 64 bits
    NumericVec<Double> seq3 = NumericVec.doubles(48.0, 123.25);  // séquence de flottants 64 bits


La classe NumericVec possède, de plus,

les méthodes add(value), get(index) et size qui permettent respectivement d'ajouter un élément, d'obtenir un élément à un index donné et d'obtenir le nombre de valeurs dans la séquence.
un mécanisme qui permet d'afficher une séquence : les valeurs sont séparées par des virgules suivies d'un espace, et le tout entre '[' et ']'

      var seq1 = NumericVec.ints(1, 4, 7);
      System.out.println(seq1);   // affiche [1, 4, 7]
           

un mécanisme qui permet de parcourir la séquence avec une boucle, comme dans l'exemple ci-dessous

      var seq1 = NumericVec.ints(1, 4, 7);
      for(var value: seq1) {
        System.out.println("value: " + value);
      }
           

une méthode addAll(anotherSequence) qui permet d'ajouter les valeurs d'une autre séquence à la séquence courante si elles ont le même type de valeurs numériques 
une méthode map(function, factory) qui renvoie une nouvelle séquence créée en utlisant la méthode de création factory et qui contient les valeurs de retour de l'appel à la fonction prise en premier paramètre pour chaque valeur de la séquence courante.

      var seq1 = NumericVec.ints(1, 4, 7);
      var seq4 = seq1.map(v -> v * 2.5, NumericVec::double);
      System.out.println(seq4);  // affiche [2.5, 10.0, 17.5]
           

les méthodes toNumericVec(factory) et stream(), qui permettent respectivement de collecter des valeurs dans un NumericVec et de renvoyer un Stream.


* Question 1 :

  Dans la classe fr.uge.numeric.NumericVec, on souhaite écrire une méthode longs sans paramètre qui permet de créer un NumericVec vide ayant pour l'instant une capacité fixe de 16 valeurs. Cela doit être la seule façon de pouvoir créer un NumericVec.
  Écrire la méthode longs puis ajouter les méthodes add(value), get(index) et size.

* **Réponse :**

```java

public class NumericVec<T> {
  private final long[] numericVec;
  private int size = 0;

  private NumericVec() {
    this.numericVec = new long[16];
  }

  public static NumericVec<Long> longs() {
    return new NumericVec<>();
  }
  public void add(Long l){
    Objects.requireNonNull(l);
    numericVec[size] = l;
    size++;
  }

  public int size() {
    return size;
  }

  public long get(int i) {
    Objects.checkIndex(i, size);
    return numericVec[i];
  }
}
```
***

* Question 2 :

  On veut maintenant que le tableau utilisé par NumericVec puisse s'agrandir pour permettre d'ajouter un nombre arbitraire de valeurs.
  On veut, de plus, que NumericVec soit économe en mémoire, donc la capacité du tableau d'un NumericVec vide doit être 0 (si vous n'y arrivez pas, faites la suite).
  Vérifier que les tests unitaires marqués "Q2" passent.
  Note: agrandir un tableau une case par une case est très inefficace !

* **Réponse :**

  - On peut modifier la méthode add(Long l) pour agrandir notre tableau avec la méthode Arrays.copyOf(oldArray, lengthNewArray).
  - N'oublie pas de mettre l'array(numericVec) non final.

```java
public void add(Long l){
    Objects.requireNonNull(l);
    if (size == numericVec.length){
      numericVec = Arrays.copyOf(numericVec, numericVec.length *2);
    }
    numericVec[size] = l;
    size++;
  }
```

***

* Question 3 :

  Faire en sorte d'utiliser un Stream pour que l'on puisse afficher un NumericVec avec le format attendu.

* **Réponse :**

  - On demande le stream de les "size" éléments, donc on peut faire un limit(size) pour afficher les éléments dans notre tableau.

```java
public String toString(){
  return Arrays.stream(numericVec).limit(size)
        .mapToObj(Objects::toString)
        .collect(Collectors.joining(", ","[","]"));
}
```

***

* Question 4 :

  On veut maintenant ajouter les 3 méthodes ints, longs et doubles qui permettent respectivement de créer des NumericVec d'int, de long ou de double en prenant en paramètre des valeurs séparées par des virgules.
  En termes d'implantation, l'idée est de convertir les int ou les double en long avant de les insérer dans le tableau. 
  Et dans l'autre sens, lorsque l'on veut lire une valeur, c'est-à-dire quand on prend un long dans le tableau, on le convertit en le type numérique attendu. Pour cela, l'idée est de stocker dans chaque NumericVec une fonction into qui sait convertir une valeur en long, et une fonction from qui sait convertir un long vers la valeur attendue.

* **Réponse :**

```java
public class NumericVec<T> {
  private long[] numericVec;
  private int size = 0;
  private final Function<Long, T> from;
  private final Function<T, Long> into;

  @SuppressWarnings("unchecked")
  private NumericVec() {
    this.numericVec = new long[16];
    into = e -> (Long) e;
    from = e -> (T) e;
  }
  private NumericVec(Function<T, Long> into, Function<Long, T> from) {
    //on ajoute 2 parametres
    this.numericVec = new long[16];
    this.into = into;
    this.from = from;
  }

  public static NumericVec<Long> longs() {
    return new NumericVec<>();
  }

  public void add(T l){
    Objects.requireNonNull(l);
    if (size == numericVec.length){
      numericVec = Arrays.copyOf(numericVec, numericVec.length *2);
    }
    numericVec[size] = into.apply(l);
    size++;
  }

  public int size() {
    return size;
  }

  public T get(int i) {
    Objects.checkIndex(i, size);
    return from.apply(numericVec[i]);
  }

  public static NumericVec<Long> longs(long... i) {
    var newTabInt = new NumericVec<>(e -> e, e -> e);
    for (var value : i){
      newTabInt.add(value);
    }
    return newTabInt;
  }

  public static NumericVec<Double> doubles(double... i) {
    var newTabInt = new NumericVec<>(Double::doubleToRawLongBits, Double::longBitsToDouble);
    for (var value : i){
      newTabInt.add(value);
    }
    return newTabInt;
  }

  public static NumericVec<Integer> ints(int... i) {
    var newTabInt = new NumericVec<>(e -> (long) e, e -> Integer.parseInt(String.valueOf(e)));
    for (var value : i){
      newTabInt.add(value);
    }
    return newTabInt;
  }
  public String toString(){
    return Arrays.stream(numericVec)
        .limit(size).mapToObj(e -> from.apply(e).toString())
        .collect(Collectors.joining(", ","[","]"));
  }
}
```

***

* Question 5 :

  On souhaite maintenant pouvoir parcourir un NumericVec avec une boucle for(:). Dans le cas où l'on modifie un NumericVec avec la méthode add lors de l'itération, les valeurs ajoutées ne sont pas visibles pour la boucle.
  Modifier la classe NumericVec pour implanter le support de la boucle for(:).

* **Réponse :**

```java
public class NumericVec<T> implements Iterable<T>{

  @Override
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int i = 0;
      private final int sizeI = size;
      @Override
      public boolean hasNext() {
        return i < sizeI;
      }
      @Override
      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return get(i++);
      }
    };
  }
}
```

***

* Question 6 :

  On souhaite ajouter une méthode addAll qui permet d'ajouter un NumericVec à un NumericVec déjà existant.
  Ecrire le code de la méthode addAll.
  Vérifier que les tests unitaires marqués "Q6" passent.
  Note: on peut remarquer qu'il y a une implantation efficace car les deux NumericVec utilisent en interne des tableaux de long.

* **Réponse :**

```java
public void addAll(NumericVec<? extends T> seq) {
  Objects.requireNonNull(seq);
  for (int i = 0; i < seq.size; i++) {
    this.add(seq.get(i));
  }
}
```
***

* Question 7 :

  On souhaite maintenant écrire une méthode map(function, factory) qui prend en paramètre une fonction qui peut prendre en paramètre un élément du NumericVec et renvoie une nouvelle valeur ainsi qu' une référence de méthode permettant de créer un nouveau NumericVec qui contiendra les valeurs renvoyées par la fonction.
  Écrire la méthode map.

* **Réponse :**
```java
public <E> NumericVec<E> map(Function<? super T, E> function, Supplier<? extends NumericVec<E>> factory) {
    Objects.requireNonNull(function);
    Objects.requireNonNull(factory);
    var ret = factory.get();
    Arrays.stream(numericVec, 0, size)
        .mapToObj(o -> from.andThen(function).apply(o)).forEach(ret::add);
    return ret;
  }
```
***

* Question 8 :

  On souhaite écrire une méthode toNumericVec(factory) qui prend en paramètre une référence de méthode permettant de créer un NumericVec et renvoie un Collector qui peut être utilisé pour collecter des valeurs numériques dans un/des NumericVec créés par la référence de méthode.
  Écrire la méthode toNumericVec.

* **Réponse :**

```java
public static <T> Collector<T, ?, NumericVec<T>> toNumericVec(Supplier<NumericVec<T>> factory) {
    return Collector.of(factory, NumericVec::add, (n1, n2) -> { n1.addAll(n2); return n1; });
  }
```

***

* Question 9:

  Enfin, écrire une méthode stream() qui renvoie un Stream qui voit l'ensemble des valeurs par ordre d'insertion dans le NumericVec courant.
  Le Stream renvoyé devra être parallélisable.
  Vérifier que les tests unitaires marqués "Q9" passent.
  Note : pour que le Stream parallèle soit un peu efficace, on ne coupera pas en deux les Stream ayant moins de 1024 valeurs.

* **Réponse :**

```java

  private Spliterator<T> fromNumeric(int start, int end) {
    return new Spliterator<>() {
      int index = start;
      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        if (index == end) {
          return false;
        }
        action.accept(get(index++));
        return true;
      }
      @Override
      public Spliterator<T> trySplit() {
        var middle = (end + index) >>> 1;
        if (middle == index || end - start < 1024) {
          return null;
        }
        var split = fromNumeric(index, middle);
        index = middle;
        return split;
      }

      @Override
      public long estimateSize() {
        return end - start;
      }

      @Override
      public int characteristics() {
        return SIZED | SUBSIZED | NONNULL | ORDERED | IMMUTABLE;
      }
    };
  }

  public Stream<T> stream() {
    return StreamSupport.stream(fromNumeric(0, size), size >= 1024);
  }
```
***

