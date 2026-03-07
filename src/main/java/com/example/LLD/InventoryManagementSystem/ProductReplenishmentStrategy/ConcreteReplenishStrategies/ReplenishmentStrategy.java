package com.example.LLD.InventoryManagementSystem.ProductReplenishmentStrategy.ConcreteReplenishStrategies;

import com.example.LLD.InventoryManagementSystem.ProductFactory.Product;

public interface ReplenishmentStrategy {
    void replenish(Product product);
}
