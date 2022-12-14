package fr.uge.seq;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Seq2<T> implements Iterable<T> {
  private final T[] seq;
  private final Function<Object, ? extends T> function;

  @SuppressWarnings("unchecked")
  private Seq2(List<?> seq) {
    this.seq = (T[]) List.copyOf(seq).toArray();
    this.function = x -> (T) x;
  }
  private Seq2(List<?> seq, Function<Object, ? extends T> function) {
    Objects.requireNonNull(seq);
    Objects.requireNonNull(function);
    this.seq = (T[]) List.copyOf(seq).toArray();
    this.function = function;
  }

  public static <E>Seq2<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq2<>(lst);
  }

  public int size() {
    return seq.length;
  }

  public T get(int index) {
    Objects.checkIndex(index, seq.length);
    return function.apply(seq[index]);
  }

  public String toString(){
    return Arrays.stream(seq).map(function)
        .map(Object::toString)
        .collect(Collectors.joining(", ", "<", ">"));
  }

  @SafeVarargs
  public static <E>Seq2<E> of(E... lst) {
    Objects.requireNonNull(lst);
    return new Seq2<>(List.of(lst));
  }
  public void forEach(Consumer<? super T> f) {
    Objects.requireNonNull(f);
    Arrays.stream(seq).forEach(e -> f.accept(function.apply(e)));
  }

  public <E>Seq2<E> map(Function<? super T, ? extends E> f) {
    Objects.requireNonNull(f);
    return new Seq2<>(Arrays.asList(seq), function.andThen(f));
  }

  public Optional<T> findFirst() {
    return (seq.length > 0) ? Optional.of(get(0)) : Optional.empty();
  }

  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int current = 0;
      @Override
      public boolean hasNext() {
        return current < seq.length;
      }

      @Override
      public T next() {
        if(!hasNext()){
          throw new NoSuchElementException();
        }
        return get(current++);
      }
    };
  }

  private Spliterator<T> split(int start, int end) {
    return new Spliterator<>() {
      private int tmp = start;
      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        if (tmp >= end) {
          return false;
        }
        action.accept(get(tmp++));
        return true;
      }

      @Override
      public Spliterator<T> trySplit() {
        var middle = (end + tmp) >>> 1;
        if (middle == tmp) {
          return null;
        }
        var s = split(tmp, middle);
        tmp = middle;
        return s;
      }

      @Override
      public long estimateSize() {
        return end - tmp;
      }

      @Override
      public int characteristics() {
        return IMMUTABLE | ORDERED | NONNULL | SIZED | SUBSIZED;
      }
    };
  }
  public Stream<T> stream() {
    return StreamSupport.stream(split(0, size()), false);
  }
}
