package fr.umlv.template;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Document<T> {
  private Template template;
  private List<? extends Function<? super T, ?>> functions;

  public Document(Template template, List<? extends Function<? super T, ?>> functions) {
    Objects.requireNonNull(template);
    Objects.requireNonNull(functions);
    if (functions.size() != template.fragments.size()-1){
      throw new IllegalArgumentException();
    }
    this.template = template;
    this.functions = functions;
  }

  String applyTemplate(T record) {
    Objects.requireNonNull(record);
    return template.interpolate(functions.stream().map(e->e.apply(record)).toList());
  }

  public String generate(List<? extends T> records, String s) {
    Objects.requireNonNull(records);
    Objects.requireNonNull(s);
    return records.stream().map(e -> applyTemplate(e)).collect(Collectors.joining(s));
  }

  public record Template(List<String> fragments){

    public Template {
      Objects.requireNonNull(fragments);
      if (fragments.isEmpty()){
        throw new IllegalArgumentException();
      }
      fragments = List.copyOf(fragments);
    }
    public static Template of(String s) {
      Objects.requireNonNull(s);
      int start = 0;
      var newS = s.split("");
      var str = new ArrayList<String>();
      for (int i = 0 ; i < newS.length; i++){
        if (newS[i].equals("@")){
          str.add(s.substring(start, i)); //la sous chaine.
          start = i+1;
        }
      }
      str.add(s.substring(start));
      return new Template(str);
    }

    @Override
    public String toString() {
      return fragments.stream().collect(Collectors.joining("@"));
    }

    public String interpolate(List<?> input) {
      Objects.requireNonNull(input);
      if (input.size() != fragments.size()-1){
        // fragments.size()-1 c'est le nbr de blanc.
        throw new IllegalArgumentException();
      }
      int j = 0;
      var builder = new StringBuilder();
      for (var i : input){
        builder.append(fragments.get(j));
        builder.append(i);
        j++;
      }
      builder.append(fragments.get(fragments.size()-1));
      return builder.toString();
    }

    public <E> Document<E> toDocument(Function<? super E, ?>... functions) {
      //premier <E> pour dire la methode utilise le type parametre <E>.
      //deuxiem <E> c'est pour dire le type de Document est <E>.
      Objects.requireNonNull(functions);
      return new Document<E>(this, Arrays.stream(functions).toList());
    }
    public <T> Template bind (T value){
      var val = Stream.concat(
          Stream.of(fragments.get(0).concat(String.valueOf(value)).concat(fragments.get(1))),
          fragments.subList(2, fragments.size()).stream()
      ).toList();
      return new Template(val);
    }
    @SafeVarargs
    public final <T> Template bind(T... values){
      Objects.requireNonNull(values);
      return Template.of(
          fragments.get(0).concat(String.join(" ", Arrays.stream(values).map(String::valueOf).toList())));
    }
    /*
    @SafeVarargs
    public final <R> Template bind(R... values){
      Objects.requireNonNull(values);
      return Template.of(
          fragments.get(0).concat(String.join(" ", Arrays.stream(values).map(v -> (v == null) ? "null" : v.toString()).toList())));
    }*/
  }

}
