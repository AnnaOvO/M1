package fr.uge.ymca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public final class House {
  
  private final ArrayList<People> peoples = new ArrayList<>();  
  private final HashSet<Kind> discounts = new HashSet<>();
  
  public void add(People p) {
    Objects.requireNonNull(p);
    peoples.add(p);
  }
  /*
  @Override
  public String toString() {
    var builder = new StringBuilder();
    
    if (villagePeoples.isEmpty()) {
      return "Empty House";
    }
    
    for(var p : villagePeoples) {
      builder.append(p.name());
      builder.append(", ");
    }
    return "House with " + builder.substring(0, builder.length()-2); 
  }
  */
  /* Q5
  public double averagePrice() {
    
    return peoples.stream().mapToInt(People::price).average().orElse(Double.NaN);
  }
  */
  /*Q7
  private int price(People p) {
    switch(p) {
    case VillagePeople vp -> { return 100;}
    case Minion m -> { return 1;}
    default -> { return 0;}
    }
  }
  */
  private int price(People p) {
    switch(p) {
    case VillagePeople vp -> { if(discounts.contains(vp.kind())) {return 20;} return 100;}
    case Minion m -> { return 1;}
    default -> { return 0;}
    }
  }
  
  public double averagePrice() {    
    return peoples.stream().mapToInt(this::price).average().orElse(Double.NaN);
  }
  
  public void addDiscount(Kind k) {
    Objects.requireNonNull(k);
    discounts.add(k);
  }
  public void removeDiscount(Kind k) {
    Objects.requireNonNull(k);
    if(discounts.isEmpty()) {
      throw new IllegalStateException();
    }
    discounts.remove(k);
  }
  @Override
  public String toString() {
    if(peoples.isEmpty()) {
      return "Empty House";
    }
    return "House with " + peoples.stream()
        .map(People::name)
        .sorted()
        .collect((Collectors.joining(", ")));
  }
  
}

