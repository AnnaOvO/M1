package exo2;

import APIRequest.Answer;
import APIRequest.Request;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

public class Cheapest {
  private final String item;
  private final int timeoutMilliPerRequest;

  public Cheapest(String item, int timeoutMilliPerRequest) {
    this.item = item;
    this.timeoutMilliPerRequest = timeoutMilliPerRequest;
  }
  /**
   * @return the fastest price for item if it is sold
   */
  public Optional<Answer> retrieve() throws InterruptedException {
    var sites = Request.ALL_SITES;
    var queue = new ArrayBlockingQueue<Answer>(sites.size());
    for(var site : sites){
      Thread.ofPlatform().start(() -> {
        try {
          var answer = new Request(site, item).request(timeoutMilliPerRequest);
          queue.put(answer);
        } catch (InterruptedException e) {
          return;
        }
      });
    }
    var answers = new ArrayList<Answer>();
    for (var i = 0; i < sites.size(); i++){
      var answer = queue.take();
      if (answer.isSuccessful()){
        answers.add(answer);
      }
    }
    return answers.stream().min(Comparator.comparingInt(Answer::price));
  }

  public static void main(String[] args) throws InterruptedException {
    // Optional[tortank@laredoute.fr : 219]
    System.out.println(new Cheapest("tortank",2_000).retrieve());
  }
}
/*
* Terminal exemple:
DEBUG : starting request for tortank on amazon.fr
DEBUG : starting request for tortank on amazon.uk
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
DEBUG : tortank costs 254 on amazon.uk
DEBUG : tortank costs 976 on les3suisses.fr
DEBUG : tortank costs 219 on laredoute.fr
DEBUG : Request tortank@ebay.fr has timeout
DEBUG : Request tortank@cdiscount.fr has timeout
DEBUG : Request tortank@ebay.com has timeout
DEBUG : Request tortank@boulanger.fr has timeout
DEBUG : Request tortank@darty.fr has timeout
DEBUG : Request tortank@fnac.fr has timeout
Optional[tortank@laredoute.fr : 219]

Process finished with exit code 0
*/