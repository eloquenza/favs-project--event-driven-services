package edu.hsh.favs.project.escqrs.services.businessintelligenceservice.analysis;

import edu.hsh.favs.project.escqrs.services.businessintelligenceservice.datatypes.EntityAnalyticWarehouse;

public class AmountOfProductsBoughtMarketAnalysis {

  private EntityAnalyticWarehouse<Long, Long> warehouse;

  public AmountOfProductsBoughtMarketAnalysis() {
    this.warehouse = new EntityAnalyticWarehouse<>(1L, 0L, (l, r) -> l + 1L, (l, r) -> l - 1L);
  }

  public void addProductIdEntry(Long orderId, Long productId) {
    warehouse.addValueEntry(orderId, productId);
  }

  public void removeProductIdEntry(Long orderId) {
    warehouse.removeValueEntry(orderId);
  }

  public void updateProductIdEntry(Long orderId, Long newProductId) {
    warehouse.updateValueEntry(orderId, newProductId);
  }

  @Override
  public String toString() {
    return "CustomerWithSpecificAgeMarketAnalysis{" + "warehouse=" + warehouse + '}';
  }
}
