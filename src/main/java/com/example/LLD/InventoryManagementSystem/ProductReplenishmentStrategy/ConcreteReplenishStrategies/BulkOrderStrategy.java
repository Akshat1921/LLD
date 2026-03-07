package com.example.LLD.InventoryManagementSystem.ProductReplenishmentStrategy.ConcreteReplenishStrategies;

import com.example.LLD.InventoryManagementSystem.ProductFactory.Product;

public class BulkOrderStrategy implements ReplenishmentStrategy{
    @Override
    public void replenish(Product product) {
        // Implement Bulk Order replenishment logic
        System.out.println("Applying Bulk Order replenishment for " + product.getName());
        // Order in large quantities to minimize order costs
    }
}
