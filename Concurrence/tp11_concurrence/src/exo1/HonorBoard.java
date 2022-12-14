package exo1;

public class HonorBoard {
  private record Person(String firstName, String lastName){}
  private volatile Person person = new Person(null, null);

  public void set(String firstName, String lastName) {
    person = new Person(firstName, lastName);
  }

  @Override
  public String toString() {
    var person = this.person; // volatile rend
    return person.firstName + ' ' + person.lastName;
  }

  public static void main(String[] args) {
    var board = new HonorBoard();
    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Mickey", "Mouse");
      }
    });

    Thread.ofPlatform().start(() -> {
      for(;;) {
        board.set("Donald", "Duck");
      }
    });

    Thread.ofPlatform().start(() -> {
      for(;;) {
        System.out.println(board);
      }
    });
  }
}