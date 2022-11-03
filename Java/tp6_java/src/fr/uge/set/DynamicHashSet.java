package fr.uge.set;

import java.util.Objects;
import java.util.function.Consumer;

public class DynamicHashSet<T> {
	
	private int maxSize = 8;
	@SuppressWarnings("unchecked")
	//private final Entry<T>[] array = (Entry<T>[]) new Entry<?>[8];
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
		if (contains(value)) return;
        if (size+1 >= maxSize / 2) {
            maxSize *= 2; //modifier la taille de l'array -> maxSize*2
            var old = array;
            array = (Entry<T>[]) new Entry[maxSize];
            for (var i = 0; i < old.length; i++) {
                var entry = old[i];
                while(entry != null) {
                	var oldValue = entry.value();
                    var index = hash(oldValue);
                    array[index] = new Entry<T>(oldValue, array[index]);
                    entry = entry.next();
                }
            }
        }
        var pos = hash(value);
        array[pos] = new Entry<T>(value, array[pos]);
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
