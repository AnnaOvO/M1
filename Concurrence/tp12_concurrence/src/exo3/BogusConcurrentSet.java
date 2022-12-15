package exo3;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public class BogusConcurrentSet<E> {
	private final E[][] table;

	@SuppressWarnings("unchecked")
	public BogusConcurrentSet(int capacity) {
		this.table = (E[][]) new Object[capacity][];
		Arrays.setAll(table, i -> (E[]) new Object[0]);
	}

	public void add(E element) {
	    Objects.requireNonNull(element);
	    var index = element.hashCode() % table.length;
	    for(var item : table[index]) {
	      	if (element.equals(item)) {
	        	return;
	      	}
	    }
	    var newArray = Arrays.copyOf(table[index], table[index].length + 1);
	    newArray[newArray.length - 1] = element;
	    table[index] = newArray;
	 }

	public int size() {
		return Arrays.stream(table).mapToInt(array -> array.length).sum();
	}

	public static void main(String[] args) throws InterruptedException {
		var set = new BogusConcurrentSet<Integer>(1_000_000);
		var threads = IntStream.range(0, 4).mapToObj(j -> Thread.ofPlatform().start(() -> {
			for (var i = 0; i < 250_000; i++) {
				set.add(i);
			}
		})).toList();
		for (var thread : threads) {
			thread.join();
		}
		System.out.println(set.size());
	}
}