package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed interface Slice<T> {

  public static <E> Slice<E> array(E[] lst) {
    return new ArraySlice<E>(lst);
  };
  public static <E> Slice<E> array(E[] lst, int from, int to) {
    return new SubArraySlice<E>(lst, from, to);
  };

  Slice<T> subSlice(int from, int to);
  int size();
  T get(int index); //type de T parce que c'est dans l'interface<T>
  String toString();

  /*Class ArraySlice*/
  final class ArraySlice<V> implements Slice<V> {
    private final V[] lst ;

    private ArraySlice(V[] lst) {
      Objects.requireNonNull(lst);
      this.lst = lst;
    }

    @Override
    public Slice<V> subSlice(int from, int to) {
      return new SubArraySlice<>(lst, from, to);
    }

    public int size(){
      //var e = this; pour voir this c'est quoi
      return this.lst.length;
    }
    public V get(int index){
      if (index < 0 || index > size()){
        throw new IndexOutOfBoundsException();
      }
      return this.lst[index];
    }
    /*Q2 - toString*/
    @Override
    public String toString(){
      return Arrays.stream(this.lst)
          .map(t -> t==null? "null":t.toString())
          .collect(Collectors.toList())
          .toString();
    }
  }

  /*Class SubArraySlice*/
  /*Q3 - surcharge array*/
  final class SubArraySlice<V> implements Slice<V> {
    private final V[] lst;
    private final int from;
    private final int to;

    private SubArraySlice(V[] lst, int from, int to) {
      Objects.requireNonNull(lst);
      if (from < 0){
        throw new IndexOutOfBoundsException();
      }
      if (to > lst.length){
        throw new IndexOutOfBoundsException();
      }
      if (from > to){
        throw new IndexOutOfBoundsException();
      }
      this.lst = lst;
      this.from = from;
      this.to = to;
    }

    @Override
    public Slice<V> subSlice(int from, int to) {
      if (size() < to-from){
        throw new IndexOutOfBoundsException();
      }
      if (from < 0){
        throw new IndexOutOfBoundsException();
      }
      return new SubArraySlice<V>(lst, from+this.from, this.from+to);
    }

    public int size(){
      return to - from;
    }
    public V get(int index){
      if (index < 0 || index >= size()){
        throw new IndexOutOfBoundsException();
      }
      return this.lst[index+from];
    }

    @Override
    public String toString(){
      return Arrays.stream(this.lst, from, to)
          .map(t -> t==null? "null":t.toString())
          .collect(Collectors.toList())
          .toString();
    }
  }
}
