package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Slice3<T> {
  public static <E> Slice3<E> array(E[] lst) {
    Objects.requireNonNull(lst);
    return new Slice3<E>() {
      public Slice3<E> subSlice(int from, int to) {
        if (from > to || to > lst.length || from < 0 || to < 0) {
          throw new IndexOutOfBoundsException();
        }
        Objects.checkFromToIndex(from, to, lst.length);
        //return une nouvelle methode
        return Slice3.array(lst, from, to);
      }

      public int size() {
        return lst.length;
      }

      public E get(int index) {
        if (index < 0 || index >= size()) {
          throw new IndexOutOfBoundsException();
        }
        return lst[index];
      }

      @Override
      public String toString() {
        return Arrays.stream(lst)
            .map(t -> t == null ? "null" : t.toString())
            .collect(Collectors.toList())
            .toString();
      }
    };
  }
  /*Q2*/
  public static <E> Slice3<E> array(E[] lst, int from, int to) {
    Objects.requireNonNull(lst);
    if (from > to || to > lst.length || from < 0 || to < 0) {
      throw new IndexOutOfBoundsException();
    }

    return new Slice3<E>() {
      public Slice3<E> subSlice(int subFrom, int subTo) {
        Objects.checkFromToIndex(subFrom, subTo, size());
        return Slice3.array(lst, from+subFrom, from+subTo);
      }

      public int size() {
        return to-from;
      }

      public E get(int index) {
        if (index < 0 || index >= size()) {
          throw new IndexOutOfBoundsException();
        }
        return lst[index+from];
      }

      @Override
      public String toString() {
        return Arrays.stream(lst, from, to)
            .map(t -> t == null ? "null" : t.toString())
            .collect(Collectors.toList())
            .toString();
      }
    };
  }
  Slice3<T> subSlice(int from, int to);
  int size();
  T get(int index); //type de T parce que c'est dans l'interface<T>
  String toString();
}