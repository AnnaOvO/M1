package entropy;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public final class EntropySet<T> extends AbstractSet<T>  {
  private static final int CACHE_SIZE = 4;

  private final LinkedHashSet<T> set;
  private boolean frozen;
  private final T[] cache;

  @SuppressWarnings("unchecked")
  private EntropySet(Object[] cache, LinkedHashSet<T> set) {
    this.set = set;
    this.cache = (T[]) cache;
  }

  public EntropySet() {
    this(new Object[CACHE_SIZE], new LinkedHashSet<>());
  }

  @Override
  public boolean add(T val) {
    Objects.requireNonNull(val);
    checkFrozen();
    for(var i = 0; i < cache.length; i++) {
      var element = cache[i];
      if (element == null) {
        cache[i] = val;
        return false;
      }
      if (element.equals(val)) {
        return false;
      }
    }
    set.add(val);
    return false;
  }

  private void checkFrozen() {
    if (frozen) {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public int size() {
    frozen = true;
    for(var i = 0; i < cache.length; i++) {
      if (cache[i] == null) {
        return i;
      }
    }
    return CACHE_SIZE + set.size();
  }

  public boolean isFrozen() {
    return frozen;
  }

  @Override
  public boolean contains(Object object) {
    Objects.requireNonNull(object);
    frozen = true;
    for (var element : cache) {
      if (element == null) {
        return false;
      }
      if (element.equals(object)) {
        return true;
      }
    }
    return set.contains(object);
  }

  @Override
  public Iterator<T> iterator() {
    frozen = true;
    var size = this.size();
    var iterator = set.iterator();
    return new Iterator<>() {
      private int index;

      @Override
      public boolean hasNext() {
        return index < size;
      }

      @Override
      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        if (index < CACHE_SIZE) {
          return cache[index++];
        }
        index++;
        return iterator.next();
      }
    };
  }

  public static <E> EntropySet<E> from(Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    var spliterator = collection.spliterator();
    Function<Stream<? extends E>, Stream<? extends E>> function = s -> s;
    if (!spliterator.hasCharacteristics(Spliterator.NONNULL)) {
      function = function.andThen(s -> s.filter(Objects::nonNull));
    }
    if (!spliterator.hasCharacteristics(Spliterator.DISTINCT)) {
      function = function.andThen(Stream::distinct);
    }
    var cache = function.apply(collection.stream()).limit(CACHE_SIZE).toArray(size -> new Object[CACHE_SIZE]);
    var set = function.apply(collection.stream()).skip(CACHE_SIZE).collect(toCollection(LinkedHashSet<E>::new));
    return new EntropySet<>(cache, set);
  }
}
