## TP 5 : Single instruction, Multiple Data (SIMD) 
### Chen WANG App2

Vectorisation, Loop decomposition, Opération binaire et Mask.
Nous allons utiliser l'API jdk.incubator.vector intégré au JDK 19. 

***

**Exercice 1 - Maven**

* Comme pour les TPs précédents, nous allons utiliser Maven comme outil de build. Dans ce TP, nous avons trois besoins spécifiques.

***

**Exercice 2 - Vectorized Add / Vectorized Min**

* Question 1 :

    On cherche à écrire une fonction sum qui calcule la somme des entiers d'un tableau passé en paramètre. Pour cela, nous allons utiliser l'API de vectorisation pour calculer la somme sur des vecteurs.

    - Quelle est la classe qui représente des vecteurs d'entiers ?
    
    - Qu'est ce qu'un VectorSpecies et quelle est la valeur de VectorSpecies que nous allons utiliser dans notre cas ?
    
    - Comment créer un vecteur contenant des zéros et ayant un nombre préféré de lanes ?
    
    - Comment calculer la taille de la boucle sur les vecteurs (loopBound) ?
    
    - Comment faire la somme de deux vecteurs d'entiers ?
    
    - Comment faire la somme de toutes les lanes d'un vecteur d'entiers ?
    
    - Si la longueur du tableau n'est pas un multiple du nombre de lanes, on va utiliser une post-loop, quel doit être le code de la post-loop ?

* **Réponse :**
  - eee
  - e
  - e
  - e
  - e
  - e
  - e

```java
private final static VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
  public static int sum(int[] array) {
    Objects.requireNonNull(array);
    var length = array.length;
    // loopBound permet de savoir jusqu'à où on peut vectoriser l'array
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    // la methode .zero est équivalente à IntVector.broadcast(species, 0);
    var v0 = IntVector.zero(SPECIES);
    for (; i < loopBound; i += SPECIES.length()) {
    // chunk vector of array
    var v = IntVector.fromArray(SPECIES, array, i);
    // on additionne le contenu des lanes entre eux
    v0 = v0.add(v);
    }
    // ici on additionne les lanes entre elles
    int sum = v0.reduceLanes(VectorOperators.ADD);
    // La post-loop qui nous permet de finir le traitement complet de l'array
    for (; i < length; i++) { // ajouter les restes nombres
      sum += array[i];
    }
    return sum;
}

```

***

* Question 2 :

  On souhaite écrire une méthode sumMask qui évite d'utiliser une post-loop et utilise un mask à la place.

  - Comment peut-on faire une addition de deux vecteurs avec un mask ?
  - Comment faire pour créer un mask qui allume les bits entre i la variable de boucle et length la longueur du tableau ?

  Écrire le code de la méthode sumMask et vérifier que le test "testSumMask" passe.
  Que pouvez dire en terme de performance entre sum et sumMask en utilisant les tests de performances JMH ?

* **Réponse :**
    - On peut utiliser v = IntVector.fromArray(SPECIES, array, i, mask) et puis on fait v = v.add(v0) pour faire une addition de deux vecteurs (v et v0) avec un mask.
    - On fait var mask = SPECIES.indexInRange(i, length); pour créer un mask qui allume les bits (SPECIES) entre i la variable de boucle et length la longueur du tableau.

```java
public static int sumMask(int[] array) {
  Objects.requireNonNull(array);
  var length = array.length;
  var loopBound = length - length % SPECIES.length();
  var i = 0;
  var v0 = IntVector.zero(SPECIES);
  for (; i < loopBound; i += SPECIES.length()) {
    var v = IntVector.fromArray(SPECIES, array, i);
    v0 = v0.add(v);
  }
  var mask = SPECIES.indexInRange(i, length);
  var v = IntVector.fromArray(SPECIES, array, i, mask);
  // on ajoute les restes int avec v0int sum = v.reduceLanes(VectorOperators.ADD);
  v = v.add(v0); 
  return sum;
}
```

***

* Question 3 :

    On souhaite maintenant écrire une méthode min qui calcule le minimum des valeurs d'un tableau en utilisant des vecteurs et une post-loop.
Contrairement à la somme qui a 0 comme élément nul, le minimum n'a pas d'élément nul... Quelle doit être la valeur utilisée pour initialiser de toute les lanes du vecteur avant la boucle principale ?
Écrire le code de la méthode min, vérifier que le test nommé "testMin" passe et vérifier avec les tests JMH que votre code est plus efficace qu'une simple boucle sur les valeurs du tableau.

* **Réponse :**
    - J'ai utilisé la méthode Integer.min(int a, int b) pour comparer 2 ints.
    - Après j'ai stocké la valeur minimum dans min.
    - À la fin je parcourir tout les restes nombres de l'array, et renvoie le min de l'array.

```java
public static int min(int[] array) {
    Objects.requireNonNull(array);
    var length = array.length;
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    int min = Integer.MAX_VALUE; //nobody is bigger than this min
    for (; i < loopBound; i += SPECIES.length()) {
      var v = IntVector.fromArray(SPECIES, array, i);
      min = Integer.min(v.reduceLanes(VectorOperators.MIN), min);
    }
    for (; i < length; i++) { // ajouter les restes nombres
      if (array[i] < min){
        min = Integer.min(array[i], min);
      }
    }
    return min;
  }
```

***

* Question 4 :

    On souhaite enfin écrire une méthode minMask qui au lieu d'utiliser une post-loop comme dans le code précédent, utilise un mask à la place.
Attention, le minimum n'a pas d’élément nul (non, toujours pas !), donc on ne peut pas laisser des zéros "traîner" dans les llanes lorsque l'on fait un minimum sur deux vecteurs.
Écrire le code de la méthode minMask et vérifier que le test nommé "testMinMask" passe.
Que pouvez-vous dire en termes de performance entre min et minMask en utilisant les tests de performances JMH ? 


* **Réponse :**
  - J'ai utilisé la méthode Integer.min(int a, int b) pour comparer 2 ints.
  - Après j'ai stocké la valeur minimum dans min.
  - Je parcourir tout les restes nombres de l'array avec mask
    -  var mask = SPECIES.indexInRange(i, length);
    -  var v = IntVector.fromArray(SPECIES, array, i, mask);
  - Je renvoie le min de l'array avec le mask
    - min = Integer.min(v.reduceLanes(VectorOperators.MIN, mask), min);

```java
  public static int minMask(int[] array) {
    Objects.requireNonNull(array);
    var length = array.length;
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    int min = Integer.MAX_VALUE;
    var v0 = IntVector.zero(SPECIES);
    for (; i < loopBound; i += SPECIES.length()) {
      var v = IntVector.fromArray(SPECIES, array, i);
      min = Integer.min(v.reduceLanes(VectorOperators.MIN), min);
    }
    var mask = SPECIES.indexInRange(i, length);
    var v = IntVector.fromArray(SPECIES, array, i, mask);
    System.out.println(v);
    min = Integer.min(v.reduceLanes(VectorOperators.MIN, mask), min);
    return min;
  }
```

***

**Exercice 3 - FizzBuzz**

* Question 1 :

  On souhaite écrire dans la classe FizzBuzz une méthode fizzBuzzVector128 qui prend en paramètre une longueur et renvoie un tableau d'entiers de taille longueur contenant les valeurs de FizzBuzz en utilisant des vecteurs 128 bits d'entiers.
  Écrire la méthode fizzBuzzVector128 sachant que les tableaux des valeurs et des deltas sont des constantes. Puis vérifier que votre implantation passe le test.
  En exécutant les tests JMH, que pouvez vous conclure en observant les différences de performance entre la version de base et la version utilisant l'API des vecteurs. 

* **Réponse :**
    - eee
    - e
    - e

```java

```

***

* Question 2 :

  On souhaite maintenant écrie une méthode fizzBuzzVector256 qui utilise des vecteurs 256 bits.
  Une fois la méthode écrite, vérifier que celle-ci passe le test.
  Utiliser les tests JMH pour vérifier la performance de votre implantation. Que pouvez vous en conclure en comparaison de la version utilisant des vecteurs 128 bits.

* **Réponse :**
    - eee
    - e
    - e

```java

```

***

* Question 3 :

  Il existe une autre façon d'implanter algorithme, on peut ajouter 15 avec un mask ; en pseudo code, cela donne :

  ```
  v =     [-3,  1,  2, -1,  4, -2, -1,  7,  8, -1, -2, 11, -1, 13, 14]
  mask =  [F,   T,  T,  F,  T,  F,  F,  T,  T,  F,  F,  T,  F,  T,  T]
  boucle toute les 15 valeurs
  on stock v dans le tableau
  v = v + 15 with mask
  ```
    Écrire la méthode fizzBuzzVector128AddMask qui utilise des vecteurs 128 bits et l'addition avec un mask, puis vérifier avec le test que votre code fonctionne correctement. Comme précédemmment, le tableau des masques doit être une constante.
Comparer les performances de ce nouvel algorithme par rapport votre autre implantation à base de vecteur 128 bits. Que pouvez-vous en conclure ?
Note: il y a une astuce sur la taille du tableau des masques. 

* **Réponse :**
    - eee
    - e
    - e

```java

```
