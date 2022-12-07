import APIRequest.Answer;
import APIRequest.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.IntStream;

public class CheapestPooled {
  private final String item;
  private final int timeoutMilliPerRequest;
  private final int poolSize;

  private final static int NB_SITES = Request.ALL_SITES.size();

  private final ArrayBlockingQueue<Request> sitesQueue = new ArrayBlockingQueue<>(10);
  private final ArrayBlockingQueue<Answer> answersQueue = new ArrayBlockingQueue<>(10);

  public CheapestPooled(String item, int timeoutMilliPerRequest, int poolSize) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    this.poolSize = poolSize;
  }

  public Optional<Answer> retrieve() throws InterruptedException {
    var threads = new Thread[poolSize];
    IntStream.range(0, poolSize).forEach(j ->
        threads[j] = Thread.ofPlatform().start(() -> {
          while (!Thread.interrupted()) {
            try {
              var request = sitesQueue.take();
              answersQueue.put(request.request(timeoutMilliPerRequest));
            } catch (InterruptedException e) {
              return;
            }
          }
        }));
    for (var s : Request.ALL_SITES) {
      sitesQueue.put(new Request(s, item));
    }
    try {
      var answers = new ArrayList<Answer>();
      for (int i = 0; i < NB_SITES; i++) {
        answers.add(answersQueue.take());
      }
      return answers.stream().filter(Answer::isSuccessful).min(Answer.ANSWER_COMPARATOR);
    } finally {
      Arrays.stream(threads).forEach(Thread::interrupt);
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooled("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}