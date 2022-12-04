## TP 8 : TimeSeries
### Chen WANG App2

Generics, classe interne, lambdas, listes et iterator

***

**Exercice 1 - Maven**

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>fr.uge.series</groupId>
    <artifactId>series</artifactId>
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

**Exercice 2 - TimeSeries**

Le but de cet exercice est d'implanter une structure de données dite time-series. Une structure de données time-series est une liste de valeurs. Chaque valeur possède aussi un marqueur temporel (un timestamp) associé.

* Question 1 :

  Dans un premier temps, on va créer une classe TimeSeries ainsi qu'un record Data à l'intérieur de la classe TimeSeries qui représente une paire contenant une valeur de temps (timestamp) et un élément (element).
  Le record Data est paramétré par le type de l'élément qu'il contient.
  Écrire la classe TimeSeries dans le package fr.uge.serie, ainsi que le record interne public Data et vérifier que les tests marqués "Q1" passent.

* **Réponse :**

```java
public class TimeSeries<T> {
  record Data<T>(long timestamp, T element) {
    Data {
      Objects.requireNonNull(element);
    }
  }
}
```

***

* Question 2 :

On souhaite maintenant écrire les méthodes dans TimeSeries :

 - add(timestamp, element) qui permet d'ajouter un élément avec son timestamp. La valeur de timestamp doit toujours être supérieure ou égale à la valeur du timestamp précédemment inséré (s'il existe).
 - size qui renvoie le nombre d'éléments ajoutés.
 - get(index) qui renvoie l'objet Data se trouvant à la position indiquée par l'index (de 0 à size - 1).

En interne, la classe TimeSeries stocke des instances de Data dans une liste qui s'agrandit dynamiquement.
Écrire les 3 méthodes définies ci-dessus et vérifier que les tests marqués "Q2" passent.

* **Réponse :**

```java
  private final ArrayList timeSeries = new ArrayList<Data>();
  private long lastTimestamp = Long.MIN_VALUE;
  private int size = 0;

  public void add(long timestamp, T element) {
    if (lastTimestamp > timestamp){
      throw new IllegalStateException();
    }
    if (lastTimestamp < timestamp){
      var data = new Data<T>(timestamp, element);
      timeSeries.add(data);
      lastTimestamp = data.timestamp();
      size++;
    }
  }

  public int size() {
    return size;
  }

  public Object get(int index) {
    //if (index < 0 || index > size()){ throw new IndexOutOfBoundsException(); }
    Objects.checkIndex(index, size());
    return timeSeries.get(index);
  }
```

***

* Question 3 :

  On souhaite maintenant créer une classe interne publique Index ainsi qu'une méthode index permettant de créer un Index stockant les indices des données de la TimeSeries sur laquelle la méthode index est appelée. L'objectif est de pouvoir ensuite accéder aux Data correspondantes dans le TimeSeries. Un Index possède une méthode size indiquant combien d'indices il contient.
  Seuls les indices des éléments ajoutés avant l'appel à la méthode index() doivent être présents dans l'Index.
  En interne, un Index stocke un tableau d'entiers correspondants à chaque indice.
  Écrire la méthode index et vérifier que les tests marqués "Q3" passent.
  Indication : Instream.range() permet de créer un Stream d'entiers

* **Réponse :**

```java
public Index index() {
  return new Index(IntStream.range(0, size()).toArray());
}

class Index{
  public final int[] indexs;

  private Index(int[] array) {
    indexs = array;
  }

  public int size() {
    return indexs.length;
  }
}
```

***

* Question 4 :

  On souhaite pouvoir afficher un Index, c'est-à-dire afficher les éléments (avec le timestamp) référencés par un Index, un par ligne avec un pipe (|) entre le timestamp et l'élément.
  Faites les changements qui s'imposent dans la classe Index et vérifier que les tests marqués "Q4" passent.

* **Réponse :**

  - J'ai ajouté un toString dans record Data, et on ajoute le toString à class Index
```java
record Data<T>(long timestamp, T element){
  Data{
    Objects.requireNonNull(element);
  }

  @Override
  public String toString() {
    return timestamp + " | " + element;
  }
}
class Index{
  ...
  @Override
  public String toString(){
    return Arrays.stream(indexs).mapToObj(e -> timeSeries.get(e).toString()).collect(Collectors.joining("\n"));
  }
}
```

***

* Question 5 :

  On souhaite ajouter une autre méthode index(lambda) qui prend en paramètre une fonction/lambda qui est appelée sur chaque élément de la TimeSeries et indique si l'élément doit ou non faire partie de l'index.
  Par exemple, avec une TimeSeries contenant les éléments "hello", "time" et "series" et une lambda s -> s.charAt(1) == 'e' qui renvoie vrai si le deuxième caractère est un 'e', l'Index renvoyé contient [0, 2].
  Quel doit être le type du paramètre de la méthode index(lambda) ?
  Écrire la méthode index(filter) et vérifier que les tests marqués "Q5" passent.
  Note : On peut remarquer qu'il est possible de ré-écrire la méthode index sans paramètre pour utiliser celle avec un paramètre.

* **Réponse :**

    - Ça doit être un Predicate parce qu'il prend un element et renvoie un boolean, et c'est un element n'importe quel type.
    - Et parce que les objets tester sont au plus petit cas T au plus grand Object, donc le paramètre est Predicate<? super T> f.

```java
public Index index(Predicate<? super T> f){
  return new Index(IntStream.range(0, size()).filter(i -> f.test(timeSeries.get(i).element)).toArray());
}

```

***

* Question 6 :

  Dans la classe Index, écrire une méthode forEach(lambda) qui prend en paramètre une fonction/lambda qui est appelée avec chaque Data référencée par les indices de l'Index.
  Par exemple, avec la TimeSeries contenant les Data (24 | "hello"), (34 | "time") et (70 | "series") et un Index [0, 2], la fonction sera appelée avec les Data (24 | "hello") et (70 | "series").
  Quel doit être le type du paramètre de la méthode forEach(lambda) ?
  Écrire la méthode forEach(lambda) dans la classe Index et vérifier que les tests marqués "Q6" passent.

* **Réponse :**

    - Le type du paramètre de la méthode forEach(lambda) est Consumer<? super Data<T>>
    - Parce que Data<T> est ce qu'on veut, on veut les timestamps et les elements (tout les objets).

```java
public void forEach(Consumer<? super Data<T>> f) {
  Arrays.stream(indexs).mapToObj(timeSeries::get).forEach(f);
}
```
***

* Question 7 :

  On souhaite maintenant pouvoir parcourir tous les Data d'un Index en utilisant une boucle for comme ceci

      ...
      var index = timeSeries.index();
      for(var data: index) {
        System.out.println(data);
      }

  Quelle interface doit implanter la classe Index pour pouvoir être utilisée dans une telle boucle ?
  Quelle méthode de l'interface doit-on implanter ? Et quel est le type de retour de cette méthode ? Faites les modifications qui s'imposent dans la classe Index et vérifiez que les tests marqués "Q7" passent.

* **Réponse :**

  - Interface Iterable doit implanter la classe Index.
  - On doit implanter la méthode iterator(), et le type de retour est Iterator<Data<T>>.

```java
class Index implements Iterable<Data<T>>{
    private final int[] indexs;

    private Index(int[] array) {

      indexs = array;
    }

    public int size() {
      return indexs.length;
    }

    @Override
    public String toString(){
      //parcourir les elements de timeseries et ajouter un \n à la fin de chaque element
      return Arrays.stream(indexs).mapToObj(e -> timeSeries.get(e).toString()).collect(Collectors.joining("\n"));
    }

    @Override
    public Iterator<Data<T>> iterator() {
      return new Iterator<Data<T>>() {
        int indice = 0;
        @Override
        public boolean hasNext() {
          return indice < indexs.length;
        }

        @Override
        public Data<T> next() {
          if (!hasNext()){
            throw new NoSuchElementException();
          }
          var valeur = timeSeries.get(indexs[indice]);
          indice++;
          return valeur;
        }
      };
    }

    public void forEach(Consumer<? super Data<T>> f) {
      Arrays.stream(indexs).mapToObj(timeSeries::get).forEach(f);
    }
  }
```

***

* Question 8 :

  On veut ajouter une méthode or sur un Index qui prend en paramètre un Index et renvoie un nouvel Index qui contient à la fois les indices de l'Index courant et les indices de l'Index passé en paramètre.
  Il ne doit pas être possible de faire un or avec deux Index issus de TimeSeries différentes.
  En termes d'implantation, on peut faire une implantation en O(n) mais elle est un peu compliquée à écrire. On se propose d'écrire une version en O(n.log(n)) en concaténant les Stream de chaque index puis en triant les indices et en retirant les doublons.
  Expliquer pourquoi on ne peut pas juste concaténer les deux tableaux d'indices ?
  Écrire le code de la méthode or(index) dans la classe Index et vérifier que les tests marqués "Q8" passent.
  Pour concaténer des IntStream il existe une méthode IntStream.concat

* **Réponse :**

    - On ne peut pas juste concaténer les deux tableaux d'indices parce que il y aura les doublons.

```java
public Index or(Index index) {
  Objects.requireNonNull(index);
  if (!this.myTimeSeries().equals(index.myTimeSeries())){
    throw new IllegalArgumentException();
  }
  return new Index(IntStream
        .concat(Arrays.stream(indexs), Arrays.stream(index.indexs)) //concatenate les 2 tableaux
        .sorted() //trier les integers
        .distinct() //supprimer les doublons
        .toArray()); //transformer à tableau
    }
```
***

* Question 9 :

  Même question que précédemment, mais au lieu de vouloir faire un or, on souhaite faire un and entre deux Index.
  En termes d'implantation, il existe un algorithme en O(n) qui est en O(1) en mémoire. À la place, nous allons utiliser un algorithme en O(n) mais qui utilise O(n) en mémoire. L'idée est de prendre un des tableaux d'indices et de stocker tous les indices dans un ensemble sans doublons puis de parcourir l'autre tableau d'indices et de vérifier que chaque indice est bien dans l'ensemble.
  Écrire le code de la méthode and(index) dans la classe Index et vérifier que les tests marqués "Q9" passent.

* **Réponse :**

```java
public void add(long timestamp, T element) {
  if (lastTimestamp > timestamp){
    throw new IllegalStateException();
  }
  if (lastTimestamp < timestamp){
    var data = new Data<T>(timestamp, element);
    timeSeries.add(data);
    lastTimestamp = data.timestamp();
    size++;
  }
}
```

***


