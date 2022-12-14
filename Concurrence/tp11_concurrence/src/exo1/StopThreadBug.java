package exo1;
public class StopThreadBug {
  private boolean stop;
  public void runCounter() {
    var localCounter = 0;
    for(;;) {
      if (stop) {
        break;
      }
      localCounter++;
    }
    System.out.println(localCounter);
  }

  public void stop() {
    stop = true;
  }

  public static void main(String[] args) throws InterruptedException {
    var bogus = new StopThreadBug();
    var thread = Thread.ofPlatform().start(bogus::runCounter);
    Thread.sleep(100);
    bogus.stop();
    thread.join();
  }
}
/*
 * La data-race se trouve au niveau de stop
 *
 * On rend le champ stop volatile
 */