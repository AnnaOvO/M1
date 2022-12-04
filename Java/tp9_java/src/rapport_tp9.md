## TP 9 : Trop Graph
### Chen WANG App2

Le but de ce TD est d'implanter diverses représentations des graphes orientés et de "jouer" avec les types paramétrés, les lambdas, les itérateurs et les streams.

***

**Exercice 1 - Maven**

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

        <modelVersion>4.0.0</modelVersion>
        <groupId>fr.uge.graph</groupId>
        <artifactId>graph</artifactId>
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

          <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-params</artifactId>
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

**Exercice 2 - MatrixGraph**

La classe MatrixGraph, dans le package fr.uge.graph, est une implantation par matrice d'adjacence de l'interface Graph. La structure de données est une matrice nodeCount * nodeCount telle que l'on stocke le poids d'un arc (src, dst) dans la case (src, dst).
En fait, habituellement, on ne représente pas une matrice sous forme d'un tableau à double entrée, car cela veut dire effectuer deux indirections pour trouver la valeur. On alloue un tableau à une seule dimension de taille nodeCount * nodeCount et on se balade dedans en faisant des additions et des multiplications.

* Question 1 :

  Indiquer comment trouver la case (i, j) dans un tableau à une seule dimension de taille nodeCount * nodeCount.

* **Réponse :**

  - Pour trouver la case (i, j) dans un tableau à une seule dimension de taille nodeCount * nodeCount, on peut faire (i + j * nodeCount).

***

* Question 2 :

Rappeler pourquoi, en Java, il n'est pas possible de créer des tableaux de variables de type puis implanter la classe MatrixGraph et son constructeur.
Pouvez-vous supprimer le warning à la construction ? Pourquoi?

* **Réponse :**

  - Parce que pour la comprilateur, il ne sais pas le type de T.
  - Donc il n'arrive pas à créer un new truc avec le même type que l'ancien.

```java
public class MatrixGraph<T> implements Graph<T> {
  private final int capacity;
  private final T[] graph;
  public MatrixGraph(int capacity) {
    if (capacity < 0){
      throw new IllegalArgumentException();
    }
    this.capacity = capacity;
    this.graph = (T[]) new Object[capacity*capacity];
  }
}
//Dans la class Graph on ajoute :
static Graph createMatrixGraph(int nodeCount) {
  return new MatrixGraph(nodeCount);
}
```

***

* Question 3 :

  On peut remarquer que la classe MatrixGraph n'apporte pas de nouvelles méthodes par rapport aux méthodes de l'interface Graph donc il n'est pas nécessaire que la classe MatrixGraph soit publique.
  Ajouter une méthode factory nommée createMatrixGraph dans l'interface Graph et déclarer la classe MatrixGraph non publique.

* **Réponse :**

  - On supprime le public avant la class MatrixGraph, et on change en final.
  - Maintenant la class est visible que pour le packge.

```java
final class MatrixGraph<T> implements Graph<T>;
```

***

* Question 4 :

  Afin d'implanter correctement la méthode getWeight, rappeler à quoi sert la classe java.util.Optional en Java.
  Implanter la méthode addEdge sachant que l'on ne peut pas créer un arc sans valeur.
  Implanter la méthode getWeight.

* **Réponse :**

  - Optional, qui permet d'encapsuler un objet dont la valeur peut être null.
  - Le but est de résoudre le problème des NullPointerExceptions.
  
```java

@Override
public void addEdge(int src, int dst, T weight) {
  Objects.checkIndex(src, capacity);
  Objects.checkIndex(dst, capacity);
  Objects.requireNonNull(weight);
  graph[src + dst * capacity] = weight;
}

@Override
public Optional<T> getWeight(int src, int dst) {
  Objects.checkIndex(src, capacity);
  Objects.checkIndex(dst, capacity);
  return Optional.ofNullable(graph[src + dst * capacity]);
}
```

***

* Question 5 :

  Implanter la méthode edges puis vérifier que les tests marqués "Q5" passent.

* **Réponse :**

```java
public void edges(int src, EdgeConsumer<? super T> edgeConsumer){
    Objects.checkIndex(src, capacity);
    Objects.requireNonNull(edgeConsumer);
    for (var i = 0; i < capacity; i++){
      if(getWeight(src, i).isPresent()){
        edgeConsumer.edge(src, i, getWeight(src, i).get());
      }
    }
  }
```

***

* Question 6 :

  Rappeler le fonctionnement d'un itérateur et de ses méthodes hasNext et next.
  Que renvoie next si hasNext retourne false ?
  Expliquer pourquoi il n'est pas nécessaire, dans un premier temps, d'implanter la méthode remove qui fait pourtant partie de l'interface.
  Implanter la méthode neighborsIterator(src) qui renvoie un itérateur sur tous les nœuds ayant un arc dont la source est src.
  Vérifier que les tests marqués "Q6" passent.
  Note: ça pourrait être une bonne idée de calculer quel est le prochain arc valide AVANT que l'on vous demande s'il existe.

* **Réponse :**

    -  Si hasNext retourne false next() renvoie une exception

```java
public Iterator<Integer> neighborIterator(int src){
    Objects.checkIndex(src, capacity);
    return new Iterator<Integer>() {
      int count = 0; //pour presenter dst 
      @Override
      public boolean hasNext() {
        for (; count < capacity;){
          if(getWeight(src, count).isPresent()){
            return true;
          }
          count++;
        }
        return false;
      }

      @Override
      public Integer next() {
        if (!hasNext()){
          throw new NoSuchElementException();
        }
        return count++; //renvoie count mais le pointeur continu.

      }
    };
  }
```
***

* Question 7 :

  Pourquoi le champ nodeCount ne doit pas être déclaré private avant Java 11 ?
  Est-ce qu'il y a d'autres champs qui ne doivent pas être déclarés private avant Java 11 ?

* **Réponse :**

  - Parce que sinon, comme il est utilisé dans la classe interne, la VM génère un accesseur.
  - A partir de Java 11, le .class d'un classe enternes contient un attribut NestMember qui 
    liste toutes les classe internes et les classes internes contiennent un attribut NestHost 
    qui assure un "hand check" indiquant à la VM que la sémantique de private peut être relaxée.

***

* Question 8 :

  On souhaite écrire la méthode neighborStream(src) qui renvoie un IntStream contenant tous les nœuds ayant un arc sortant par src.
  Pour créer le Stream ,nous allons utiliser StreamSupport.intStream qui prend en paramètre un Spliterator.OfInt. Rappeler ce qu'est un Spliterator, à quoi sert le OfInt et quelles sont les méthodes qu'il va falloir redéfinir.
  Écrire la méthode neighborStream sachant que l'on implantera le Spliterator en utilisant l'itérateur défini précédemment.

* **Réponse :**

  -  Un Spliterator est un objet qui compose un Stream de manière interne. 
  -  Il est un iterator qui va séparer la structure de donnée en groupe de valeurs et utiliser plusieurs itérateurs qui vont parcourir chaque groupe de valeurs.

```java
  @Override
  public IntStream neighborStream(int src) {
    Objects.checkIndex(src, nodeCount);
    var it = neighborIterator(src);
    return StreamSupport.intStream(new Spliterator.OfInt() {
      @Override
      public OfInt trySplit() {
        return null;
      }

      @Override
      public long estimateSize() {
        return Long.MAX_VALUE;
      }

      @Override
      public int characteristics() {
        return 0;
      }

      @Override
      public boolean tryAdvance(IntConsumer action) {
        if (it.hasNext()) {
          action.accept(it.next());
          return true;
        }
        return false;
      }
    }, false);
  }
```
***

* Question 9 :

  On peut remarquer que neighborStream dépend de neighborsIterator et donc pas d'une implantation spécifique. On peut donc écrire neighborStream directement dans l'interface Graph comme ça le code sera partagé.
  Rappeler comment on fait pour avoir une méthode 'instance avec du code dans une interface.
  Déplacer neighborStream dans Graph et vérifier que les tests unitaires passent toujours.



***

* Question 10 :

  Expliquer le fonctionnement précis de la méthode remove de l'interface Iterator.
  Implanter la méthode remove de l'itérateur.

* **Réponse :**

```java

```

***

* Question 11 :

  On peut remarquer que l'on peut ré-écrire edges en utilisant neighborsStream, en une ligne :) et donc déplacer edges dans Graph.
  Déplacer le code de la méthode edges dans Graph.

* **Réponse :**

```java

```

***

**Exercice 3 - NodeMapGraph (à la maison)**

On souhaite fournir une implantation de l'interface Graph par table de hachage qui pour chaque nœud permet de stocker l'ensemble des arcs sortant. Pour un nœud donné, on utilise une table de hachage qui a un nœud destination associe le poids de l'arc. Si un nœud destination n'est pas dans la table de hachage cela veut dire qu'il n'y a pas d'arc entre le nœud source et le nœud destination.
Le graphe est représenté par un tableau dont chaque case correspond à un nœud, donc chaque case contient une table de hachage qui associe à un nœud destination le poids de l'arc correspondant.

Les tests unitaires sont les mêmes que précédemment car NodeMapGraph est une autre implantation de l'interface Graph, il suffit de dé-commenter la méthode référence dans graphFactoryProvider.

* Question 1:

  Écrire dans l'interface Graph la méthode createNodeMapGraph et implanter la classe NodeMapGraph (toujours non publique).
  Note: chaque méthode est sensée ne pas prendre plus de 2 ou 3 lignes, tests des préconditions compris.

* **Réponse :**

```java

```

***

