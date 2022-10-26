## TP 4 : JSON, réflexion et annotation
### WANG Chen APP2

***
Ce TD à pour but de comprendre l'intérêt de la réflexion et des annotations.
***

* **EXERCICE 1 - Maven**

Comme pour les TPs précédents, nous allons utiliser Maven comme outil de build. Dans ce TP, lors des tests, nous avons besoin de vérifier que le JSON produit est correct. Pour cela, nous allons utiliser la bibliothèque jackson.

        <dependency>
          <groupId>com.fasterxml.jackson.core</groupId>
          <artifactId>jackson-databind</artifactId>
          <version>2.13.3</version>
          <scope>test</scope>
        </dependency>


Comme cette dépendance est uniquement nécessaire pour les tests, elle est déclarée avec le scope test.
Comme précédemment, créer un projet Maven en cochant create simple project au niveau du premier écran, puis passer à l'écran suivant en indiquant Next.
Pour ce TP, le groupId est fr.uge.json , l'artefactId est json et la version est 0.0.1-SNAPSHOT. Pour finir, cliquer sur Finish.

***

* **Exercice 2 - JSON Encoder**

On souhaite écrire un code qui permet d'afficher un objet au format JSON.
On suppose qu'il existe un record fr.uge.json.Person avec un prénom firstName et un nom lastName :
```java
import static java.util.Objects.requireNonNull;
    
public record Person(String firstName, String lastName) {
  public Person {
    requireNonNull(firstName);
    requireNonNull(lastName);
  }
}
```

Dans une classe JSONPrinter, on peut alors écrire la méthode toJSON qui prend en paramètre une Person et renvoie une chaîne de caractères au format JSON :

```java
package fr.uge.json;
   
public class JSONPrinter {
  public static String toJSON(Person person) {
    return """
      {
        "firstName": "%s",
        "lastName": "%s"
      }
      """.formatted(person.firstName(), person.lastName());
  }
  
  public static void main(String[] args) {
    var person = new Person("John", "Doe");
    System.out.println(toJSON(person));
  }
}
```

Supposons maintenant qu'il existe un record fr.uge.json.Alien avec un age age et une planète planet :
```java
import static java.util.Objects.requireNonNull;
     
public record Alien(int age, String planet) {
  public Alien {
    if (age < 0) {
      throw new IllegalArgumentException("negative age");
    }
    requireNonNull(planet);
  }
}
```

si l'on veut aussi pouvoir afficher un Alien au format JSON, on va écrire une autre méthode toJSON dans la classe JSONPrinter :

```java
public class JSONPrinter {
  ...
  public static String toJSON(Alien alien) {
    return """
      {
        "age": %s,
        "planet": "%s"
      }
      """.formatted(alien.age(), alien.planet());
  } 
  
  public static void main(String[] args) {
    ...
    var alien = new Alien(100, "Saturn");
    System.out.println(toJSON(alien));
  }
}
```

Si l'on doit dupliquer le code de toJSON à chaque fois que l'on veut transformer en JSON un nouveau record, c'est embêtant...

Pour éviter l'hécatombe, on se propose de modifier la classe JSONPrinter, de commenter le code des deux méthodes toJSON et de les remplacer par une seule méthode toJSON prenant un Record en paramètre et utilisant la réflexion (reflection en anglais) pour trouver les composants du record à écrire au format JSON.

Les tests unitaires sont dans la classe JSONPrinterTest.java.

* **Q1**

Écrire la méthode toJSON qui prend en paramètre un java.lang.Record, utilise la réflexion pour accéder à l'ensemble des composants d'un record (java.lang.Class.getRecordComponent), sélectionne les accesseurs, puis affiche les couples nom du composant, valeur associée.
Puis vérifier que les tests marqués "Q1" passent.

Note 1 : il est recommandé d'écrire la méthode en utilisant un Stream.
Note 2 : il faut faire attention à gérer correctement les exceptions lors de l'invocation de méthode (surtout InvocationTargetException).
```java
      ...
    } catch (InvocationTargetException e) {
      var cause = e.getCause();
      if (cause instanceof RuntimeException exception) {
        throw exception;
      }
      if (cause instanceof Error error) {
        throw error;
      }
      throw new UndeclaredThrowableException(e);
    }
```
Note 3 : il y a une petite subtilité avec les guillemets. Dans le format JSON, les chaînes de caractères apparaissent entre "". Nous vous offrons la méthode suivante pour gérer cela :
```java
    private static String escape(Object o) {
      return o instanceof String s ? "\"" + s + "\"": "" + o;
    }
```

**Réponse :**

```java
package fr.uge.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.StringJoiner;

public class JSONPrinter {

  private static String escape(Object o) {
    return o instanceof String ? "\"" + o + "\"": "" + o;
  }
  private static Object myInvoke(Method accessor, Object o) {
    try {
      return accessor.invoke(o);
    } catch (InvocationTargetException | IllegalAccessException ex) {
      var cause = ex.getCause();
      if (cause instanceof RuntimeException exception) {
        throw exception;
      }
      if (cause instanceof Error error) {
        throw error;
      }
      throw new UndeclaredThrowableException(ex);
    }
  }
  public static String toJSON(Record record){
    RecordComponent[] nameValue; // Renvoie un tableau des composants du record
    nameValue = record.getClass().getRecordComponents();
    var s = new StringJoiner(",\n", "{\n", "\n}"); //StringJoiner(séparateur, prefix, suffix)

    Arrays.stream(nameValue).forEach(e -> {
      var invk = myInvoke(e.getAccessor(), record);
      if(e.isAnnotationPresent(JSONProperty.class)) {
        s.add(escape(e.getAnnotation(JSONProperty.class).value()) + " : " + escape(invk));
      }
      else {
        s.add(escape(e.getName()) + " : " + escape(invk));
      }
    });
    System.out.println(s.toString());
    return s.toString();
  }

  public static void main(String[] args) {
    var person = new Person("John", "Doe");
    System.out.println(toJSON(person));

    var alien = new Alien(100, "Saturn");
    System.out.println(toJSON(alien));
  }
}
```

* **Q2**

  En fait, on peut avoir des noms de clé d'objet JSON qui ne sont pas des noms valides en Java, par exemple "book-title", pour cela on se propose d'utiliser un annotation pour indiquer quel doit être le nom de clé utilisé pour générer le JSON.
  Déclarez l'annotation JSONProperty visible à l'exécution et permettant d'annoter des composants de record, puis modifiez le code de toJSON pour n'utiliser que les propriétés issues de méthodes marquées par l'annotation JSONProperty.
  Puis vérifier que les tests marqués "Q2" passent (et uniquement ceux-là pour l'instant).

**Réponse :**

```java
package fr.uge.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.RECORD_COMPONENT)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONProperty {
  String value() default ""; //parameter : change title or author or price into this value
}

```


* **Q3**

  En fait, on veut aussi gérer le fait que l'annotation peut ne pas être présente et aussi le fait que si l'annotation est présente mais sans valeur spécifiée alors le nom du composant est utilisé avec les '_' réécrits en '-'.
  Modifier le code dans JSONPrinter et la déclaration de l'annotation en conséquence.
  Pour tester, vérifier que tous les tests jusqu'à ceux marqués "Q3" passent.
  Rappel : la valeur par défaut d'un attribut d'une annotation ne peut pas être null.

**Réponse :**

Ici on peut utiliser la méthode replace(old string, new string). 
Tout à bord on doit vérifier si e.getAnnotation(JSONProperty.class).value().isEmpty(), si c'est vide (le cas défault) donc on peut

```java
Arrays.stream(nameValue).forEach(e -> {
  var invk = myInvoke(e.getAccessor(), record);
  if(e.isAnnotationPresent(JSONProperty.class)) {
    if (e.getAnnotation(JSONProperty.class).value().isEmpty()){
      s.add(escape(e.getName().replace("_", "-")) + " : " + escape(invk));
    }
    else {
      s.add(escape(e.getAnnotation(JSONProperty.class).value()) + " : " + escape(invk));
    }
  }
  else {
    s.add(escape(e.getName()) + " : " + escape(invk));
  }
});

```

* **Q4**

  En fait, l'appel à getRecordComponents est lent ; regardez la signature de cette méthode et expliquez pourquoi...

**Réponse :**

getRecordComponents est lent parce que il renvoie une tableau, et un tableau est mutable donc la méthode doit copie la table chaque fois avant il renvoie le résultat.


* **Q5**

  Nous allons donc limiter les appels à getRecordComponents en stockant le résultat de getRecordComponents dans un cache pour éviter de faire l'appel à chaque fois qu'on utilise toJSON.
  Utilisez la classe java.lang.ClassValue pour mettre en cache le résultat d'un appel à getRecordComponents pour une classe donnée.

**Réponse :**

J'ai creé une classe final static ClassValue<RecordComponent[]> qui contient une méthode protected computeValue qui prends Class<?> type et renvoie RecordComponent[].

```java
private final static ClassValue<RecordComponent[]> CLASS_VALUE = new ClassValue<>() {
    @Override
    protected RecordComponent[] computeValue(Class<?> type) {
      return type.getRecordComponents();
    }
  };

public static String toJSON(Record record){
        RecordComponent[] nameValue; // Renvoie un tableau des composants du record
        // nameValue = record.getClass().getRecordComponents();
        nameValue = CLASS_VALUE.get(record.getClass());
        var s = new StringJoiner(",\n", "{\n", "\n}"); //StringJoiner(séparateur, prefix, suffix)
        ...
```

* **Q6**

  En fait, on peut mettre en cache plus d'informations que juste les méthodes, on peut aussi pré-calculer le nom des propriétés pour éviter d'accéder aux annotations à chaque appel.
  Écrire le code qui pré-calcule le maximum de choses pour que l'appel à toJSON soit le plus efficace possible.
  Indication : quelle est la lettre grecque entre kappa et mu?

**Réponse :**

J'ai créé une méthode pour obtenir tout les component dans le record qui renvoie le String, et une méthode qui prend les recordComponents engendré par la méthode takeName et renvoie une interface fonctionnelle (lambda).
De plus, j'ai créé une ClassValue qui sert de cache pour stocker des interfaces fonctionnelles dans une liste. Après dans la méthode toJSON on peut utiliser get() pour vérifier si la Class est misa à jour ou pas, 
sinon on en calcule la valeur.

La méthode takeName c'est ce que on avait mis dans toJSON avec le lambda, et ici pour stocker les valeur, on crée une méthode takeName qui prend une Record : record et les éléments dans RecordComponent : e.

```java

  private final static ClassValue<List<Function<Record, String>>> CLASS_VALUE2 = new ClassValue<>() {
    @Override
    protected List<Function<Record, String>> computeValue(Class<?> type) {
      return Arrays.stream(type.getRecordComponents()).map(JSONPrinter::lambdaForList).collect(Collectors.toList());
    }
  };

private static Function<Record, String> lambdaForList(RecordComponent recordComponent){
  return record -> takeName(record, recordComponent);
}

public static String takeName(Record record, RecordComponent e){
  var invk = myInvoke(e.getAccessor(), record);
  if(e.isAnnotationPresent(JSONProperty.class)) {
    if (e.getAnnotation(JSONProperty.class).value().isEmpty()){
      return escape(e.getName().replace("_", "-")) + " : " + escape(invk);
    }
    else {
      return escape(e.getAnnotation(JSONProperty.class).value()) + " : " + escape(invk);
    }
  }
  else {
    return escape(e.getName()) + " : " + escape(invk);
  }
}

public static String toJSON(Record record){
    return CLASS_VALUE2.get(record.getClass()).stream().map(c -> c.apply(record)).collect(Collectors.joining(",\n", "{\n", "\n}"));
}
```



