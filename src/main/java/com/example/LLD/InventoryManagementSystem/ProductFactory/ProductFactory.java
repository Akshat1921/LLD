package com.example.LLD.InventoryManagementSystem.ProductFactory;

import com.example.LLD.InventoryManagementSystem.Enums.ProductCategory;
import com.example.LLD.InventoryManagementSystem.ProductFactory.ConcreteProducts.ClothingProduct;
import com.example.LLD.InventoryManagementSystem.ProductFactory.ConcreteProducts.ElectronicsProduct;
import com.example.LLD.InventoryManagementSystem.ProductFactory.ConcreteProducts.GroceryProduct;

public class ProductFactory {
    public Product createProduct(ProductCategory category, String sku, String name, double price, int quantity, int threshold){
        switch (category) {
            case ELECTRONICS:
                return new ElectronicsProduct(sku, name, price, quantity, threshold);
            case CLOTHING:
                return new ClothingProduct(sku, name, price, quantity, threshold);
            case GROCERY:
                return new GroceryProduct(sku, name, price, quantity, threshold);
            default:
                throw new IllegalArgumentException(
                        "Unsupported product category: " + category);
        }
    }
}
