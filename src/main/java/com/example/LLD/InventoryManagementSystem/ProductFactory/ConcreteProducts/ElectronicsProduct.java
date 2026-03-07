package com.example.LLD.InventoryManagementSystem.ProductFactory.ConcreteProducts;

import com.example.LLD.InventoryManagementSystem.Enums.ProductCategory;
import com.example.LLD.InventoryManagementSystem.ProductFactory.Product;

public class ElectronicsProduct extends Product{
    private String brand;
    private int warrantyPeriod;

    public ElectronicsProduct(String sku, String name, double price, int quantity, int threshold) {
        super();
        setSku(sku);
        setName(name);
        setPrice(price);
        setQuantity(quantity);
        setCategory(ProductCategory.ELECTRONICS);
        setThreshold(threshold);
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getWarrantyPeriod() {
        return this.warrantyPeriod;
    }

    public void setWarrantyPeriod(int warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }
    
}
