## TP 10 : Structure de données persistante (fonctionnelle)
### Chen WANG App2

Le but de ce TP est de bien faire la différence entre les informations exposées publiquement par une classe et les informations utilisées par celle-ci en interne.
Nous allons implanter une classe fr.uge.seq.Seq qui représente une séquence (comme une liste) paresseuse non mutable d'éléments non null. Les éléments sont stockés en interne dans une liste non mutable.

***

**Exercice 1 - Maven**

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

          <modelVersion>4.0.0</modelVersion>
          <groupId>fr.uge.seq</groupId>
          <artifactId>seq</artifactId>
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

**Exercice 2 - Seq**

La classe fr.uge.seq.Seq est paramétrée par le type des éléments qu'elle contient.

La classe Seq est munie des opérations initiales suivantes :

from qui prend une liste en paramètre (une java.util.List) et créé un objet Seq contenant les éléments de la liste (dans le même ordre);
get qui permet d'obtenir le n-ième élément d'un Seq (avec une comnplexité en O(1), bien sûr);
size qui indique le nombre d'éléments dans la séquence (en O(1), toujours).
Note : comme la séquence est non mutable, le nombre d'éléments de la séquence ne change pas.

* Question 1 :

  Écrire le code de la classe Seq dans le package fr.uge.seq.

* **Réponse :**

  - La méthode from() est static donc l'object de from n'est pas obligé d'être le même type.

```java

import java.util.List;
import java.util.Objects;

public class Seq<T> {
  private final List<T> seq;
  public Seq(List<? extends T> seq) {
    this.seq = List.copyOf(seq);
  }

  public static <E>Seq<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq<>(lst);
  }

  public int size() {
    return seq.size();
  }

  public T get(int index) {
    Objects.checkIndex(index, seq.size());
    return seq.get(index);
  }
}
```
***

* Question 2 :

  Écrire une méthode d'affichage permettant d'afficher les valeurs d'un Seq séparées par des virgules (suivies d'un espace), l'ensemble des valeurs étant encadré par des chevrons ('<' et '>').

* **Réponse :**

    - On utilise StringJoiner(delimiter, prefix, suffix).
  
```java
public String toString(){
    var s = new StringJoiner(", ", "<",">");
    for (var i : seq){
      s.add(i.toString());
    }
    return s.toString();
}
```

***

* Question 3 :

  Écrire une méthode of permettant d'initialiser un Seq à partir de valeurs séparées par des virgules.
    Note : si vous avez des warnings, vous avez un problème.
    Note 2 : si vous pensez un @SuppressWarnings, pensez plus fort !

* **Réponse :**

    - Varargs : E... c'est à dire on peut mettre n'importe quel type et n'import quel nombre d'éléments qu'on veut.
    - N'oublie pas **@SafeVarargs** pour éviter des warnings.
```java
@SafeVarargs
public static <E>Seq<E> of(E... lst) {
        Objects.requireNonNull(lst);
        return new Seq<>(List.of(lst));
        }
```

***

* Question 4 :

  Écrire une méthode forEach qui prend en paramètre une fonction qui prend en paramètre chaque élément un par un et fait un effet de bord.

* **Réponse :**

```java
public void forEach(Consumer<? super T> f) {
  Objects.requireNonNull(f);
  seq.forEach(f);
}
```

***

* Question 5 :

  On souhaite écrire une méthode map qui prend en paramètre une fonction à appliquer à chaque élément d'un Seq pour créer un nouveau Seq. On souhaite avoir une implantation paresseuse, c'est-à-dire une implantation qui ne fait pas de calcul si ce n'est pas nécessaire. Par exemple, tant que personne n'accède à un élément du nouveau Seq, il n'est pas nécessaire d'appliquer la fonction. L'idée est de stoker les anciens éléments ainsi que la fonction et de l'appliquer seulement si c'est nécessaire.
  Bien sûr, cela va nous obliger à changer l'implantation déjà existante de Seq car maintenant tous les Seq vont stocker une liste d'éléments ainsi qu'une fonction de transformation (de mapping).
  Exemple d'utilisation

        var seq2 = seq.map(String::valueOf); // String.valueOf() est pas appelée
        System.out.println(seq2.get(0));     // "78", String.valueOf a été appelée 1 fois
                                             // car on demande explicitement la valeur



  Avant de se lancer dans l'implantation de map, quelle doit être sa signature ?
  Quel doit être le type des éléments de la liste ? Et le type de la fonction stockée ?
  Faire les modifications correspondantes, puis changer le code des méthodes pour les prendre en compte. Enfin, écrire le code de map.
  Note : le code doit fonctionner si l'on appelle map deux fois successivement.

* **Réponse :**

  - Sa signature est public void forEach(Consumer<? super T> f).
  - Le type des éléments de la liste est List<?>, et le type de la fonction stockée est Function<Object, ? extends T>.
  - J'ai changé List<? extends T> à List<?>, et j'ai modifié les méthodes get, forEach, map et toString pour appliquer la fonction dans le champs.
  - J'ai aussi modifié la visibilité de notre constructeur Seq (public -> private) et je rajoute un constructeur Seq(List<?> seq, Function<Object, ? extends T> function).

```java

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Seq<T> {
  //private final List<T> seq;
  private final List<?> seq; //Q5
  private final Function<Object, ? extends T> function;

  @SuppressWarnings("unchecked")
  private Seq(List<?> seq) {
    this.seq = List.copyOf(seq);
    this.function = x -> (T) x;
  }
  private Seq(List<?> seq, Function<Object, ? extends T> function) {
    this.seq = List.copyOf(seq);
    this.function = function;
  }

  public static <E>Seq<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq<>(lst);
  }

  public int size() {
    return seq.size();
  }

  public T get(int index) {
    Objects.checkIndex(index, seq.size());
    return function.apply(seq.get(index));
  }

  public String toString(){
    return seq.stream().map(function).map(Object::toString).collect(Collectors.joining(", ", "<", ">"));
  }

  @SafeVarargs
  public static <E>Seq<E> of(E... lst) {
    Objects.requireNonNull(lst);
    return new Seq<>(List.of(lst));
  }

  public void forEach(Consumer<? super T> f) {
    Objects.requireNonNull(f);
    seq.stream().map(function).forEach(f);
  }

  public <E>Seq<E> map(Function<? super T, ? extends E> f) {
    Objects.requireNonNull(f);
    return new Seq<>(seq, function.andThen(f));
  }
}
```

***

* Question 6 :

  Écrire une méthode findFirst qui renvoie le premier élément du Seq si celui-ci existe.

* **Réponse :**

  - Optional c'est quelque chose qui nous permet de dire j'ai peut être une valeur mais peut être pas
    Donc on a monOptional.isPresent() et monOptional.isEmpty() pour tester s'il est empty ou pas
    si il est pas empty -> monOptional.get()
  - Optional.of(value) nous permet de transformer une valeur en optional donc on peut faire
    Optional.of(seq.get(0)), et on veut pas que le programme s'arrête s'il y'a pas de first
    On veut dire qu'il y'a rien et donc il y'a Optional.empty()

```java
public Optional<T> findFirst() {
  return (seq.size() > 0) ? Optional.of(get(0)) : Optional.empty();
}
```
***

* Question 7 :

  Faire en sorte que l'on puisse utiliser la boucle for-each-in sur un Seq

* **Réponse :**
  
  - N'oublie pas de implements Iterable<T> dans la class Seq<T> pour que ce soit parcourable et indique qu'on veut parcourir des T.

```java
public class Seq<T> implements Iterable<T> { //N'oublie pas de implements Iterable<T> ici
  
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < seq.size();
      }

      @Override
      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        //Ici get(current) renvoie l'element de courant.
        return get(current++);
      }
    };
  }
}
```
***

* Question 8 :

  Enfin, on souhaite implanter la méthode stream() qui renvoie un Stream des éléments du Seq. Pour cela, on va commencer par implanter un Spliterator que l'on peut construire à partir du Spliterator déjà existant de la liste (que l'on obtient avec la méthode List.spliterator()).
  Puis en utilisant la méthode StreamSupport.stream, créer un Stream à partir de ce Spliterator.
  Écrire la méthode stream().

* **Réponse :**

```java
private Spliterator<T> fromSplit(Spliterator<?> spliterator) {
  return new Spliterator<>() {
    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
      Objects.requireNonNull(action);
      return spliterator.tryAdvance(e -> action.accept(function.apply(e)));
    }

    @Override
    public Spliterator<T> trySplit() {
      var split = spliterator.trySplit();
      if (split == null) {
        return null;
      }
      return fromSplit(split);
    }

    @Override
    public long estimateSize() {
      return spliterator.estimateSize();
    }

    @Override
    public int characteristics() {
      return spliterator().characteristics() | IMMUTABLE | ORDERED | NONNULL | SIZED | SUBSIZED;
    }
  };
}

public Stream<T> stream() {   
  var spliterator = seq.spliterator();
  return StreamSupport.stream(fromSplit(spliterator), false);
}
```

***

**Exercice 3 - Seq2 le retour (à la maison)**

En fait, l'implantation à base de liste de l'exercice précédent n'est pas très efficace en mémoire. Il vaut mieux stocker les éléments dans un tableau car c'est de toute façon le stockage utilisé par la liste (pas complètement vrai si vous utilisez List.of(), on vous laisse trouver pourquoi).
Les tests unitaires JUnit 5 correspondant à l'implantation sont ici: Seq2Test.java.

* Question 1:

  Ré-implanter toutes les méthodes publiques de Seq dans une classe Seq2 en utilisant un tableau d'éléments en interne.

* **Réponse :**

```java
package fr.uge.seq;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Seq2<T> implements Iterable<T> {
  private final T[] seq;
  private final Function<Object, ? extends T> function;

  @SuppressWarnings("unchecked")
  private Seq2(List<?> seq) {
    this.seq = (T[]) List.copyOf(seq).toArray();
    this.function = x -> (T) x;
  }
  private Seq2(List<?> seq, Function<Object, ? extends T> function) {
    Objects.requireNonNull(seq);
    Objects.requireNonNull(function);
    this.seq = (T[]) List.copyOf(seq).toArray();
    this.function = function;
  }

  public static <E>Seq2<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq2<>(lst);
  }

  public int size() {
    return seq.length;
  }

  public T get(int index) {
    Objects.checkIndex(index, seq.length);
    return function.apply(seq[index]);
  }

  public String toString(){
    return Arrays.stream(seq).map(function)
        .map(Object::toString)
        .collect(Collectors.joining(", ", "<", ">"));
  }

  @SafeVarargs
  public static <E>Seq2<E> of(E... lst) {
    Objects.requireNonNull(lst);
    return new Seq2<>(List.of(lst));
  }
  public void forEach(Consumer<? super T> f) {
    Objects.requireNonNull(f);
    Arrays.stream(seq).forEach(e -> f.accept(function.apply(e)));
  }

  public <E>Seq2<E> map(Function<? super T, ? extends E> f) {
    Objects.requireNonNull(f);
    return new Seq2<>(Arrays.asList(seq), function.andThen(f));
  }

  public Optional<T> findFirst() {
    return (seq.length > 0) ? Optional.of(get(0)) : Optional.empty();
  }

  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int current = 0;
      @Override
      public boolean hasNext() {
        return current < seq.length;
      }

      @Override
      public T next() {
        if(!hasNext()){
          throw new NoSuchElementException();
        }
        return get(current++);
      }
    };
  }

  private Spliterator<T> split(int start, int end) {
    return new Spliterator<>() {
      private int tmp = start;
      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if (tmp >= end) {
          return false;
        }
        action.accept(get(tmp++));
        return true;
      }

      @Override
      public Spliterator<T> trySplit() {
        var middle = (end + tmp) >>> 1;
        if (middle == tmp) {
          return null;
        }
        var s = split(tmp, middle);
        tmp = middle;
        return s;
      }

      @Override
      public long estimateSize() {
        return end - tmp;
      }

      @Override
      public int characteristics() {
        return IMMUTABLE | ORDERED | NONNULL | SIZED | SUBSIZED;
      }
    };
  }
  public Stream<T> stream() {
    return StreamSupport.stream(split(0, size()), false);
  }
}

```
***

