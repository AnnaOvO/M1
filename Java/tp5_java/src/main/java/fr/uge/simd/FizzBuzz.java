package fr.uge.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;
import java.util.Objects;

public class FizzBuzz {

  private final static VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
  public static int[] fizzBuzzVector128(int length) {
    if (length < 0){
      throw new IllegalArgumentException("Length can not be less than 0.");
    }
    var SPECIES = IntVector.SPECIES_128;
    var resultat = new int[length];
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    var v0 = IntVector.zero(SPECIES);
    for (; i < loopBound; i += SPECIES.length()) {
      var mask = SPECIES.indexInRange(i, length);
      var v = IntVector.fromArray(SPECIES, resultat, i, mask);
    }
    for (; i < length; i++) {
    }
    return resultat;
  }
  public static int[] fizzBuzzVector256(int length) {
    var species = IntVector.SPECIES_256;
    var fizzBuzzArray = new int[length];
    return fizzBuzzArray;
  }

  public static int[] fizzBuzzVector128AddMask(int length) {
    var fizzBuzzArray = new int[length];
    return fizzBuzzArray;
  }
}
