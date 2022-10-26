package tp2;

public class StopThreadBug implements Runnable {
  private boolean stop = false;

  public void stop() {
    stop = true;
  }

  @Override
  public void run() {

    while (!stop) {
      //System.out.println("Up");
    }
    System.out.print("Done");
  }

  public static void main(String[] args) throws InterruptedException {
    var stopThreadBug = new StopThreadBug();
    Thread.ofPlatform().start(stopThreadBug::run);
    Thread.sleep(5_000);
    System.out.println("Trying to tell the thread to stop");
    stopThreadBug.stop();
  }
}
/* Exercice 2
* Le résultat est toujours le même:
* Up
* ...
* Up
* Trying to tell the thread to stop
* Up
* Done
* Process finished with exit code 0 */

/* La fonction exécute sans jamais arrêter. */

/* Non, parce que nous avons le possibilité de rencontrer une exception. */