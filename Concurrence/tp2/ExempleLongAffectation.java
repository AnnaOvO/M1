package tp2;
class ExampleLongAffectation {
  long l = -1L;

  public static void main(String[] args) {
    var e = new ExampleLongAffectation();
    Thread.ofPlatform().start(() -> {
      System.out.println("l = " + e.l);
    });
    e.l = 0;
  }
}
/* Exercice 2
 * -1 (1111 1111)    0 (0000 0000)
 *    (1111 0000)
 *    (0000 1111)
 *
 */
