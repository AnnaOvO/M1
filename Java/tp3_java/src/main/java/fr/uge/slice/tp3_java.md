## TP 3 : Slices of bread
### WANG Chen APP2

***
Vue d'un tableau, classe interne, inner class et classe anonyme
Le but de ce TP est d'écrire 4 versions différentes du concept de slice (une vue partielle d'un tableau) pour comprendre les notions de classe interne et de classe anonyme en Java.
***

* **EXERCICE 1 - Maven**

Comme pour le TP précédent, nous allons utiliser Maven avec une configuration (le pom.xml) très similaire, ici nous n'avons pas besoin du pattern matching, donc pas besoin d'activer les preview features.

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>fr.uge.slice</groupId>
    <artifactId>slice</artifactId>
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
Comme précédemment, créer un projet Maven, au niveau du premier écran, cocher create simple project puis passer à l'écran suivant en indiquant Next.
Pour ce TP, le groupId est fr.uge.slice , l'artefactId est slice et la version est 0.0.1-SNAPSHOT. Puis cliquer sur Finish.


***

* **EXERCICE 2**

Un slice est une structure de données qui permet de "virtuellement" découper un tableau en gardant des indices de début et de fin (from et to) ainsi qu'un pointeur sur le tableau. Cela évite de recopier tous les éléments du tableau, c'est donc beaucoup plus efficace.
Le concept d'array slicing est un concept très classique dans les langages de programmation, même si chaque langage vient souvent avec une implantation différente.

Les tests JUnit 5 de cet exercice sont SliceTest.java.


* **Q1**

On va dans un premier temps créer une interface Slice avec une méthode array qui permet de créer un slice à partir d'un tableau en Java.

       String[] array = new String[] { "foo", "bar" };
       Slice<String> slice = Slice.array(array);


L'interface Slice est paramétrée par le type des éléments du tableau et permet que les éléments soient null.
L'interface Slice possède deux méthodes d'instance, size qui renvoie le nombre d'éléments et get(index) qui renvoie le index-ième (à partir de zéro).
En termes d'implantation, on va créer une classe interne à l'interface Slice nommée ArraySlice implantant l'interface Slice. L'implantation ne doit pas recopier les valeurs du tableau donc un changement d'une des cases du tableau doit aussi être visible si on utilise la méthode get(index).
Implanter la classe Slice et les méthodes array, size et get(index).
Vérifier que les tests JUnit marqués "Q1" passent.

**Réponse :**

```java
package fr.uge.slice;

import java.util.Objects;

public sealed interface Slice<T> {

  public static <E> Slice<E> array(E[] lst) {

    return new ArraySlice<E>(lst);
  };
  int size();
  T get(int index); //type de T parce que c'est dans l'interface<T>

  final class ArraySlice<V> implements Slice<V> {

    private final V[] lst ;

    private ArraySlice(V[] lst) {
      Objects.requireNonNull(lst);
      this.lst = lst;
    }

    public int size(){
      //var e = this; pour voir this c'est quoi
      return this.lst.length;
    }
    public V get(int index){
      if (index < 0){
        throw new IndexOutOfBoundsException();
      }
      return this.lst[index];
    }
  }
}
```
L'interface doit être sealed et les champs dans la class interne doivent être private.

* **Q2**

On souhaite que l'affichage d'un slice affiche les valeurs séparées par des virgules avec un '[' et un ']' comme préfixe et suffixe.
Par exemple,

      var array = new String[] { "foo", "bar" };
      var slice = Slice.array(array);
      System.out.println(slice);   // [foo, bar]


En terme d'implantation, penser à utiliser un Stream avec le bon Collector !
Vérifier que les tests JUnit marqués "Q2" passent.

**Réponse :**

```java
@Override
public String toString(){
  return Arrays.stream(this.lst)
          .map(t -> t==null? "null":t.toString())
          .collect(Collectors.toList())
          .toString();
}
```
Pour l'affichage de l'array, on utilise un lambda (t -" t == null? "null":to.toString()").
Cela vérifie si les éléments dans le tableau est null ou pas. Et puis on utilise un collevtor pour les mettre
ensemble

* **Q3**

On souhaite ajouter une surcharge à la méthode array qui, en plus de prendre le tableau en paramètre, prend deux indices from et to et montre les éléments du tableau entre from inclus et to exclus.
Par exemple

       String[] array = new String[] { "foo", "bar", "baz", "whizz" };
       Slice<String> slice = Slice.array(array, 1, 3);



En terme d'implantation, on va créer une autre classe interne nommée SubArraySlice implantant l'interface Slice.
Vérifier que les tests JUnit marqués "Q3" passent.
Note : il existe une méthode Arrays.stream(array, from, to) dans la classe java.util.Arrays

**Réponse :**

```java
/*Q3 - surcharge array*/
  final class SubArraySlice<V> implements Slice<V> {
    private final V[] lst;
    private final int from;
    private final int to;

    private SubArraySlice(V[] lst, int from, int to) {
      Objects.requireNonNull(lst);
      if (from < 0){
        throw new IndexOutOfBoundsException();
      }
      if (to > lst.length){
        throw new IndexOutOfBoundsException();
      }
      if (from > to){
        throw new IndexOutOfBoundsException();
      }
      this.lst = lst;
      this.from = from;
      this.to = to;
    }

    public int size(){
      return to - from;
    }
    public V get(int index){
      if (index < 0 || index > size()){
        throw new IndexOutOfBoundsException();
      }
      return this.lst[index+from];
    }

    @Override
    public String toString(){
      return Arrays.stream(this.lst, from, to)
          .map(t -> t==null? "null":t.toString())
          .collect(Collectors.toList())
          .toString();
    }
  }
  
```
Cela est la deuxième class interne, les champs doivent être private et final. La méthode size() renvoie le length de subArray donc c'est 'to - from'
Et la même chose pour la méthode get(int index), on veut l'index de subArray donc c'est 'from + index'.Le point important de toString() c'est l'array c'est l'array origine mais pas subArray (on utilise this.array)

* **Q4**

On souhaite enfin ajouter une méthode subSlice(from, to) à l'interface Slice qui renvoie un sous-slice restreint aux valeurs entre from inclus et to exclu.
Par exemple,

        String[] array = new String[] { "foo", "bar", "baz", "whizz" };
        Slice<String> slice = Slice.array(array);
        Slice<String> slice2 = slice.subSlice(1, 3);



Bien sûr, cela veut dire implanter la méthode subSlice(from, to) dans les classes ArraySlice et SubArraySlice.
Vérifier que les tests JUnit marqués "Q4" passent.

**Réponse :**

```java
//Déclaration dans l'interface Slice
Slice<T> subSlice(int from, int to);

//subSlice dans la class ArraySlice
@Override
public Slice<V> subSlice(int from, int to) {
    return new SubArraySlice<>(lst, from, to);
}

//subSlice dans la class SubArraySlice
@Override
public Slice<V> subSlice(int from, int to) {
  if (size() < to-from){
    throw new IndexOutOfBoundsException();
  }
  if (from < 0){
    throw new IndexOutOfBoundsException();
  }
  return new SubArraySlice<V>(lst, from+this.from, this.from+to);
}
```
On dois aussi ajouter la déclaration dans l'interface Slice.

***

* **Exercice 3 - 2 Slice 2 Furious**

Le but de cet exercice est d'implanter l'interface Slice2 qui possède les mêmes méthodes que l'interface Slice mais on souhaite de la classe SubArraySlice soit une inner class de la classe ArraySlice.

Les tests JUnit 5 de cet exercice sont Slice2Test.java.

* **Q1**

Le but de cet exercice est d'implanter l'interface Slice2 qui possède les mêmes méthodes que l'interface Slice mais on souhaite de la classe SubArraySlice soit une inner class de la classe ArraySlice.

Les tests JUnit 5 de cet exercice sont Slice2Test.java.

**Réponse :**

J'ai modifié l'interface Slice2 et implenté la class ArraySlice dans Slice2.
De plus j'ai supprimé la class SubArraySlice et la méthode subSlice. De plus j'ai changé le nom d'interface à Slice2.

* **Q2**

Déclarer une classe SubArraySlice à l'intérieur de la classe ArraySlice comme une inner class donc pas comme une classe statique et implanter cette classe et la méthode array(array, from, to).
Vérifier que les tests JUnit marqués "Q3" passent. 

**Réponse :**

```java
final class SubArraySlice implements Slice2<V>{
      private final int from;
      private final int to;

      private SubArraySlice(int from, int to){
        if (from > to || to > lst.length || from < 0){
          throw new IndexOutOfBoundsException();
        }
        this.from = from;
        this.to = to;
      }
      public int size() {
        return to-from;
      }

      public V get(int index) {
        Objects.checkIndex(index, size());
        return ArraySlice.this.lst[from+index];
      }
      
      @Override
      public String toString(){
        return Arrays.stream(lst, from, to)
            .map(t -> t==null? "null":t.toString())
            .collect(Collectors.toList())
            .toString();
      }
    }
    
    public static <E> Slice2<E> array(E[] lst) {
      return new ArraySlice<E>(lst);
  };
    public static <E> Slice2<E> array(E[] lst, int from, int to) {
      var tmp = new ArraySlice<E>(lst);
      return tmp.new SubArraySlice(from, to);
    };
```

* **Q3**

Dé-commenter la méthode >subSlice(from, to) et fournissez une implantation de cette méthode dans la classe SubArraySlice.
On peut aussi noter que l'on peut simplifier le code de array(array, from, to).
Vérifier que les tests JUnit marqués "Q4" passent.

**Réponse :**
```java
@Override
public Slice2<V> subSlice(int from, int to) {
if (size() < to-from){
throw new IndexOutOfBoundsException();
}
if (from < 0){
throw new IndexOutOfBoundsException();
}
return new SubArraySlice(from+this.from, this.from+to);
}
```

* **Q4**

Dans quel cas va-t-on utiliser une inner class plutôt qu'une classe interne ?

**Réponse :**

Une classe interne peut être statique, ici dire indépendant de la classe englobante et une classe interne statique ne peut pas accéder directement aux membres non-statiques de la classe englobante.
Une inner class (classe interne non-statique) possède une référence cachée sur la classe englobante et on peut voir tous les champs de la classe globante.

***

* **Exercice 4 - The Slice and The Furious: Tokyo Drift**

Le but de cet exercice est d'implanter l'interface Slice3 qui possède les mêmes méthodes que l'interface Slice mais on va transformer les classes ArraySlice et SubArraySlice en classes anonymes.

Les tests JUnit 5 de cet exercice sont Slice3Test.java.

* **Q1**

Recopier l'interface Slice du premier exercice dans une interface Slice3. Supprimer la classe interne SubArraySlice ainsi que la méthode array(array, from, to) car nous allons les réimplanter et commenter la méthode subSlice(from, to) de l'interface, car nous allons la réimplanter plus tard.
Puis déplacer la classe ArraySlice à l'intérieur de la méthode array(array) et transformer celle-ci en classe anonyme.
Vérifier que les tests JUnit marqués "Q1" et "Q2" passent.

**Réponse :**

```java
public interface Slice3<T> {

  public static <E> Slice3<E> array(E[] lst) {
    Objects.requireNonNull(lst);
    return new Slice3<E>() { //class anonyme demande pas de l'interface sealed

      public int size(){
        return lst.length;
      }
      public E get(int index){
        if (index < 0 || index > size()){
          throw new IndexOutOfBoundsException();
        }
        return lst[index];
      }

      @Override
      public String toString(){
        return Arrays.stream(lst)
            .map(t -> t==null? "null":t.toString())
            .collect(Collectors.toList())
            .toString();
      }
    };
  };

  Slice3<T> subSlice(int from, int to);
  int size();
  T get(int index); //type de T parce que c'est dans l'interface<T>
  String toString();
```
Dans le constructor de l'interface Slice3, on return new Slice<E>() directement parce que c'est une classe anonyme.
Et on doit déclaré les méthodes dans l'interface. N'oublie pas que la class anonyme demande pas de l'interface sealed.

* **Q2**

On va maintenant chercher à implanter la méthode subSlice(from, to) directement dans l'interface Slice3 comme cela l'implantation sera partagée.
Écrire la méthode subSlice(from, to) en utilisant là encore une classe anonyme.
Comme l'implantation est dans l'interface, on a pas accès au tableau qui lui n'existe que dans l'implantation dans array(array) mais ce n'est pas gràve car on peut utiliser les méthodes de l'interface.
Puis fournissez une implantation à la méthode array(array, from, to).
Vérifier que les tests JUnit marqués "Q3" et "Q4" passent.

**Réponse :**

```java
public static <E> Slice3<E> array(E[] lst, int from, int to){
  Objects.requireNonNull(lst);
  if(from>to||to>lst.length||from< 0||to< 0){
    throw new IndexOutOfBoundsException();
  }

  return new Slice3<E>(){
    public Slice3<E> subSlice(int subFrom,int subTo){
      Objects.checkFromToIndex(subFrom,subTo,size());
      return Slice3.array(lst,from+subFrom,from+subTo);
    }

    public int size(){
      return to-from;
    }

    public E get(int index){
      if(index< 0||index>=size()){
      throw new IndexOutOfBoundsException();
      }
      return lst[index+from];
    }

    @Override
    public String toString(){
      return Arrays.stream(lst,from,to)
        .map(t->t==null?"null":t.toString())
        .collect(Collectors.toList())
        .toString();
    }
  };
}
```
On doit réécrire une méthode array(lst, int, from) en utilisant la méthode subslice.
Et modifie les méthodes pour array(lst, from, to). Voici tout les changements.

* **Q3**

Dans quel cas va-t-on utiliser une classe anonyme plutôt qu'une classe interne ?

**Réponse :**

Une classe anonyme c'est une classe qu'on peut utiliser qu'une seule fois dans la classe englobante. Comme un champ ou une méthode, une classe
interne est un membre de la classe englobante donc on peut appeler plusieur fois.


