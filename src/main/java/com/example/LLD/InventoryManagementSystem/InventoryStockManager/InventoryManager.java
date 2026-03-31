package com.example.LLD.InventoryManagementSystem.InventoryStockManager;

import java.util.ArrayList;
import java.util.List;

import com.example.LLD.InventoryManagementSystem.Enums.ProductCategory;
import com.example.LLD.InventoryManagementSystem.ProductFactory.Product;
import com.example.LLD.InventoryManagementSystem.ProductFactory.ProductFactory;
import com.example.LLD.InventoryManagementSystem.ProductReplenishmentStrategy.ConcreteReplenishStrategies.ReplenishmentStrategy;
import com.example.LLD.InventoryManagementSystem.UtitlityClasses.Warehouse;

public class InventoryManager {
   // Singleton instance
    private static InventoryManager instance;

    // System components
    private List<Warehouse> warehouses;
    private ProductFactory productFactory;
    private ReplenishmentStrategy replenishmentStrategy;

    // Private constructor to prevent instantiation from outside
    private InventoryManager(ReplenishmentStrategy replenishmentStrategy) {
        // Initialize collections and dependencies
        warehouses = new ArrayList<>();
        productFactory = new ProductFactory();
        this.replenishmentStrategy = replenishmentStrategy;
    }

    // Static method to get the singleton instance with thread safety
    public static synchronized InventoryManager getInstance(ReplenishmentStrategy replenishmentStrategy) {
        if (instance == null) {
            instance = new InventoryManager(replenishmentStrategy);
        }
        return instance;
    }

    // Strategy pattern method
    public void setReplenishmentStrategy(ReplenishmentStrategy replenishmentStrategy) {
        this.replenishmentStrategy = replenishmentStrategy;
    }

    // Warehouse management
    public void addWarehouse(Warehouse warehouse) {
        warehouses.add(warehouse);
    }

    public void removeWarehouse(Warehouse warehouse) {
        warehouses.remove(warehouse);
    }

    public Product createProduct(ProductCategory category, String sku, String name, double price,
            int quantity, int threshold) {
        return productFactory.createProduct(category, sku, name, price, quantity, threshold);
    }

    public void addStock(Warehouse warehouse, Product product, int quantity) {
        ensureWarehouseIsManaged(warehouse);
        warehouse.addProduct(product, quantity);
    }

    public boolean removeStock(Warehouse warehouse, String sku, int quantity) {
        ensureWarehouseIsManaged(warehouse);
        return warehouse.removeProduct(sku, quantity);
    }

    // Product inventory operations
    public Product getProductBySku(String sku) {
        for (Warehouse warehouse : warehouses) {
            Product product = warehouse.getProductBySku(sku);
            if (product != null) {
                return product;
            }
        }
        return null;
    }

    // Check stock levels and apply replenishment strategy if needed
    public void checkAndReplenish(String sku) {
        Product product = getProductBySku(sku);
        if (product != null) {
            // If product is below threshold
            if (product.getQuantity() < product.getThreshold()) {
                // Apply current replenishment strategy
                if (replenishmentStrategy != null) {
                    replenishmentStrategy.replenish(product);
                }
            }
        }
    }

    // Global inventory check
    public void performInventoryCheck() {
        for (Warehouse warehouse : warehouses) {
            for (Product product : warehouse.getAllProducts()) {
                if (product.getQuantity() < product.getThreshold()) {
                    if (replenishmentStrategy != null)  replenishmentStrategy.replenish(product);
                }
            }
        }
    } 

    private void ensureWarehouseIsManaged(Warehouse warehouse) {
        if (!warehouses.contains(warehouse)) {
            throw new IllegalArgumentException("Warehouse is not managed by InventoryManager");
        }
    }
}
