package tp1;

public class HelloThreadBis {
  public static void println(String s){
    for(var i = 0; i < s.length(); i++){
      System.out.print(s.charAt(i));
    }
    System.out.print("\n");
  }
  
  public static void main(String[] args) {
    
    for(var j = 0; j<4; j++) {
      //À l'intérieur d'une lambda, pas possible d'utiliser des variables déclarées à l'extérieur que si leur valeur ne change pas.
      Runnable runnable = () -> {
        for(int i = 0; i <= 5000; i++) {
          println("hello " + i);
        }
      };
      Thread.ofPlatform().start(runnable);
    
    }
    
  }
}

/*Parfois, la chaîne est incomplète puis insérée par d'autres threads*/

/*
The println() method is similar to print() method except that it moves the cursor 
to the next line after printing the result. It is used when you want the result 
in two separate lines. It is called with "out" object.

If we want the result in two separate lines, then we should use the println() method. 
It is also an overloaded method of PrintStream class. It throws the cursor 
to the next line after displaying the result.*/