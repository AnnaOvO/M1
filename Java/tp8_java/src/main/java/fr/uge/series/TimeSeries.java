package fr.uge.series;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimeSeries<T> {

  private final ArrayList<Data<T>> timeSeries = new ArrayList<>();
  private long lastTimestamp = Long.MIN_VALUE;
  private int size = 0;

  public record Data<T>(long timestamp, T element){
    public Data{
      Objects.requireNonNull(element);
    }

    @Override
    public String toString() {
      //on va faire un toString ici pour la Q4
      return timestamp + " | " + element;
    }
  }

  public Index index() {
    return new Index(IntStream.range(0, size()).toArray());
  }

  public Index index(Predicate<? super T> f){
    //il prend T en paramètre, mais il peut prendre des types plus grands comme object donc c<est <? super T>.
    return new Index(IntStream.range(0, size()).filter(i -> f.test(timeSeries.get(i).element)).toArray());
  }

  class Index implements Iterable<Data<T>>{
    private final int[] indexs;

    private Index(int[] array) {

      indexs = array;
    }

    public int size() {
      return indexs.length;
    }

    @Override
    public String toString(){
      //parcourir les elements de timeseries et ajouter un \n à la fin de chaque element
      return Arrays.stream(indexs).mapToObj(e -> timeSeries.get(e).toString()).collect(Collectors.joining("\n"));
    }

    @Override
    public Iterator<Data<T>> iterator() {
      return new Iterator<Data<T>>() {
        int indice = 0;
        @Override
        public boolean hasNext() {
          return indice < indexs.length;
        }

        @Override
        public Data<T> next() {
          if (!hasNext()){
            throw new NoSuchElementException();
          }
          var valeur = timeSeries.get(indexs[indice]);
          indice++;
          return valeur;
        }
      };
    }

    public void forEach(Consumer<? super Data<T>> f) {
      Arrays.stream(indexs).mapToObj(timeSeries::get).forEach(f);
    }
    private TimeSeries<T> myTimeSeries(){
      /* La méthode renvoie l'adresse de TimeSeries
        var n1= new TimeSeries();
        var n2=new TimeSeries<>();
        var s1= n1.index();
        var s2=n2.index();
        System.out.println(s1.getTimeSeries());
        System.out.println(s2.getTimeSeries());
      */
      var i = TimeSeries.this;
      return TimeSeries.this;
    };
    public Index or(Index index) {
      Objects.requireNonNull(index);
      if (!this.myTimeSeries().equals(index.myTimeSeries())){
        throw new IllegalArgumentException();
      }
      return new Index(IntStream
          .concat(Arrays.stream(indexs), Arrays.stream(index.indexs)) //concatenate les 2 tableaux
          .sorted() //trier les integers
          .distinct() //supprimer les doublons
          .toArray()); //transformer à tableau
    }
    public Index and(Index index) {
      Objects.requireNonNull(index);
      if (!this.myTimeSeries().equals(index.myTimeSeries())){
        throw new IllegalArgumentException();
      }
      var index1 = Arrays.stream(index.indexs)
          .boxed()
          .collect(Collectors.toSet()); //toSet() supprimer les doublons mais elle a besoin integer c'est pour ça on a besoin boxed().
      return new Index(Arrays.stream(indexs)
          .filter(e -> index1.contains(e))
          .toArray()); //transformer à tableau
    }
  }

  public void add(long timestamp, T element) {
    if (lastTimestamp > timestamp){
      throw new IllegalStateException();
    }
    if (lastTimestamp < timestamp){
      var data = new Data<T>(timestamp, element);
      timeSeries.add(data);
      lastTimestamp = data.timestamp();
      size++;
    }
  }

  public int size() {
    return size;
  }

  public Object get(int index) {
    //if (index < 0 || index > size()){ throw new IndexOutOfBoundsException(); }
    Objects.checkIndex(index, size());
    return timeSeries.get(index);
  }

}