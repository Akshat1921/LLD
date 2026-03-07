package com.example.LLD.VendingMachine.UtilityClasses;

import java.util.ArrayList;
import java.util.List;

public class ItemShelf {
    private int code;
    private List<Item> items;
    private boolean isSoldOut;

    public ItemShelf(int code){
        this.code = code;
        this.items = new ArrayList<>();
        this.isSoldOut = false;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public boolean checkIsSoldOut() {
        return isSoldOut;
    }

    public void setIsSoldOut(boolean isSoldOut) {
        this.isSoldOut = isSoldOut;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        if(isSoldOut) setIsSoldOut(false); // Update the sold-out status when items are set
    }

    // Add an item to the shelf
    public void addItem(Item item) {
        items.add(item);
        if(isSoldOut) setIsSoldOut(false); // Update sold-out status after adding an item
    }

    // Remove an item from the shelf
    public void removeItem(Item item) {
        items.remove(item);
        if(items.isEmpty()) setIsSoldOut(true); // Update sold-out status after removing an item
    }
}
