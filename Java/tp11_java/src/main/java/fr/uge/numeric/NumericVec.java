package fr.uge.numeric;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NumericVec<T> {
  private long[] numericVec;
  private int size = 0;
  private final Function<Long, T> from;
  private final Function<T, Long> into;

  @SuppressWarnings("unchecked")
  private NumericVec() {
    this.numericVec = new long[16];
    into = e -> (Long) e;
    from = e -> (T) e;
  }
  private NumericVec(Function<T, Long> into, Function<Long, T> from) {
    this.numericVec = new long[16];
    this.into = into;
    this.from = from;
  }

  public static NumericVec<Long> longs() {
    return new NumericVec<>();
  }

  public void add(T l){
    Objects.requireNonNull(l);
    if (size == numericVec.length){
      numericVec = Arrays.copyOf(numericVec, numericVec.length *2);
    }
    numericVec[size] = into.apply(l);
    size++;
  }

  public int size() {
    return size;
  }

  public T get(int i) {
    Objects.checkIndex(i, size);
    return from.apply(numericVec[i]);
  }

  public static NumericVec<Long> longs(long... i) {
    var newTabInt = new NumericVec<>(e -> e, e -> e);
    for (var value : i){
      newTabInt.add(value);
    }
    return newTabInt;
  }

  public static NumericVec<Double> doubles(double... i) {
    var newTabInt = new NumericVec<>(Double::doubleToRawLongBits, Double::longBitsToDouble);
    for (var value : i){
      newTabInt.add(value);
    }
    return newTabInt;
  }

  public static NumericVec<Integer> ints(int... i) {
    var newTabInt = new NumericVec<>(e -> (long) e, e -> Integer.parseInt(String.valueOf(e)));
    for (var value : i){
      newTabInt.add(value);
    }
    return newTabInt;
  }
  public String toString(){
    return Arrays.stream(numericVec)
        .limit(size).mapToObj(e -> from.apply(e).toString())
        .collect(Collectors.joining(", ","[","]"));
  }
  public void iterator() {

  }
}
