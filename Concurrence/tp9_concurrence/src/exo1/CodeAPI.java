package exo1;

import java.util.Random;

public class CodeAPI {
  public static String receive() throws InterruptedException {
    var rng = new Random();
    Thread.sleep(100);
    return "Coded : " + rng.nextInt(10000);
  }

  public static String decode(String codedMsg) throws InterruptedException {
    Thread.sleep(300);
    if (!codedMsg.startsWith("Coded : ")) {
      throw new IllegalArgumentException("decode can only be applied to coded messages");
    }
    var number = Integer.parseInt(codedMsg.substring(8));
    if (number > 5000) {
      throw new IllegalArgumentException();
    }
    return "Decoded : " + number;
  }

  public static void archive(String decodedMsg) throws InterruptedException {
    Thread.sleep(50);
    System.out.println("Archiving : "+decodedMsg);
  }

  public static void main(String[] args) throws InterruptedException {
    System.out.println(decode(receive()));
  }
}
