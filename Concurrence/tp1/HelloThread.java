package tp1;

public class HelloThread {
	
	public static void main(String[] args) {
	
		for(var j = 0; j<4; j++) {
			var a = j;
			//À l'intérieur d'une lambda, pas possible d'utiliser des variables déclarées à l'extérieur que si leur valeur ne change pas.
			Runnable runnable = () -> {
				for(int i = 0; i <= 5000; i++) {
					System.out.println("hello " + a + " " + i);
				}
			};
			Thread thread = Thread.ofPlatform().start(runnable);
		
		}
		
	}

}

/*L'interface Runnable
Pour créer un Thread, il faut uniquement le code qu'il doit exécuter.
Un code est un objet implémentant l'interface fonctionnelle Runnable.*/

/*L'exécution de Thread n'est pas dans un ordre fixe, c'est presque 
 * quatre threads qui s'exécutent en même temps*/


 