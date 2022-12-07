package exo1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Codex {
  public static void main(String[] args) {
    var receive = 3;
    var decode = 2;

    BlockingQueue<String> queueReceive = new ArrayBlockingQueue<>(10);
    BlockingQueue<String> queueDecode = new ArrayBlockingQueue<>(10);
    BlockingQueue<String> queueArchive = new ArrayBlockingQueue<>(10);

    for (int i = 0; i < receive; i++) {
      Thread.ofPlatform().start(() -> {
        while (!Thread.interrupted()) {
          try {
            var message = CodeAPI.receive();
            queueReceive.put(message);
          } catch (InterruptedException e) {
            return;
          }
        }
      });
    }

    for (int i = 0; i < decode; i++) {
      Thread.ofPlatform().start(() -> {
        while (!Thread.interrupted()) {
          try {
            var message = queueReceive.take();
            var decodeMessage = CodeAPI.decode(message);
            queueDecode.put(decodeMessage);
          } catch (InterruptedException e) {
            return;
          }
        }
      });
    }

    Thread.ofPlatform().start(() -> {
      while (!Thread.interrupted()) {
        try {
          var message = queueDecode.take();
          CodeAPI.archive(message);
        } catch (InterruptedException e) {
          return;
        }
      }
    });
  }
}
/*Terminal exemple:
Archiving : Decoded : 2652
Archiving : Decoded : 2622
Exception in thread "Thread-4" java.lang.IllegalArgumentException
	at exo1.CodeAPI.decode(CodeAPI.java:19)
	at exo1.Codex.lambda$main$1(Codex.java:33)
	at java.base/java.lang.Thread.run(Thread.java:1589)
Exception in thread "Thread-3" java.lang.IllegalArgumentException
	at exo1.CodeAPI.decode(CodeAPI.java:19)
	at exo1.Codex.lambda$main$1(Codex.java:33)
	at java.base/java.lang.Thread.run(Thread.java:1589)
*/