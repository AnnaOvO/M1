package fr.uge.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JSONPrinter {

  private final static ClassValue<RecordComponent[]> CLASS_VALUE = new ClassValue<>() {
    @Override
    protected RecordComponent[] computeValue(Class<?> type) {
      return type.getRecordComponents();
    }
  };

  private final static ClassValue<List<Function<Record, String>>> CLASS_VALUE2 = new ClassValue<>() {
    @Override
    protected List<Function<Record, String>> computeValue(Class<?> type) {
      return Arrays.stream(type.getRecordComponents()).map(JSONPrinter::lambdaForList).collect(Collectors.toList());
    }
  };

  private static Function<Record, String> lambdaForList(RecordComponent recordComponent){
    return record -> takeName(record, recordComponent);
  }
  private static String escape(Object o) {
    return o instanceof String ? "\"" + o + "\"": "" + o;
  }
  private static Object myInvoke(Method accessor, Object o) {
    try {
      return accessor.invoke(o);
    } catch (InvocationTargetException | IllegalAccessException ex) {
      var cause = ex.getCause();
      if (cause instanceof RuntimeException exception) {
        throw exception;
      }
      if (cause instanceof Error error) {
        throw error;
      }
      throw new UndeclaredThrowableException(ex);
    }
  }

  public static String takeName(Record record, RecordComponent e){
    var invk = myInvoke(e.getAccessor(), record);
      if(e.isAnnotationPresent(JSONProperty.class)) {
        if (e.getAnnotation(JSONProperty.class).value().isEmpty()){
          return escape(e.getName().replace("_", "-")) + " : " + escape(invk);
        }
        else {
          return escape(e.getAnnotation(JSONProperty.class).value()) + " : " + escape(invk);
        }
      }
      else {
        return escape(e.getName()) + " : " + escape(invk);
      }
  }
  public static String toJSON(Record record){
    /*RecordComponent[] nameValue; // Renvoie un tableau des composants du record

    nameValue = CLASS_VALUE.get(record.getClass());
    var sj = new StringJoiner(",\n", "{\n", "\n}"); //StringJoiner(séparateur, prefix, suffix)

    Arrays.stream(nameValue).forEach(e -> {
      sj.add(takeName(record, e));
    });
    return sj.toString();*/
    return CLASS_VALUE2.get(record.getClass()).stream().map(c -> c.apply(record)).collect(Collectors.joining(",\n", "{\n", "\n}"));
  }

  public static void main(String[] args) {
    var person = new Person("John", "Doe");
    System.out.println(toJSON(person));

    var alien = new Alien(100, "Saturn");
    System.out.println(toJSON(alien));
  }
}