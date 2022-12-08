package exo;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class FastestPooled {
  private final String item;
  private final int timeoutMilliPerRequest;
  private final int poolSize;

  private final static int NB_SITES = Request.ALL_SITES.size();

  public FastestPooled(String item, int timeoutMilliPerRequest, int poolSize) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    this.poolSize = poolSize;
  }

  public Optional<Answer> retrieve() throws InterruptedException, ExecutionException {
    var executorService = Executors.newFixedThreadPool(poolSize);
    var callables = new ArrayList<Callable<Answer>>();
    for(var site : Request.ALL_SITES){
      callables.add(() ->{
        var request = new Request(site, item);
        return request.request(timeoutMilliPerRequest);
      });
    }
    try {
      var answer = executorService.invokeAny(callables);
      return Optional.of(answer);
    } catch (ExecutionException e){
      return Optional.empty();
    } finally {
      executorService.shutdown();
    }
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    var agregator = new FastestPooled("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}