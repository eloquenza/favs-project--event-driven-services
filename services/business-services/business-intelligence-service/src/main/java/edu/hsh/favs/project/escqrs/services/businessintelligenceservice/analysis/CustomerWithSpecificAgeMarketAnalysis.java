package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis;

import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.datatypes.EntityAnalyticWarehouse;

public class CustomerWithSpecificAgeMarketAnalysis {

  private EntityAnalyticWarehouse<Long, Integer> warehouse;

  public CustomerWithSpecificAgeMarketAnalysis() {
    this.warehouse = new EntityAnalyticWarehouse<>(1L, 0L, (l, r) -> l + 1L, (l, r) -> l - 1L);
  }

  public void addAgeEntry(Long customerId, Integer age) {
    warehouse.addValueEntry(customerId, age);
  }

  public void removeAgeEntry(Long customerId) {
    warehouse.removeValueEntry(customerId);
  }

  public void updateAgeEntry(Long customerId, Integer newAge) {
    warehouse.updateValueEntry(customerId, newAge);
  }

  @Override
  public String toString() {
    return "CustomerWithSpecificAgeMarketAnalysis{" + "warehouse=" + warehouse + '}';
  }
}
