package tp2;
class ExempleReordering {
  int a = 0;
  int b = 0;

  public static void main(String[] args) {
    var e = new ExempleReordering();
    Thread.ofPlatform().start(() -> {
      System.out.println("a = " + e.a + "  b = " + e.b);
    });
    e.a = 1;
    e.b = 2;
  }
}
/* Exercice 2
* Le résultat peut être des combinaison de a et b tel que
* a    b
* 0    0
* 1    0
* 1    2
* 0    2
*/