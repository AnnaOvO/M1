package APIRequest;

import java.util.Comparator;
import java.util.Optional;

public class CheapestSequential {

  private final String item;
  private final int timeoutMilliPerRequest;

  public CheapestSequential(String item, int timeoutMilliPerRequest) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
  }

  /**
   * @return the cheapest price for item if it is sold
   */
  public Optional<Answer> retrieve() throws InterruptedException {
    var answerOptional = Request.ALL_SITES.stream()
        .map(site -> {
          try{
            return new Request(site, item).request(timeoutMilliPerRequest);
          } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return Answer.empty();
          }
        })
        .filter(Answer::isSuccessful)
        .min(Comparator.comparingInt(Answer::price));
    if (Thread.interrupted()){
      throw new InterruptedException();
    }
    return answerOptional;
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new CheapestSequential("pikachu", 2_000);
    var answer = agregator.retrieve();
    System.out.println(answer); // Optional[pikachu@darty.fr : 214]
  }
}
