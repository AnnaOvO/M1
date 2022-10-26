package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.List;

public class Application {
  private int numberOfRooms;
  private Object lock = new Object();
  private ArrayList<Integer> listTemperature;

  public Application(int numberOfRooms) throws InterruptedException {
    // constructeur avec un nombre de pièces fixé.
    synchronized (lock){
      if (numberOfRooms<0){
        throw new IllegalArgumentException();
      }
      this.numberOfRooms = numberOfRooms;
      listTemperature = new ArrayList<>(); //sinon listTemperature va etre null, on peux pas ajouter les int
    }

  }

  // méthode permettant d'ajouter un température,seulement s'il n'y en a pas déjà numberOfRooms
  public void addTemperature(String room, int temperature) throws InterruptedException {
    synchronized (lock) {
      listTemperature.add(temperature);
      numberOfRooms--;
      lock.notifyAll();
    }
  }

  // méthode qui attend d'avoir numberOfRooms températures
// pour renvoyer la moyenne des températures
  public double average() throws InterruptedException{

    synchronized (lock) {
      while (numberOfRooms != 0) {
        lock.wait();
      }
      return listTemperature.stream().mapToInt(Integer::intValue).average().getAsDouble();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var rooms = List.of("bedroom1", "bedroom2", "kitchen", "dining-room", "bathroom", "toilets");
    var temperatures = new Application(rooms.size());

    for (String room : rooms) {
      Runnable runnable = () -> {
        try {
          var temperature = Heat4J.retrieveTemperature(room);
          temperatures.addTemperature(room, temperature);
          System.out.println("Temperature in room " + room + " : " + temperature);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      };
      Thread.ofPlatform().start(runnable);
    }
    System.out.println(temperatures.average());
  }
}
