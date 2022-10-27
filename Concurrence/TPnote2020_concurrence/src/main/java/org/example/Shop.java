package org.example;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
public class Shop<E> {

  private final Object lock = new Object();
  private final HashMap<E, Integer> listFruits;

  public Shop(Set<E> items) {
    synchronized (lock) {
      listFruits = new HashMap<>();
      for(var item : items){
        listFruits.put(item, 0);
      }
      System.out.println(listFruits);
    }
  }

  public static <T> Shop<T> launch(Set<T> items) {
      Objects.requireNonNull(items);
      return new Shop<>(items);
  }

  public boolean restock(E item, int count) throws InterruptedException {
    Objects.requireNonNull(item);
    if (count <= 0){
      throw new IllegalArgumentException();
    }
    synchronized (lock) {
      if (!listFruits.containsKey(item)) {
        throw new IllegalArgumentException();
      }
      while (isInStock(item)){
        lock.wait();
      }
      listFruits.put(item, listFruits.get(item)+count);
      lock.notifyAll();
      return true;
    }
  }

  public boolean isInStock(E item) throws InterruptedException {
    Objects.requireNonNull(item);
    synchronized (lock) {
      if (listFruits.get(item) > 0) {
        return true;
      }
      return false;
    }
  }

  public int buy(E item) throws InterruptedException {
    Objects.requireNonNull(item);
    synchronized (lock) {
      if (!listFruits.containsKey(item)) {
        throw new IllegalArgumentException();
      }
      while (!isInStock(item)) {
        lock.wait();
      }
      listFruits.put(item, listFruits.get(item)-1); //mise a jour la quantite
      lock.notifyAll();
      return listFruits.get(item);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    String[] fruits = { "Apricot", "Cherry", "Raspberry", "Strawberry",
        "Rhubard", "Quince", "Apple", "Blackberry" };
    var shop = Shop.launch(Set.of(fruits));
    int nbThread = 5;
    for (var fruit : fruits) {
      Runnable runnable = () -> {
        while (true) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          try {
            var i = ((int) (Math.random()*4)) +1;
            shop.restock(fruit, i);
            System.out.println("----> " + Thread.currentThread().getName() + " " + "restocks" + " " + fruit + " " + i);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      };
      Thread.ofPlatform().start(runnable);
    }
    for (var i = 0; i < nbThread; i++) {
      Runnable runnable = () -> {
        while (true) {
          var fruitRandom = fruits[(int) (Math.random() * fruits.length)];
          try {
            shop.buy(fruitRandom);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          System.out.println(Thread.currentThread().getName() + " buys one " + fruitRandom);
        }
      };
      Thread.ofPlatform().start(runnable);
    }
  }
}

//Math.random()是令系统随机选取大于等于0.0 且小于1.0 的伪随机double 值.