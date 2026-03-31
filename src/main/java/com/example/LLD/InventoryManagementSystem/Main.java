package com.example.LLD.InventoryManagementSystem;

import com.example.LLD.InventoryManagementSystem.Enums.ProductCategory;
import com.example.LLD.InventoryManagementSystem.InventoryStockManager.InventoryManager;
import com.example.LLD.InventoryManagementSystem.ProductFactory.Product;
import com.example.LLD.InventoryManagementSystem.ProductReplenishmentStrategy.ConcreteReplenishStrategies.BulkOrderStrategy;
import com.example.LLD.InventoryManagementSystem.ProductReplenishmentStrategy.ConcreteReplenishStrategies.JustInTimeStrategy;
import com.example.LLD.InventoryManagementSystem.ProductReplenishmentStrategy.ConcreteReplenishStrategies.ReplenishmentStrategy;
import com.example.LLD.InventoryManagementSystem.UtitlityClasses.Warehouse;

public class Main {
    public static void main(String[] args) {
        // Get the singleton instance of InventoryManager
        ReplenishmentStrategy replenishmentStrategy = new JustInTimeStrategy();
        InventoryManager inventoryManager = InventoryManager.getInstance(replenishmentStrategy);

        // Create and add warehouses
        Warehouse warehouse1 = new Warehouse("Warehouse 1");
        Warehouse warehouse2 = new Warehouse("Warehouse 2");
        inventoryManager.addWarehouse(warehouse1);
        inventoryManager.addWarehouse(warehouse2);

        // Create products using InventoryManager
        Product laptop = inventoryManager.createProduct(
                ProductCategory.ELECTRONICS, "SKU123", "Laptop", 1000.0, 50, 25);
        Product tShirt = inventoryManager.createProduct(
                ProductCategory.CLOTHING, "SKU456", "T-Shirt", 20.0, 200, 100);
        Product apple = inventoryManager.createProduct(
                ProductCategory.GROCERY, "SKU789", "Apple", 1.0, 100, 200);

        // Add products to warehouses through InventoryManager
        inventoryManager.addStock(warehouse1, laptop, 15);
        inventoryManager.addStock(warehouse1, tShirt, 20);
        inventoryManager.addStock(warehouse2, apple, 50);

        // Set replenishment strategy to Just-In-Time
        inventoryManager.setReplenishmentStrategy(new JustInTimeStrategy());

        // Perform inventory check and replenish if needed
        inventoryManager.performInventoryCheck();

        // Switch replenishment strategy to Bulk Order
        inventoryManager.setReplenishmentStrategy(new BulkOrderStrategy());

        // Replenish a specific product if needed
        inventoryManager.checkAndReplenish("SKU123");
    }
}
