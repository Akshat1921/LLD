package com.example.LLD.VendingMachine.VendingMachineStates;

import java.util.ArrayList;
import java.util.List;

import com.example.LLD.VendingMachine.Enums.Coin;
import com.example.LLD.VendingMachine.UtilityClasses.Inventory;
import com.example.LLD.VendingMachine.UtilityClasses.Item;
import com.example.LLD.VendingMachine.VendingMachineStates.ConcreteStates.DispenseState;
import com.example.LLD.VendingMachine.VendingMachineStates.ConcreteStates.HasMoneyState;
import com.example.LLD.VendingMachine.VendingMachineStates.ConcreteStates.IdleState;
import com.example.LLD.VendingMachine.VendingMachineStates.ConcreteStates.SelectionState;

public class VendingMachineContext {
    private VendingMachineState currentState;
    private Inventory inventory;
    private int selectedItemCode;
    private List<Coin> coinList;

    public VendingMachineContext(){
        inventory = new Inventory(10);
        coinList = new ArrayList<>();
        currentState = new IdleState();
        System.out.println("Initialized: " + currentState.getStateName());
    }

    public VendingMachineState getCurrentState(){
        return currentState;
    }

    public void advanceState(){
        VendingMachineState nextState = currentState.next(this);
        currentState = nextState;
        System.out.println("Current state: " + currentState.getStateName());
    }

    public void clickOnInsertCoinButton(Coin coin){
        if(currentState instanceof IdleState || currentState instanceof HasMoneyState){
            System.out.println("Inserted " + coin.name() + " worth " + coin.value);
            coinList.add(coin);
            advanceState();
        }else{
            System.out.println("Cannot insert coin in " + currentState.getStateName());
        }
    }

    public void clickOnStartProductSelectionButton(int codeNumber){
        if(currentState instanceof HasMoneyState){
            advanceState();
            selectProduct(codeNumber);
        }else{
            System.out.println("Product selection button can only be clicked in HasMoney state");
        }
    }

    public void selectProduct(int codeNumber){
        if(currentState instanceof SelectionState){
            try{
                Item item = inventory.getItem(selectedItemCode);
                int balance = getBalance();
                if(balance<item.getPrice()){
                    System.out.println(
                            "Insufficient amount. Product price: " + item.getPrice() + ", paid: " + balance);
                    return;
                }
                setSelectedItemCode(codeNumber);
                advanceState();
                dispenseItem(codeNumber);
                if(balance>=item.getPrice()){
                    int change = balance-item.getPrice();
                    System.out.println("Returning change " + change);
                }
            }catch(Exception e){
                 System.out.println("Error: " + e.getMessage());
            }
        }else{
            System.out.println("Products can only be selected in Selection state");
        }
    }

    public void dispenseItem(int codeNumber){
        if(currentState instanceof DispenseState){
            try{
                Item item = inventory.getItem(codeNumber);
                System.out.println("Dispensing item " + item.getType());
                inventory.removeItem(codeNumber);
                inventory.updateSoldItem(codeNumber);
                resetBalance();
                resetSelection();
                advanceState();
            }catch(Exception e){
                System.out.println("Failed to Dispense the Product with code : " + codeNumber);
            }
        }else{
            System.out.println("System cannot dispense in : " + currentState);
        }
    }

    public void updateInventory(Item item, int codeNumber){
        if(currentState instanceof IdleState){
            try{
                inventory.addItem(item, codeNumber);
                System.out.println("Added " + item.getType() + " to slot " + codeNumber);
            }catch(Exception e){
                System.out.println("Error updating inventory: " + e.getMessage());
            }
        }else{
            System.out.println("Inventory can only be updated in Idle state");
        }
    }

    public void resetSelection(){
        this.selectedItemCode = 0;
    }

        // Calculates the total balance from inserted coins
    public int getBalance() {
        int balance = 0;
        for (Coin coin : coinList) {
            balance += coin.value; // Sum up the coin values
        }
        return balance;
    }

    // Resets the balance by clearing all coins
    public void resetBalance() {
        coinList.clear();
    }

    public void setCurrentState(VendingMachineState currentState) {
        this.currentState = currentState;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getSelectedItemCode() {
        return this.selectedItemCode;
    }

    public void setSelectedItemCode(int selectedItemCode) {
        this.selectedItemCode = selectedItemCode;
    }

    public List<Coin> getCoinList() {
        return this.coinList;
    }

    public void setCoinList(List<Coin> coinList) {
        this.coinList = coinList;
    }


}
