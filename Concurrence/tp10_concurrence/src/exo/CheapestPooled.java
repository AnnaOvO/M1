package exo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public record CheapestPooled(String item, int sizePool, int timeoutMilliPerRequest) {
  public Optional<Answer> retrieve() throws InterruptedException {
    var executer = Executors.newFixedThreadPool(sizePool);
    var callables = new ArrayList<Callable<Answer>>();
    for (var site : Request.ALL_SITES){
      callables.add(() -> {
        var request = new Request(site, item);
        return request.request(timeoutMilliPerRequest);
      });
    }
    var futures = executer.invokeAll(callables);
    executer.shutdown();
    return futures.stream()
        //.filter(future -> future.state() == Future.State.SUCCESS)
        .map(Future::resultNow)
        .filter(Answer::isSuccessful)
        .min(Comparator.comparingInt(Answer::price));
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestPooled("pikachu", 2_000, 5);
    var answer = agregator.retrieve();
    System.out.println(answer);
  }
}