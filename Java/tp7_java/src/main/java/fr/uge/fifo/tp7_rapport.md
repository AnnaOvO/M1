## TP 7 : Faites la queue
### Chen WANG App2

Le but de ce TD est d'implanter une file d'attente en utilisant un tableau de façon circulaire et de jouer un peu avec les types paramétrés.

***

**Exercice 1 - Maven**

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>fr.uge.fifo</groupId>
    <artifactId>fifo</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>19</maven.compiler.source>
        <maven.compiler.target>19</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
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
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
```
***

**Exercice 2 - Fifo**

On souhaite écrire une structure de données pour représenter un file d'attente (first-in first-out ou FIFO) utilisant un tableau circulaire qui aura, au moins au début du TP, une taille fixée à la création.
Les deux opérations principales sont offer qui permet d'insérer un élément à la fin de la file et poll qui permet de retirer un élément en début de la file.
L'idée est d'utiliser deux indices, un correspondant à la tête de la file pour retirer les éléments et un correspondant à la queue de la file pour ajouter des éléments. Lorsque l'on insérera un élément, on incrémentera la queue de la file, lorsque l'on retirera un élément, on incrémentera la tête de la file.
De plus, il est impossible de stocker null dans la file.

* Question 1 :

  Cette représentation peut poser problème, car si la tête et la queue correspondent au même indice, il n'est pas facile de détecter si cela veux dire que la file est pleine ou vide.
  Comment doit-on faire pour détecter si la file est pleine ou vide ?
  Cette question a plusieurs réponses possibles :).

* **Réponse :**

    - Ici on crée un int capacity c'est la capacité de l'array, et on crée un int size pour compter les éléments dans l'array.
    - Si size = capacity, alors la file est pleine, si size = 0 alors la file est vide.

```java

```

***

* Question 2 :

Écrire une classe Fifo générique (avec une variable de type E) dans le package fr.uge.fifo prenant en paramètre le nombre maximal d’éléments que peut stocker la structure de données. Pensez à vérifier les préconditions.

* **Réponse :**

```java
public class Fifo<E> {
  private final ArrayList<E> array = new ArrayList<E>();
  private int capacity;
  private int size = 0;

  public Fifo(int capacity){
    if (capacity <= 0){
      throw new IllegalArgumentException();
    }
    this.capacity = capacity;
  }
}
```

***

* Question 3 :

  Écrire la méthode offer qui ajoute un élément de type E dans la file. Pensez à vérifier les préconditions sachant que, notamment, on veut interdire le stockage de null.
  Comment détecter que la file est pleine ?
  Que faire si la file est pleine ?

* **Réponse :**

    - On peut comparer size de notre array et la capacity de l'array.
    - Il y aura une erreur si la file est pleine, on peut faire **throw new IllegalStateException()**

```java
public void offer(E value) {
  Objects.requireNonNull(value);
  if (capacity <= 0 || size >= capacity){
    throw new IllegalStateException();
  }
  array.add(value);
  size++;
}

```

***

* Question 4 :

  Écrire une méthode **poll** qui retire un élément de type E de la file. Penser à vérifier les préconditions.
  Que faire si la file est vide ?

* **Réponse :**

    - Si la file est vide, on ne peut pas poll elements. Il y aura une erreur, on peut faire **throw new IllegalStateException()**

```java
public E poll() {
  if (size <= 0){
    throw new IllegalStateException();
  }
  var e = array.remove(0);
  size--;
  return e;
}
```

***

* Question 5 :

  Ajouter une méthode d'affichage qui affiche les éléments dans l'ordre dans lequel ils seraient sortis en utilisant poll. L'ensemble des éléments devra être affiché entre crochets ('[' et ']') avec les éléments séparés par des virgules (suivies d'un espace).
  Note : attention à bien faire la différence entre la file pleine et la file vide.
  Note 2 : Il existe une classe StringJoiner qui est ici plus pratique à utiliser qu'un StringBuilder !
  Indication : Vous avez le droit d'utiliser 2 compteurs.

* **Réponse :**

```java
  
```

***

* Question 6 :

  Rappelez ce qu'est un memory leak en Java et assurez-vous que votre implantation n'a pas ce comportement indésirable.

* **Réponse :**

```java
  
```

***

* Question 7 :

  Ajouter une méthode size et une méthode isEmpty.

* **Réponse :**

  - 

```java
  
```

***

* Question 8 :

  Rappelez quel est le principe d'un itérateur.
  Quel doit être le type de retour de la méthode iterator() ?

* **Réponse :**

  -

```java
  
```

***


* Question 9 :

  Implanter la méthode iterator().
  Note : ici, pour simplifier le problème, on considérera que l'itérateur ne peut pas supprimer des éléments pendant son parcours.

* **Réponse :**

  -

```java
  
```

***

* Question 10 :

  Rappeler à quoi sert l'interface Iterable.
  Faire en sorte que votre file soit Iterable.

* **Réponse :**

  -

```java
  
```

***

**Exercice 3 - ResizeableFifo**

On souhaite maintenant que le tableau circulaire de la file puisse s'agrandir si jamais il n'y a plus assez de place.
On va garder un entier pour le constructeur qui au lieu d'indiquer la taille va indiquer la capacité initial de la file (la taille avant un possible agrandissement).

* Question 1 :

  Indiquer comment agrandir la file si celle-ci est pleine et que l'on veut doubler sa taille. Attention, il faut penser au cas où le début de la liste a un indice qui est supérieur à l'indice indiquant la fin de la file.
  Implanter la solution retenue dans une nouvelle classe ResizeableFifo.
  Note: il existe les méthodes **Arrays.copyOf** et **System.arraycopy**.

* **Réponse :**

    - 

```java


```

***

* Question 2 :

  En fait, il existe déjà une interface pour les files dans le JDK appelée java.util.Queue.
  Sachant qu'il existe une classe AbstractQueue qui fournit déjà des implantations par défaut de l'interface Queue indiquer

  1. quelles sont les méthodes supplémentaires à implanter;
  2. quelles sont les méthodes dont l'implantation doit être modifiée;
  3. quelles sont les méthodes que l'on peut supprimer.
  
  Faire en sorte que la classe ResizableFifo implante l'interface Queue.


* **Réponse :**

    - 
    - 
    - 

```java

```

