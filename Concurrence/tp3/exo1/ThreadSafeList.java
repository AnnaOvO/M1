package fr.uge.concurrence.exo1;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ThreadSafeList {
  private final ArrayList<Integer> list;
  private final Object lock = new Object(); //créer un lock

  public ThreadSafeList(int capacity){
    list = new ArrayList<>(capacity);
  }
  public void add(int value){
    synchronized (lock){
      list.add(value);
    }//délock automatique quand on sort les{}
  }
  public int size(){
    synchronized (lock) {
      return list.size();
    }
  }
  @Override
  public String toString(){
    synchronized (lock) {
      return list.stream()
          .map(e -> e.toString())
          .collect(Collectors.toList()).toString();
    }
  }
}
