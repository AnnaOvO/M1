package fr.uge.graph;

import java.util.*;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

final class MatrixGraph<T> implements Graph<T> {
  private final int capacity;
  private final T[] graph;
  public MatrixGraph(int capacity) {
    if (capacity < 0){
      throw new IllegalArgumentException();
    }
    this.capacity = capacity;
    this.graph = (T[]) new Object[capacity*capacity];
  }

  @Override
  public void addEdge(int src, int dst, T weight) {
    Objects.checkIndex(src, capacity);
    Objects.checkIndex(dst, capacity);
    Objects.requireNonNull(weight);
    graph[src + dst * capacity] = weight;
  }

  @Override
  public Optional<T> getWeight(int src, int dst) {
    Objects.checkIndex(src, capacity);
    Objects.checkIndex(dst, capacity);
    return Optional.ofNullable(graph[src + dst * capacity]);
  }
  public void edges(int src, EdgeConsumer<? super T> edgeConsumer){
    Objects.checkIndex(src, capacity);
    Objects.requireNonNull(edgeConsumer);
    for (var i = 0; i < capacity; i++){
      if(getWeight(src, i).isPresent()){
        edgeConsumer.edge(src, i, getWeight(src, i).get());
      }
    }
  }
  public Iterator<Integer> neighborIterator(int src){
    Objects.checkIndex(src, capacity);
    return new Iterator<Integer>() {
      int count = 0; //pour presenter dst
      @Override
      public boolean hasNext() {
        for (; count < capacity;){
          if(getWeight(src, count).isPresent()){
            return true;
          }
          count++;
        }
        return false;
      }

      @Override
      public Integer next() {
        if (!hasNext()){
          throw new NoSuchElementException();
        }
        return count++; //renvoie count mais le pointeur continu.

      }
    };
  }

  public IntStream neighborStream(int src) {
    Objects.checkIndex(src, capacity);
    var it = neighborIterator(src);
    return StreamSupport.intStream(new Spliterator.OfInt() {
      @Override
      public OfInt trySplit() {
        return null;
      }

      @Override
      public long estimateSize() {
        return Long.MAX_VALUE;
      }

      @Override
      public int characteristics() {
        return 0;
      }

      @Override
      public boolean tryAdvance(IntConsumer action) {
        if (it.hasNext()) {
          action.accept(it.next());
          return true;
        }
        return false;
      }
    }, false);
  }
}
