package tp1;

import java.util.ArrayList;

public class HelloThreadJoin {

	public static void main(String[] args) throws InterruptedException {
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		for(var j = 0; j<4; j++) {
			var a = j;
			//À l'intérieur d'une lambda, pas possible d'utiliser des variables déclarées à l'extérieur que si leur valeur ne change pas.
			Runnable runnable = () -> {
				for(int i = 0; i <= 5000; i++) {
					System.out.println("hello " + a + " " + i);
					
				}
			};
			Thread thread = Thread.ofPlatform().start(runnable);
			threads.add(thread);
			
		}
		
		for(var t : threads) {
			t.join();
		}
		
        System.out.println("Le thread a fini son Runnable");
		
	}
}
