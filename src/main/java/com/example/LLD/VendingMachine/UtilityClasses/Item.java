package com.example.LLD.VendingMachine.UtilityClasses;

import com.example.LLD.VendingMachine.Enums.ItemType;

public class Item {
    private ItemType type;
    private int price;

    public ItemType getType() {
        return this.type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
}

    
