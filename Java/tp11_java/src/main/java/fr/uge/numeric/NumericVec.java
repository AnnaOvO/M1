package fr.uge.numeric;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NumericVec<T> implements Iterable<T>{
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
  @Override
  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int i = 0;
      private final int sizeI = size;
      @Override
      public boolean hasNext() {
        return i < sizeI;
      }
      @Override
      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        return get(i++);
      }
    };
  }
  public void addAll(NumericVec<? extends T> seq) {
    Objects.requireNonNull(seq);
    for (int i = 0; i < seq.size; i++) {
      this.add(seq.get(i));
    }
  }

  public <E> NumericVec<E> map(Function<? super T, E> function, Supplier<? extends NumericVec<E>> factory) {
    Objects.requireNonNull(function);
    Objects.requireNonNull(factory);
    var ret = factory.get();
    Arrays.stream(numericVec, 0, size)
        .mapToObj(o -> from.andThen(function).apply(o)).forEach(ret::add);
    return ret;
  }

  public static <T> Collector<T, ?, NumericVec<T>> toNumericVec(Supplier<NumericVec<T>> factory) {
    return Collector.of(factory,
        NumericVec::add,
        (n1, n2) -> { n1.addAll(n2); return n1; });
  }

  private Spliterator<T> fromNumeric(int start, int end) {
    return new Spliterator<>() {
      int index = start;
      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        if (index == end) {
          return false;
        }
        action.accept(get(index++));
        return true;
      }
      @Override
      public Spliterator<T> trySplit() {
        var middle = (end + index) >>> 1;
        if (middle == index || end - start < 1024) {
          return null;
        }
        var split = fromNumeric(index, middle);
        index = middle;
        return split;
      }

      @Override
      public long estimateSize() {
        return end - start;
      }

      @Override
      public int characteristics() {
        return SIZED | SUBSIZED | NONNULL | ORDERED | IMMUTABLE;
      }
    };
  }

  public Stream<T> stream() {
    return StreamSupport.stream(fromNumeric(0, size), size >= 1024);
  }
}
