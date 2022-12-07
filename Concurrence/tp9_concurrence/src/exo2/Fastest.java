package exo2;

import APIRequest.Answer;
import APIRequest.Request;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public class Fastest {
  private final String item;
  private final int timeoutMilliPerRequest;

  public Fastest(String item, int timeoutMilliPerRequest) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
  }
  /**
   * @return the fastest price for item if it is sold
   */
  public Optional<Answer> retrieve() throws InterruptedException {
    var sites = Request.ALL_SITES;
    var queue = new ArrayBlockingQueue<Answer>(sites.size());
    var threads = Request.ALL_SITES.stream()
        .map(site -> Thread.ofPlatform().start(() -> {
          Answer answer = null;
          try {
            answer = new Request(site, item).request(timeoutMilliPerRequest);
            queue.put(answer);
          } catch (InterruptedException e) {
            return;
          }
        }))
        .toList();
    for (var i = 0; i < sites.size(); i++){
      var answer = queue.take();
      if (answer.isSuccessful()){
        for (var thread : threads){
          thread.interrupt();
        }
        return Optional.of(answer);
      }
    }
    return Optional.empty();
  }

  public static void main(String[] args) throws InterruptedException {
    var agregator = new Fastest("tortank", 2_000);
    var answer = agregator.retrieve();
    System.out.println(answer); // Optional[tortank@... : ...]
  }
}
/*
* Terminal exemple:
DEBUG : starting request for tortank on amazon.uk
DEBUG : starting request for tortank on amazon.fr
DEBUG : starting request for tortank on darty.fr
DEBUG : starting request for tortank on fnac.fr
DEBUG : starting request for tortank on boulanger.fr
DEBUG : starting request for tortank on cdiscount.fr
DEBUG : starting request for tortank on tombeducamion.fr
DEBUG : starting request for tortank on leboncoin.fr
DEBUG : starting request for tortank on ebay.fr
DEBUG : starting request for tortank on ebay.com
DEBUG : starting request for tortank on laredoute.fr
DEBUG : starting request for tortank on les3suisses.fr
DEBUG : tortank costs 427 on tombeducamion.fr
DEBUG : tortank costs 221 on leboncoin.fr
DEBUG : tortank costs 796 on amazon.fr
Optional[tortank@tombeducamion.fr : 427]

Process finished with exit code 0
*/