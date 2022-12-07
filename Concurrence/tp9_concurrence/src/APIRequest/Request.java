package APIRequest;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Request {

  private final String site;
  private final String item;
  private final Object lock = new Object();
  private boolean started;

  private final static String[] ARRAY_ALL_SITES = { "amazon.fr", "amazon.uk", "darty.fr", "fnac.fr", "boulanger.fr",
      "cdiscount.fr", "tombeducamion.fr", "leboncoin.fr", "ebay.fr", "ebay.com", "laredoute.fr",
      "les3suisses.fr" };
  private final static Set<String> SET_ALL_SITES = Set.of(ARRAY_ALL_SITES);
  public final static List<String> ALL_SITES = Collections.unmodifiableList(List.of(ARRAY_ALL_SITES));

  public Request(String site, String item) {
    if (!SET_ALL_SITES.contains(site)) {
      throw new IllegalStateException();
    }
    this.site = site;
    this.item = item;
  }

  @Override
  public String toString() {
    return item + "@" + site;
  }

  /**
   * Performs the request the price for the item on the site waiting at most
   * timeoutMilli milliseconds. The returned Answered is not guaranteed to be
   * successful.
   *
   * This method can only be called once. All further calls will throw an
   * IllegalStateException
   *
   *
   * @param timeoutMilli
   * @throws InterruptedException
   */
  public Answer request(int timeoutMilli) throws InterruptedException {
    synchronized (lock) {
      if (started) {
        throw new IllegalStateException();
      }
      started = true;
    }
    System.out.println("DEBUG : starting request for " + item + " on " + site);
    if (item.equals("pokeball")) {
      System.out.println("DEBUG : " + item + " is not available on " + site);
      return Answer.empty();
    }
    long hash1 = Math.abs((site + "|" + item).hashCode());
    long hash2 = Math.abs((item + "|" + site).hashCode());
    if ((hash1 % 1000 < 400) || ((hash1 % 1000) * 2 > timeoutMilli)) { // simulating timeout
      Thread.sleep(timeoutMilli);
      System.out.println("DEBUG : Request " + toString() + " has timeout");
      return Answer.empty();
    }
    Thread.sleep((hash1 % 1000) * 2);
    if ((hash1 % 1000 < 500)) {
      System.out.println("DEBUG : " + item + " is not available on " + site);
      return Answer.empty();
    }
    int price = (int) (hash2 % 1000) + 1;
    System.out.println("DEBUG : " + item + " costs " + price + " on " + site);
    return Answer.of(site, item, price);
  }

  public static void main(String[] args) throws InterruptedException {
    var request = new Request("amazon.fr", "pikachu");
    var answer = request.request(5_000);
    if (answer.isSuccessful()) {
      System.out.println("The price is " + answer.price());
    } else {
      System.out.println("The price could not be retrieved from the site");
    }
  }
}

/*
* Terminal exemple:
DEBUG : starting request for pikachu on amazon.fr
DEBUG : Request pikachu@amazon.fr has timeout
DEBUG : starting request for pikachu on amazon.uk
DEBUG : pikachu costs 700 on amazon.uk
DEBUG : starting request for pikachu on darty.fr
DEBUG : pikachu costs 214 on darty.fr
DEBUG : starting request for pikachu on fnac.fr
DEBUG : pikachu costs 982 on fnac.fr
DEBUG : starting request for pikachu on boulanger.fr
DEBUG : Request pikachu@boulanger.fr has timeout
DEBUG : starting request for pikachu on cdiscount.fr
DEBUG : Request pikachu@cdiscount.fr has timeout
DEBUG : starting request for pikachu on tombeducamion.fr
DEBUG : Request pikachu@tombeducamion.fr has timeout
DEBUG : starting request for pikachu on leboncoin.fr
DEBUG : Request pikachu@leboncoin.fr has timeout
DEBUG : starting request for pikachu on ebay.fr
DEBUG : pikachu costs 891 on ebay.fr
DEBUG : starting request for pikachu on ebay.com
DEBUG : Request pikachu@ebay.com has timeout
DEBUG : starting request for pikachu on laredoute.fr
DEBUG : Request pikachu@laredoute.fr has timeout
DEBUG : starting request for pikachu on les3suisses.fr
DEBUG : Request pikachu@les3suisses.fr has timeout
Optional[pikachu@darty.fr : 214]

Process finished with exit code 0
*/