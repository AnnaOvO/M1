package fr.uge.simd;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;


import java.util.Objects;

public class VectorComputation {

  private final static VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
  public static int sum(int[] array) {
    Objects.requireNonNull(array);
    var length = array.length;
    // loopBound permet de savoir jusqu'à où on peut vectoriser l'array
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    // la methode .zero est équivalente à IntVector.broadcast(species, 0);
    var v0 = IntVector.zero(SPECIES);
    for (; i < loopBound; i += SPECIES.length()) {
      // chunk vector of array
      var v = IntVector.fromArray(SPECIES, array, i);
      // on additionne le contenu des lanes entre eux
      v0 = v0.add(v);
    }
    // ici on additionne les lanes entre elles
    int sum = v0.reduceLanes(VectorOperators.ADD);
    // La post-loop qui nous permet de finir le traitement complet de l'array
    for (; i < length; i++) { // ajouter les restes nombres
      sum += array[i];
    }
    return sum;
  }
  public static int sumMask(int[] array) {
    Objects.requireNonNull(array);
    var length = array.length;
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    var v0 = IntVector.zero(SPECIES);
    for (; i < loopBound; i += SPECIES.length()) {
      var v = IntVector.fromArray(SPECIES, array, i);
      v0 = v0.add(v);
    }
    var mask = SPECIES.indexInRange(i, length);
    var v = IntVector.fromArray(SPECIES, array, i, mask);
    v = v.add(v0); // on ajoute les restes int avec v0
    int sum = v.reduceLanes(VectorOperators.ADD);
    return sum;
  }

  public static int min(int[] array) {
    Objects.requireNonNull(array);
    var length = array.length;
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    int min = Integer.MAX_VALUE;
    for (; i < loopBound; i += SPECIES.length()) {
      var v = IntVector.fromArray(SPECIES, array, i);
      min = Integer.min(v.reduceLanes(VectorOperators.MIN), min);
    }
    for (; i < length; i++) { // ajouter les restes nombres
      if (array[i] < min){
        min = Integer.min(array[i], min);
      }
    }
    return min;
  }
  public static int minMask(int[] array) {
    Objects.requireNonNull(array);
    var length = array.length;
    var loopBound = length - length % SPECIES.length();
    var i = 0;
    int min = Integer.MAX_VALUE;
    var v0 = IntVector.zero(SPECIES);
    for (; i < loopBound; i += SPECIES.length()) {
      var v = IntVector.fromArray(SPECIES, array, i);
      min = Integer.min(v.reduceLanes(VectorOperators.MIN), min);
    }
    var mask = SPECIES.indexInRange(i, length);
    var v = IntVector.fromArray(SPECIES, array, i, mask);
    min = Integer.min(v.reduceLanes(VectorOperators.MIN, mask), min);// ici on peut utiliser le mask pour avoir la valeur min de l'array
    return min;
  }
}
