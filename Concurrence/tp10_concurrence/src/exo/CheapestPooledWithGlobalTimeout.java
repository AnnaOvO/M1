package exo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CheapestPooledWithGlobalTimeout {
  private final String item;
  private final int timeoutMilliPerRequest;
  private final int poolSize;

  private final static int NB_SITES = Request.ALL_SITES.size();

  private final int timeoutMilliGlobal;

  public CheapestPooledWithGlobalTimeout(String item, int timeoutMilliPerRequest, int poolSize, int timeoutMilliGlobal) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    this.poolSize = poolSize;
    this.timeoutMilliGlobal = timeoutMilliGlobal;
  }

  public Optional<Answer> retrieve() throws InterruptedException {
    var executorService = Executors.newFixedThreadPool(poolSize);
    var callables = new ArrayList<Callable<Answer>>();
    for(var site : Request.ALL_SITES){
      callables.add(() ->{
        var request = new Request(site, item);
        return request.request(timeoutMilliPerRequest);
      });
    }
    var futures = executorService.invokeAll(callables, timeoutMilliGlobal, TimeUnit.MILLISECONDS);
    executorService.shutdown();
    return futures.stream()
        .filter(future -> future.state() == Future.State.SUCCESS)
        .map(Future::resultNow)
        .filter(Answer::isSuccessful)
        .min(Comparator.comparingInt(Answer::price));
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooledWithGlobalTimeout("pikachu", 2_000, 5, 1_000);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}