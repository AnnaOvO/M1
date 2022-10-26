package tp1;

public class TurtleRace {

	public static void main(String[] args) throws InterruptedException {
		  System.out.println("On your mark!");
		  Thread.sleep(30_000); //main wait 30s
		  System.out.println("Go!");
		  
		  int[] times = {25_000, 10_000, 20_000, 5_000, 50_000, 60_000};
		  
		  for(var j = 0; j<times.length; j++) {
	      var a = j;
	      //À l'intérieur d'une lambda, pas possible d'utiliser des variables déclarées à l'extérieur que si leur valeur ne change pas.
	      Runnable runnable = () -> {
	        try {
            Thread.sleep(times[a]); //threads wait times[a]s
          } catch (InterruptedException e) {
              throw new AssertionError(e);
          }
	        System.out.println("Turtle " + a + " has finished");
	          
	      };
	      Thread.ofPlatform().start(runnable); //thread start
		  }
		}
}
