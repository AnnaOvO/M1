package fr.uge.sed;

import java.util.Objects;
import java.util.regex.Pattern;

public record FindAndDeleteCommand(Pattern p)  {
  public FindAndDeleteCommand {
    Objects.requireNonNull(p);
  }
}
