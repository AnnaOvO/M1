
public class Main {

	public static void main(String[] args) throws InterruptedException {
	    var thread = Thread.ofPlatform().start(() -> {
	      for (var i = 1;; i++) {
	        try {
	          Thread.sleep(1_000);
	          System.out.println("Thread slept " + i + " seconds.");
	        } catch (InterruptedException e) {
	          System.out.println(Thread.currentThread().getName() + " s'est interrompu");
	          return;
	        }
	      }
	    });
	    Thread.sleep(5_000);
	    thread.interrupt();
	  }
		
	}



/*Q1)*/
/*Q2) Une opération bloquante est une opération qui va interrompre un thread jusqu'a obtention de la ressource qu'il attend*/
/*Q3) la méthode interrupt envoie un signal au thread pour lui dire qu'on aimerait bien qu'il s'interrompe*/