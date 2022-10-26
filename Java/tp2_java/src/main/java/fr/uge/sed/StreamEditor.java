package fr.uge.sed;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class StreamEditor {
  private final Command command;

public sealed interface Action {
  // sealed parce que on a utiliser le switch et case Action.xxx
  record DeleteAction() implements Action {}
  record PrintAction(String text) implements Action {}
}
  public StreamEditor(){
    this.command = (String s, Integer i) -> {
      return new Action.PrintAction(s); //Au début on n'a rien à delete
    };
  }

  public StreamEditor(Command command){
    this.command = command;
  }

  /*Q6*/
  @FunctionalInterface
  public interface Command {
    Action selectCommand(String s, Integer i);
  }

  public void transform(LineNumberReader numberReader, Writer w) throws IOException {
    Objects.requireNonNull(numberReader);
    Objects.requireNonNull(w);
    String e; //stocker les lignes
    while ((e=numberReader.readLine())!=null){
      /*
      if (numberReader.getLineNumber()!=command.line()){
        w.append(e);
        w.append("\n");
      }*/
      switch (command.selectCommand(e, numberReader.getLineNumber())) {
        case Action.DeleteAction deleteAction -> { }
        case Action.PrintAction printAction -> {
          /*
          if (!findAndDeleteCommand.p().matcher(e).find()){
            //trouver si la commande on veut delete est égale à la command dans e
            w.append(printAction.text());//Parce que ici n'est plus un e
            w.append("\n");
          }*/
        }
      }
    }
  }

  public static Command lineDelete(int i){
    if (i<0){
      throw new IllegalArgumentException();
    }
    return (String s, Integer n) -> {
      if (n == i)
        return new Action.DeleteAction();
      return new Action.PrintAction(s);
    };
  }
  public static Command findAndDelete(Pattern p){
    Objects.requireNonNull(p);
    return (String s, Integer i) -> {
      if(p.matcher(s).find()){
        return new Action.DeleteAction();
      }
      return new Action.PrintAction(s);
    };
  }

  /*Q8*/
  public static Command substitute(Pattern pattern, String replacement) {
    Objects.requireNonNull(pattern);
    Objects.requireNonNull(replacement);
    return (String s, Integer i) -> {
      return new Action.PrintAction(pattern.matcher(s).replaceAll(replacement));
      //matcher nous aide trouver si s(line actuel) est égale à pattern
    };
  }
  public static void main(String[] args) {
    var nomFichier = args[0];
    var deleteLine = StreamEditor.lineDelete(2);

    try(var fichier = Files.newBufferedReader(Path.of(nomFichier));
        Writer out = new BufferedWriter(new OutputStreamWriter(System.out))) {
        //write(output))
      var output = new StreamEditor(deleteLine); //new class StreamEditor (methodes+champs)


      var line = new LineNumberReader(fichier); //read number
      output.transform(line, out);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

}

