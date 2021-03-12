package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.datatypes;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Histogram<KeyT, ValueT> {

  private final Map<KeyT, ValueT> histogram;
  private final ValueT stepValue;
  private final ValueT lowestValue;
  private final BinaryOperator<ValueT> incrementFunctor;
  private final BinaryOperator<ValueT> decrementFunctor;

  public Histogram(
      ValueT stepValue,
      ValueT lowestValue,
      BinaryOperator<ValueT> incrementFunctor,
      BinaryOperator<ValueT> decrementFunctor) {
    this.stepValue = stepValue;
    this.lowestValue = lowestValue;
    this.incrementFunctor = incrementFunctor;
    this.decrementFunctor = decrementFunctor;
    histogram = new TreeMap<>();
  }

  public ValueT getEntry(KeyT key) {
    return histogram.get(key);
  }

  public ValueT addEntry(KeyT key) {
    return mergeEntries(
        key, stepValue, (oldV, newV) -> this.incrementFunctor.apply(oldV, stepValue));
  }

  public ValueT removeEntry(KeyT key) {
    return mergeEntries(
        key,
        this.lowestValue,
        (oldV, newV) -> {
          // Make sure that it cannot reach below zero
          if (oldV.equals(this.lowestValue)) {
            return this.lowestValue;
          }
          return decrementFunctor.apply(oldV, stepValue);
        });
  }

  private ValueT mergeEntries(
      KeyT key, ValueT initialValue, BinaryOperator<ValueT> remappingFunction) {
    return histogram.merge(key, initialValue, remappingFunction::apply);
  }

  @Override
  public String toString() {
    return printHistogram();
  }

  private String printHistogram() {
    return this.histogram.keySet().stream()
        .map(key -> key + "=" + this.histogram.get(key))
        .collect(Collectors.joining(", ", "{", "}"));
  }
}
