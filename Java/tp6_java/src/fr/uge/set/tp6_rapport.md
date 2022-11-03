## TP 6 : Implantation d'une table de hachage, classe interne.
### Chen WANG App2

Le but de ces deux exercices est d'implanter une représentation d'un ensemble d'éléments sans doublons.
Comme les éléments ne sont pas forcément consécutifs, ils n'est pas possible d'utiliser un tableau ou un bitset pour représenter les valeurs (explosion en mémoire) ni une liste (algos d'ajout/recherche/suppression en O(n)), on utilisera donc une table de hachage où les collisions sont stockées dans une liste chaînée.
La taille de la table de hachage sera toujours une puissance de 2 (2, 4, 8, 16, 32, etc...) pour permettre l'écriture d'une fonction de hachage rapide.
Dans le premier exercice, on considérera que la table de hachage a une taille fixe, puis pour le second exercice que la table de hachage doit grandir dès qu'elle est à moitié pleine. Il faudra réorganiser les éléments lorsque la table de hachage grandie.
On souhaite que les deux tables de hachage aient les opérations suivantes:

    - add qui permet d'ajouter un entier à l'ensemble (s'il n'est pas déjà présent).
    - contains qui renvoie vrai si l'entier est dans l'ensemble.
    - size qui renvoie le nombre d'entiers contenus dans l'ensemble.
    - forEach qui prend une lambda en paramètre, celle-ci sera appelée pour chaque entier présent dans l'ensemble.

Note : le but du TD est de ré-écrire une table de hachage, pas d'en utiliser une qui existe déjà, donc pour ce TD, nous n'utiliserons aucune des classes de java.util.

***

**Exercice 1 - Pas de Maven cette fois-ci**

* Pour préparer le TP noté, on va apprendre à utliser les tests unitaires JUnit en dehors d'un projet Maven. Dans la configuration de votre projet Java classique, il faut ajouter la librarie JUnit 5 correctement.
  Vous avez 2 options :

  * Soit à partir du fichier IntHashSetTest.java : sur la première annotation @Test, utiliser le quickfix "Fixup project ..."
  * Soit en selection les "Properties" du projet (avec le bouton droit de la souris sur le projet) puis en ajoutant la Library JUnit 5 (jupiter) au ClassPath.

***

**Exercice 2 - IntHashSet**

Nous allons, dans un premier temps, implanter l'ensemble d'entiers en utilisant une table de hachage qui, s'il y a collision, stocke les éléments dans une liste chaînée.
Pour les besoins du TP, nous allons, pour l'instant, fixer la taille de la table de hachage à 8, mais on vous demande d'écrire le code en présupposant seulement que la taille est une puissance de 2.

Les tests JUnit 5 de cet exercice sont IntHashSetTest.java.

* Question 1 :

  Quels doivent être les champs de la classe Entry correspondant à une case de la table de hachage sachant que l'on veut stocker les collisions dans une liste chaînée (que l'on va fabriquer nous-même, et pas en utilisant LinkedList).
  On cherche à écrire la classe Java Entry correspondante dans le package fr.umlv.set. 
    - Quelle doit être la visibilité de cette classe ? 
    - Quels doivent être les modificateurs pour chaque champ ? 
    - En fait, ne pourrait-on pas utiliser un record plutôt qu'une classe, ici ? Pourquoi ?
    - Pour finir, il vaut mieux déclarer Entry en tant que classe interne de la classe IntHashSet plutôt qu'en tant que type dans le package fr.umlv.set. Pourquoi ? 
    - Quelle doit être la visibilité de Entry ?
  Écrire la classe IntHashSet dans le package fr.umlv.set et ajouter Entry en tant que classe interne.

* **Réponse :**
    - La visibilité de la classe IntHashSet est public.
    - Comme un champ ou une méthode, une classe interne est un membre de la classe englobante. Elle accepte donc les modificateurs de visibilité
    - La visibilité de la classe Entry c'est private et ici on peut utiliser un record parce que il ne change pas sa value et next(pointer sur next Entry).

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class IntHashSet {

  private final Entry[] array = new Entry[8];

  private record Entry(int value, Entry next){
  }
}

```

***

* Question 2 :

    - Comment écrire la fonction de hachage dans le cas où la taille de la table est 2 ?
    - Pourquoi est-ce que ça n'est pas une bonne idée d'utiliser l'opérateur % ? 
    - Écrire une version "rapide" de la fonction de hachage
  Indice : on peut regarder le bit le plus à droite (celui des unités) pour savoir si l'on doit stocker les éléments dans la case 0 ou 1.
    - Et si la taille de la table est 8 ?
  En suivant la même idée, modifier votre fonction de hachage dans le cas où la taille de la table est une puissance de 2.
  Note : si vous ne connaissez pas, allez lire le chapitre 2 du Hacker's Delight.

* **Réponse :**
    - Parce que on a utilisé la méthode donnée par le fichier Hacker's Delight. On peut trouver la position de certain de valeur grâce à cette méthode.

```java
  private int hash(int value) {
    return value & (array.length - 1);
  }

```

***

* Question 3 :

  Dans la classe IntHashSet, implanter la méthode add.
  Écrire également la méthode size avec une implantation en O(1).

* **Réponse :**

  - La méthode size() renvoie void et quand on appelle la méthode add(value), size va ajouter 1 vue que la méthode size avec une implantation en O(1).
  - Après on initialise size = 0, parce que au début notre array est vide.
  - Pour la méthode add(value), on trouve tout d'abord la position de cette valeur, et on ajoute la valeur dans la position d'array.
  
```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class IntHashSet {

  private final Entry[] array = new Entry[8];

  int size = 0;

  private record Entry(int value, Entry next){

  }

  private int hash(int value) {
    return value & (array.length - 1);
  }

  public Integer size() {
    return size;
  }

  public void add(int value) {
    int pos = hash(value); //donner une position pour value
    Entry entry = array[pos];
    while(entry != null) {
      if(entry.value() == value) {
        return;
      }
      entry = entry.next(); //parcourir l'entry
    }
    array[pos] = new Entry(value, array[pos]);
    size++;
  }
}

```

***

* Question 4 :

  On cherche maintenant à implanter la méthode forEach. 
    - Quelle doit être la signature de la functional interface prise en paramètre de la méthode forEach ? 
    - Quel est le nom de la classe du package java.util.function qui a une méthode ayant la même signature ?
  Écrire la méthode forEach.

* **Réponse :**
    - La signature de la functional interface prise en paramètre de la méthode forEach est : Consumer<Integer> consomer.
    - Integer est le nom de la classe.

```java
  public void forEach(Consumer<Integer> op) {
    Objects.requireNonNull(op);
    for(var i = 0; i < array.length; i++) {
      Entry entry = array[i]; //index de l'array 
      while(entry != null) {
        op.accept(entry.value());
        entry = entry.next(); //parcourir l'entry
      }
    }
  }

```

***

* Question 5 :

  Écrire la méthode contains.

* **Réponse :**
  
```java
  public boolean contains(int value) {

    for(var i = 0; i < array.length; i++) {
      Entry entry = array[i]; //index de l'array 
      while(entry != null) {
        if(entry.value() == value) {
          return true;
        }
        entry = entry.next(); //parcourir l'entry
      }
    }
    return false;
  }
```

***

**Exercice 3 - DynamicHashSet**

Pour optimiser notre table de hachage, on souhaite qu'en moyenne, la table soit toujours à moitié vide, de façon à éviter que les listes chaînées qui gèrent les collisions soient trop longues. Pour cela, on vous demande d'agrandir la table de hachage si le nombre d’éléments stockés est supérieur à la moitié de la taille de la table de hachage.
L'algorithme classique d'agrandissement d'une table de hachage consiste non seulement à doubler la taille mais aussi, pour chaque liste chaînée, à re-répartir l'ensemble des valeurs contenues dans la liste car des valeurs qui entraient en collision pour une certaine taille de la table de hachage ne sont plus forcément en collision avec une nouvelle taille.
Les tests JUnit 5 de cet exercice sont DynamicHashSetTest.java.


* Question 1 :

  Avant tout, nous souhaitons générifier notre table de hachage, pour permettre de ne pas stocker uniquement des entiers mais n'importe quel type de valeur.
  Avant de générifier votre code, 
    - quelle est le problème avec la création de tableau ayant pour type un type paramétré ?
    - Comment fait-on pour résoudre le problème, même si cela lève un warning.
    - Rappeler pourquoi on a ce warning.
    - Peut-on supprimer le warning ici, ou est-ce une bêtise ?
    - Comment fait-on pour supprimer le warning ?
  Reprendre et générifier le code de l'exercice précédent pour fabriquer une classe de table de hachage générique.

* **Réponse :**
    - Le problem est : Type safety: Unchecked cast from DynamicHashSet.Entry<?>[] to DynamicHashSet.Entry<T>[]
    - On peut mettre **@SuppressWarnings** sur la déclaration d’une variable locale pour réduire sa portée.
    - Parce que cast non safe, on peut dire au compilateur que le cast n’est pas un cast non sûre et que l’on peut supprimer le warning.

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class DynamicHashSet<T> {
	
	@SuppressWarnings("unchecked")
	private final Entry<T>[] array = (Entry<T>[]) new Entry<?>[8];
	
	int size = 0;
	
	private record Entry<T>(T value, Entry<T> next){ 
		
	}
	
	private int hash(T value) {
		Objects.requireNonNull(value);
		return value.hashCode() & (array.length - 1); //value % size
	}

	public Integer size() {
	
		return size;
	}
/*
	public void add(T value) {
		Objects.requireNonNull(value);
		int pos = hash(value); 
		Entry<T> entry = array[pos];
		
		while(entry != null) {
			if(entry.value().equals(value)) { //comparer avec les objects on utilise equals(object)
				return;
			}
			entry = entry.next(); 
		}
		array[pos] = new Entry<T>(value, array[pos]);
		size++;
	}
*/
	//optimisation de la méthode add
	@SuppressWarnings("unchecked")
	public void add(T value) {
		Objects.requireNonNull(value);
		if(array.length < 8) { //si la longueur du tableau est inférieure à la moitié du nombre d’éléments
			array = (Entry<T>[]) new Entry<?>[16];
		}
		var pos = hash(value);
		if(contains(value)) {
			return;
		}
		array[pos] = new Entry<T>(value, array[pos]);
		size++;
	}

	public void forEach(Consumer<T> op) {
		Objects.requireNonNull(op);
		for(var i = 0; i < array.length; i++) {
			Entry<T> entry = array[i]; 
			while(entry != null) {
				op.accept(entry.value());
				entry = entry.next();
			}
		}
	}
	
	public boolean contains(Object value) {
		Objects.requireNonNull(value);
		for(var i = 0; i < array.length; i++) {
			Entry<T> entry = array[i]; //index de l'array 
			while(entry != null) {
				if(entry.value().equals(value)) {
					return true;
				}
				entry = entry.next(); //parcourir l'entry
			}
		}
		return false;
	}
	
}

```

***

* Question 2 :

  Vérifier la signature de la méthode contains de HashSet et expliquer pourquoi on utilise un type plus général que E.

* **Réponse :**

    - J'ai optimisé la méthode add(T value) avec la méthode contains(Object value).
    - Ici on va utiliser Object mais pas T parce que ce champs là n'est pas obligé d'être le même type que la record.
    - Et à la fin on change la visibilité en public.

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class DynamicHashSet<T> {
	
	@SuppressWarnings("unchecked")
	private final Entry<T>[] array = (Entry<T>[]) new Entry<?>[8];
	
	int size = 0;
	
	private record Entry<T>(T value, Entry<T> next){ 
		
	}
	
	private int hash(T value) {
		Objects.requireNonNull(value);
		return value.hashCode() & (array.length - 1); //value % size
	}

	public Integer size() {
	
		return size;
	}
/*
	public void add(T value) {
		Objects.requireNonNull(value);
		int pos = hash(value); 
		Entry<T> entry = array[pos];
		
		while(entry != null) {
			if(entry.value().equals(value)) { //comparer avec les objects on utilise equals(object)
				return;
			}
			entry = entry.next(); 
		}
		array[pos] = new Entry<T>(value, array[pos]);
		size++;
	}
*/
	//optimisation de la méthode add
	@SuppressWarnings("unchecked")
	public void add(T value) {
		Objects.requireNonNull(value);
		if(array.length < 8) { //si la longueur du tableau est inférieure à la moitié du nombre d’éléments
			array = (Entry<T>[]) new Entry<?>[16];
		}
		var pos = hash(value);
		if(contains(value)) {
			return;
		}
		array[pos] = new Entry<T>(value, array[pos]);
		size++;
	}

	public void forEach(Consumer<T> op) {
		Objects.requireNonNull(op);
		for(var i = 0; i < array.length; i++) {
			Entry<T> entry = array[i]; 
			while(entry != null) {
				op.accept(entry.value());
				entry = entry.next();
			}
		}
	}
	
	public boolean contains(Object value) {
		Objects.requireNonNull(value);
		for(var i = 0; i < array.length; i++) {
			Entry<T> entry = array[i]; //index de l'array 
			while(entry != null) {
				if(entry.value().equals(value)) {
					return true;
				}
				entry = entry.next(); //parcourir l'entry
			}
		}
		return false;
	}
}

```

***

* Question 3 :

  Modifier le code de la méthode add pour implanter l'algorithme d'agrandissement de la table.
  L'idée est que si la longueur du tableau est inférieure à la moitié du nombre d’éléments, il faut doubler la taille du tableau et re-stocker tous les éléments (pas besoin de tester si un élément existe déjà dans le nouveau tableau).
  Note : il faut que le champ contenant le tableau ne soit plus final.

* **Réponse :**

    - On doit tout d'abord optimiser la méthode **contains(object value)**, ici on peut utiliser **hash(value)** pour obtenir sa position dans array directement.
    - Après on ajoute un **private int maxSize** comme ça on peut modifier la taille de notre array avec **private Entry<T>[] array = (Entry<T>[]) new Entry<?>[maxSize]**;
    - Pour la méthode **add(value)**, on vérifie l'existence de cette valeur, si elle a déjà existé, on return, sinon on vérifie la taille de l'array (Attention, ici on compare size+1 avec sizeMax/2)
    - On stocke l'ancienne array dans un **var old** après on re-initialise le champ array avec maxSize*2 **array = (Entry<T>[]) new Entry[maxSize]**
    - On parcourt l'ancienne array et chaque entry de l'array[i] et stocke sa valeur et sa position dans **oldValue** et **index**.
    - Je donne sa nouvelle position dans la nouvelle array **array[index] = new Entry<T>(oldValue, array[index])**, et passe à next enrty.
    - Dehors le **if**, c'est la partie pour ajouter la valeur, on stocke sa position **var pos = hash(value)** et stocke dans l'array[pos].
    - À la fin, n'oublie pas de mettre à jour le size de notre array avec **size++**.

```java
package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class DynamicHashSet<T> {
	
	int maxSize = 8;
	@SuppressWarnings("unchecked")
    
	private Entry<T>[] array = (Entry<T>[]) new Entry<?>[maxSize];
	
	private int size = 0;
	
	private record Entry<T>(T value, Entry<T> next){ 
		public Entry{
			Objects.requireNonNull(value);
		}
	}
	
	private int hash(Object value) { //renvoie position de cette valeur
		Objects.requireNonNull(value);
		return value.hashCode() & (maxSize - 1); //value % size
	}

	public Integer size() {
		return size;
	}
  
	//optimisation de la méthode add
	@SuppressWarnings("unchecked")
    public void add(T value) {
		if (contains(value)) return;
        if (size >= maxSize / 2) {
            maxSize *= 2;
            var old = array;
            //System.out.println(old);
            array = (Entry<T>[]) new Entry[maxSize];
            for (var i = 0; i < old.length; i++) {
                var entry = old[i];
                while(entry != null) {
                    var index = hash(entry.value());
                    array[index] = new Entry<T>(entry.value(), array[index]);
                    entry = entry.next();
                }
            }
        }
        var index = hash(value);
        array[index] = new Entry<T>(value, array[index]);
        size++;
        
    }

	public void forEach(Consumer<T> op) {
		Objects.requireNonNull(op);
		for(var i = 0; i < maxSize; i++) {
			Entry<T> entry = array[i]; 
			while(entry != null) {
				op.accept(entry.value());
				entry = entry.next();
			}
		}
	}
	
	public boolean contains(Object value) {
		Objects.requireNonNull(value);
		Entry<T> entry = array[hash(value)]; //Pas besoin de parcourir l'array. On utilise hash(value pour trouver sa position)
		while(entry != null) {
			if(entry.value().equals(value)) {
				return true;
			}
			entry = entry.next(); //parcourir l'entry
		}
		return false;
	}
}

```
