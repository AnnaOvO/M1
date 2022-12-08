package exo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// faire un submit


public class CheapestPooled2 {
  private final String item;
  private final int timeoutMilliPerRequest;
  private final int poolSize;

  private final static int NB_SITES = Request.ALL_SITES.size();

  public CheapestPooled2(String item, int timeoutMilliPerRequest, int poolSize) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    this.poolSize = poolSize;
  }

  public Optional<Answer> retrieve() throws InterruptedException {
    var executorService = Executors.newFixedThreadPool(poolSize);
    var callables = new ArrayList<Callable<Answer>>();
    var futures = Request.ALL_SITES.stream()
        .map(site -> executorService.submit(() ->{
          var reqest = new Request(site, item);
          return reqest.request(timeoutMilliPerRequest);
        })).toList();
    executorService.shutdown();

    var answers = new ArrayList<Answer>();
    Comparator<Answer> comparator = Comparator.comparingInt(Answer::price);
    for (Future<Answer> future : futures) {
      Answer answer;
      try {
        answer = future.get();
      } catch (ExecutionException e) {
        continue;
      }
      answers.add(answer);
    }

    return answers.stream().filter(Answer::isSuccessful).min(Comparator.comparingInt(Answer::price));
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooled2("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}
/*
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public record CheapestPool2(String item, int sizePool, int timeoutMilliPerRequest) {
  public Optional<Answer> retrieve() throws InterruptedException {
    var executer = Executors.newFixedThreadPool(sizePool);

    var futures =
        Request.ALL_SITES.stream()
                .map(site -> executer.submit(() -> {
                  var request = new Request(site, item);
                  return request.request(timeoutMilliPerRequest);
                }))
                .toList();
    executer.shutdown();

    var answers = new ArrayList<Answer>();
    for (var future : futures){
      Answer answer;
      try {
        answer = future.get();
      } catch (ExecutionException e) {
        continue;
      }
      answers.add(answer);
    }

    return answers.stream().filter(Answer::isSuccessful).min(Comparator.comparingInt(Answer::price));
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPool2("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}

import java.util.ArrayList;
    */