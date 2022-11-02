# TP 6 - Implantation d'une table de hachage, classe interne.
## Chen WANG App2

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
	
}
```