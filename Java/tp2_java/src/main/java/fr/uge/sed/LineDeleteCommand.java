package fr.uge.sed;
import fr.uge.sed.StreamEditor;

public record LineDeleteCommand(int line)  {
  //type primitif donc on a besoin de IAE
  public LineDeleteCommand{
    if (line < 0){
      throw new IllegalArgumentException("line must be positive");
    }
  }
}
