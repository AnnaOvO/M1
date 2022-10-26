package tp2;

public class Counter {
    private int value;
    public void addALot() {
        for (var i = 0; i < 100_000; i++) {
            this.value++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var counter = new Counter();
        var thread1 = Thread.ofPlatform().start(counter::addALot);
        var thread2 = Thread.ofPlatform().start(counter::addALot);
        thread1.join();
        thread2.join();
        System.out.println(counter.value);
    }
}
/* Exercice 0
* On a observé que parfois c'est 200_000 et parfois moins de 200_000
* Parce que les 2 threads ne sont pas exécutés en meme temps, thread2 peut
*  déscheduler le thread1 donc  la valeur peut etre moin de 200_000. */

/*
* Oui c'est possible et le pire cas c'est 2. Quand thread2 run 99999 fois et
* thread1 run 1 seul fois, le thread2 va lire la valeur que thread1 écrit
* tout à l'heure donc c'est 1 (il va lire depuis la case mémoire donc c'est 1),
* et thread va finir son dernier run et la valeur c'est 1+1 = 2. */