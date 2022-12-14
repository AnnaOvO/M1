package fr.uge.seq;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Seq<T> implements Iterable<T> {
  //private final List<T> seq;
  private final List<?> seq; //Q5
  private final Function<Object, ? extends T> function;

  @SuppressWarnings("unchecked")
  private Seq(List<?> seq) {
    this.seq = List.copyOf(seq);
    this.function = x -> (T) x;
  }
  private Seq(List<?> seq, Function<Object, ? extends T> function) {
    this.seq = List.copyOf(seq);
    this.function = function;
  }

  public static <E>Seq<E> from(List<? extends E> lst) {
    Objects.requireNonNull(lst);
    return new Seq<>(lst);
  }

  public int size() {
    return seq.size();
  }

  public T get(int index) {
    Objects.checkIndex(index, seq.size());
    return function.apply(seq.get(index));
  }

  public String toString(){
    return seq.stream()
        .map(function)
        .map(Object::toString)
        .collect(Collectors.joining(", ", "<", ">"));
  }

  @SafeVarargs
  public static <E>Seq<E> of(E... lst) {
    Objects.requireNonNull(lst);
    return new Seq<>(List.of(lst));
  }
  public void forEach(Consumer<? super T> f) {
    Objects.requireNonNull(f);
    seq.stream().map(function).forEach(f);
  }

  public <E>Seq<E> map(Function<? super T, ? extends E> f) {
    Objects.requireNonNull(f);
    return new Seq<>(seq, function.andThen(f));
  }

  public Optional<T> findFirst() {
    return (seq.size() > 0) ? Optional.of(get(0)) : Optional.empty();
  }

  public Iterator<T> iterator() {
    return new Iterator<>() {
      private int current = 0;
      @Override
      public boolean hasNext() {
        return current < seq.size();
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

  private Spliterator<T> fromSplit(Spliterator<?> spliterator) {
    return new Spliterator<>() {
      @Override
      public boolean tryAdvance(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        return spliterator.tryAdvance(e -> action.accept(function.apply(e)));
      }

      @Override
      public Spliterator<T> trySplit() {
        var split = spliterator.trySplit();
        if (split == null) {
          return null;
        }
        return fromSplit(split);
      }

      @Override
      public long estimateSize() {
        return spliterator.estimateSize();
      }

      @Override
      public int characteristics() {
        return spliterator().characteristics() | IMMUTABLE | ORDERED | NONNULL | SIZED | SUBSIZED;
      }
    };
  }

  public Stream<T> stream() {
    var spliterator = seq.spliterator();
    return StreamSupport.stream(fromSplit(spliterator), false);
  }
}
