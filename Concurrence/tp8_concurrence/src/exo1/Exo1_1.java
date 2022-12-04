package exo1;

/* Exo 1
* Pourquoi n'est il pas possible d’arrêter un thread de façon non coopérative ?
  * Parce que habituellement, appeler interrupt() sur un thread veut dire qu'on lui demande de s'arrêter.
    Mais ce n'est qu'une convention, le code du thread peut faire ce que bon lui semble.
    Donc l’interruption d'un thread se fait de façon coopérative : le code du thread doit être écrit pour qu'il puisse s'interrompre.

* Rappeler ce qu'est une opération bloquante.
  * Une opération bloquante est un appel système ou de méthode qui peut rester bloqué dans l'attente d'une ressource.
    Normalement, ces opérations devraient lancer une InterruptedException.

* À quoi sert la méthode d'instance interrupt() de la classe Thread?
  * Un thread peut envoyer un signal d'interruption à un autre thread avec autreThread.interrupt().
    Si le thread n'est pas dans un appel bloquant, thread.interrupt() positionne le statut d'interruption à true.
    Si le thread est dans un appel de méthode bloquant, ou si un appel bloquant arrive après :
    l'appel lève une InterruptedException et le statut est repositionné à false.
*/

public class Exo1_1 {
  public static void main(String[] args) throws InterruptedException {
    var t1 = Thread.ofPlatform().start(() -> {
      for (var i = 1; ; i++) {
        try {
          Thread.sleep(1_000);
          System.out.println("Thread slept " + i + " seconds.");
        } catch (InterruptedException e) {
          return;
          //throw new AssertionError(e);
        }
      }
    });
    Thread.sleep(5_000); //ici le thread main attend 5 sec et interrupt le thread t1
    t1.interrupt(); // le thread t1 est interrupté
  }
}
