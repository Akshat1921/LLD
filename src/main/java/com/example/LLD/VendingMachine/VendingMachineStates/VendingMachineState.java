package com.example.LLD.VendingMachine.VendingMachineStates;

public interface VendingMachineState {
    String getStateName();
    VendingMachineState next(VendingMachineContext context);
}
