package APIRequest;
import java.util.Comparator;
import java.util.Objects;

public class Answer implements Comparable<Answer> {
  private final String site;
  private final String item;
  private final int price;

  /**
   * The Comparator used to compare Answers
   */
  public static Comparator<Answer> ANSWER_COMPARATOR = Comparator
      .comparing(Answer::price, Comparator.nullsLast(Comparator.naturalOrder()))
      .thenComparing(Answer::site, Comparator.naturalOrder())
      .thenComparing(Answer::item, Comparator.naturalOrder());

  private Answer(String site, String item, int price) {
    this.site = site;
    this.item = item;
    this.price = price;
  }

  private Answer() {
    this(null, null, -1);
  }

  static Answer empty() {
    return new Answer();
  }

  static Answer of(String site, String item, int price) {
    Objects.requireNonNull(site);
    Objects.requireNonNull(item);
    if (price < 0) {
      throw new IllegalStateException();
    }
    return new Answer(site, item, price);
  }

  /**
   * Test if this is the Answer of a successful request.
   * @return true if this is the Answer of a successful request, false otherwise
   */
  public boolean isSuccessful() {
    return price != -1;
  }

  /**
   * Return the price of a successful Answer.
   * @return the price of a successful Answer
   * @throws IllegalStateException if this Answer is not successful
   */
  public int price() {
    if (price == -1) {
      throw new IllegalStateException();
    }
    return price;
  }

  /**
   * Return the item of a successful Answer
   * @return the item of a successful Answer
   * @throws IllegalStateException if this Answer is not successful
   */
  public String item() {
    if (item == null) {
      throw new IllegalStateException();
    }
    return item;
  }

  /**
   * Return the site of a successful Answer
   * @return the site of a successful Answer
   * @throws IllegalStateException if this Answer is not successful
   */
  public String site() {
    if (site == null) {
      throw new IllegalStateException();
    }
    return site;
  }

  @Override
  public String toString() {
    return item + "@" + site + " : " + ((price == -1) ? "Not found" : price);

  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Answer answer)) {
      return false;
    }
    return price == answer.price && site.equals(answer.site) && item.equals(answer.item);
  }

  @Override
  public int hashCode() {
    int result = site.hashCode();
    result = 31 * result + item.hashCode();
    result = 31 * result + price;
    return result;
  }

  @Override
  public int compareTo(Answer o) {
    return ANSWER_COMPARATOR.compare(this, o);
  }
}