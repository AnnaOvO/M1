package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import fr.uge.concurrence.Heat4J;

public class ApplicationBis {
  private int numberOfRooms;
  private Object lock = new Object();
  private HashMap<String, Integer> listTemperature;

  public ApplicationBis(int numberOfRooms) throws InterruptedException {
    // constructeur avec un nombre de pièces fixé.
    synchronized (lock){
      if (numberOfRooms<0){
        throw new IllegalArgumentException();
      }
      this.numberOfRooms = numberOfRooms;
      listTemperature = new HashMap<>();
    }
  }

  // méthode permettant d'ajouter un température,seulement s'il n'y en a pas déjà numberOfRooms
  public void addTemperature(String room, int temperature) throws InterruptedException {
    synchronized (lock) {
      while(listTemperature.containsKey(room)) {
        lock.wait();
      }
      listTemperature.put(room, temperature);
      System.out.println("Temperature in room " + room + " : " + temperature);
      lock.notifyAll();
    }
  }

  // méthode qui attend d'avoir numberOfRooms températures
// pour renvoyer la moyenne des températures
  public double average() throws InterruptedException{
    synchronized (lock){
      while (listTemperature.size() < numberOfRooms){
        lock.wait();
      }
      var average = listTemperature.values().stream().mapToInt(Integer::intValue).average().getAsDouble();
      listTemperature.clear();
      System.out.println(average);
      return average;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");
    var temperatures = new ApplicationBis(rooms.size());

    for (String room : rooms) {
      Runnable runnable = () -> {
        while (true) {
          try {
            var temperature = Heat4J.retrieveTemperature(room);
            temperatures.addTemperature(room, temperature);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
      };
      Thread.ofPlatform().start(runnable);
    }
    while (!Thread.interrupted()){
      temperatures.average();
    }
  }
}
