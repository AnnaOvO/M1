package fr.uge.concurrence;

import java.util.concurrent.ThreadLocalRandom;

public class Heat4J {

  /**
   * Return the temperature in Celcius of the sensor located in roomName. This
   * method is thread-safe.
   *
   * @param roomName name of the room equipped with a sensor
   */
  public static int retrieveTemperature(String roomName) throws InterruptedException {
    Thread.sleep(Math.abs(ThreadLocalRandom.current().nextInt() % 1000));
    return 10 + (ThreadLocalRandom.current().nextInt() % 20);
  }
}