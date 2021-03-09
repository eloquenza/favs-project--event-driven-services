package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.processing;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Histogram<KeyT> {

  private Map<KeyT, Long> histogram;

  public Histogram() {
    histogram = new TreeMap<>();
  }

  public Long addEntry(KeyT key) {
    return mergeEntries(key, 1L, (oldV, newV) -> oldV + 1L);
  }

  public Long removeEntry(KeyT key) {
    Long initialValue = 0L;
    return mergeEntries(key, initialValue, (oldV, newV) -> {
      // Make sure that it cannot reach below zero
      if (oldV == initialValue) {
        return initialValue;
      }
      return oldV - 1L;
    });
  }

  private Long mergeEntries(
      KeyT key, Long initialValue, BiFunction<Long, Long, Long> remappingFunction) {
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
