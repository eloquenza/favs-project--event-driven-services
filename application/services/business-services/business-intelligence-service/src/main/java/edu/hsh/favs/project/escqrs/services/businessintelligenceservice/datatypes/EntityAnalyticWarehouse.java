package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class EntityAnalyticWarehouse<KeyT, ValueT> {

  private final Histogram<ValueT, KeyT> histogram;
  private final Map<KeyT, ValueT> entityInterestedValueMap;

  public EntityAnalyticWarehouse(
      KeyT stepValue,
      KeyT lowestValue,
      BinaryOperator<KeyT> incrementFunctor,
      BinaryOperator<KeyT> decrementFunctor) {
    this.histogram = new Histogram<>(stepValue, lowestValue, incrementFunctor, decrementFunctor);
    this.entityInterestedValueMap = new HashMap<>();
  }

  public KeyT getHistogramEntryForValue(ValueT value) {
    return histogram.getEntry(value);
  }

  public ValueT getMappedValue(KeyT key) {
    return entityInterestedValueMap.get(key);
  }

  public void addValueEntry(KeyT entityId, ValueT value) {
    entityInterestedValueMap.put(entityId, value);
    histogram.addEntry(value);
  }

  public void removeValueEntry(KeyT entityId) {
    ValueT valueToBeRemoved = entityInterestedValueMap.get(entityId);
    entityInterestedValueMap.remove(entityId);
    histogram.removeEntry(valueToBeRemoved);
  }

  public void updateValueEntry(KeyT entityId, ValueT newValue) {
    ValueT oldValue = entityInterestedValueMap.get(entityId);
    entityInterestedValueMap.replace(entityId, oldValue, newValue);
    histogram.removeEntry(oldValue);
    histogram.addEntry(newValue);
  }

  @Override
  public String toString() {
    return "EntityAnalyticWarehouse{" + "histogram=" + histogram + '}';
  }
}
