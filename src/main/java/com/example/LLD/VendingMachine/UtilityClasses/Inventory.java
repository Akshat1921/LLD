package com.example.LLD.VendingMachine.UtilityClasses;


public class Inventory {
    ItemShelf[] inventory = null;
    
    public Inventory(int itemCount){
        inventory = new ItemShelf[itemCount];
        initializeInventory();
    }


    public ItemShelf[] getInventory() {
        return this.inventory;
    }

    public void setInventory(ItemShelf[] inventory) {
        this.inventory = inventory;
    }


    public void initializeInventory(){
        int startCode = 101;
        for(int i=0; i<inventory.length; i++){
            ItemShelf space = new ItemShelf(startCode);
            inventory[i] = space;
            startCode++;
        }
    }

    public void addItem(Item item, int codeNumber)throws Exception{
        for(ItemShelf shelf: inventory){
            if(shelf.getCode() == codeNumber){
                shelf.addItem(item);
                return; 
            }
            throw new Exception("Invalid Code");
        }
    }

    public Item getItem(int codeNumber) throws Exception{
        for(ItemShelf shelf: inventory){
            if(shelf.getCode()==codeNumber){
                if(shelf.checkIsSoldOut()){
                    throw new Exception("Item already sold out");
                }else{
                    return shelf.getItems().get(0);
                }
            }
        }
        throw new Exception("Invalid Code");
    }

    public void updateSoldItem(int codeNumber) throws Exception{
        for(ItemShelf shelf: inventory){
            if(shelf.getCode()==codeNumber){
                if(shelf.getItems().isEmpty()){
                    shelf.setIsSoldOut(true);
                }
            }
        }
    }

    public void removeItem(int codeNumber) throws Exception{
        for(ItemShelf shelf: inventory){
            if(shelf.getCode()==codeNumber){
                shelf.getItems().remove(shelf.getItems().get(0));
            }
        }
    }

    public boolean hasItems() {
        for(ItemShelf itemShelf : inventory){
            if(!itemShelf.checkIsSoldOut()) return true;
        }
        return false;
    }

}
