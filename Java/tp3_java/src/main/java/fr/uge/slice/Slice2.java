package fr.uge.slice;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public sealed interface Slice2<T> {

  public static <E> Slice2<E> array(E[] lst) {
    return new ArraySlice<E>(lst);
  };
  public static <E> Slice2<E> array(E[] lst, int from, int to) {
    var tmp = new ArraySlice<E>(lst);
    return tmp.new SubArraySlice(from, to);
  };

  Slice2<T> subSlice(int from, int to);
  int size();
  T get(int index); //type de T parce que c'est dans l'interface<T>
  String toString();

  /*Class ArraySlice*/
  final class ArraySlice<V> implements Slice2<V> {
    private final V[] lst;
    private ArraySlice(V[] lst) {
      Objects.requireNonNull(lst);
      this.lst = lst;
    }
    @Override
    public Slice2<V> subSlice(int from, int to) {
      return new SubArraySlice(from, to);
    }
    public int size(){
      return this.lst.length;
    }
    public V get(int index){
      if (index < 0 || index > size()){
        throw new IndexOutOfBoundsException();
      }
      return this.lst[index];
    }
    @Override
    public String toString(){
      return Arrays.stream(this.lst)
          .map(t -> t==null? "null":t.toString())
          .collect(Collectors.toList())
          .toString();
    }

    final class SubArraySlice implements Slice2<V>{
      private final int from;
      private final int to;

      private SubArraySlice(int from, int to){
        if (from > to || to > lst.length || from < 0){
          throw new IndexOutOfBoundsException();
        }
        this.from = from;
        this.to = to;
      }
      public int size() {
        return to-from;
      }

      public V get(int index) {
        Objects.checkIndex(index, size());
        return ArraySlice.this.lst[from+index];
      }
      @Override
      public Slice2<V> subSlice(int from, int to) {
        if (size() < to-from){
          throw new IndexOutOfBoundsException();
        }
        if (from < 0){
          throw new IndexOutOfBoundsException();
        }
        return new SubArraySlice(from+this.from, this.from+to);
      }
      @Override
      public String toString(){
        return Arrays.stream(lst, from, to)
            .map(t -> t==null? "null":t.toString())
            .collect(Collectors.toList())
            .toString();
      }
    }
  }
}
