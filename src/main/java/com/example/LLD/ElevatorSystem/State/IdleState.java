package com.example.LLD.ElevatorSystem.State;

import com.example.LLD.ElevatorSystem.Elevator;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class IdleState implements State{
    private Elevator elevator;

    @Override
    public void moveUp() {
        elevator.setState(new MoveUpState(elevator));
        // elevator.getManager()
    }

    @Override
    public void moveDown() {
        elevator.setState(new MoveDownState(elevator));
    }

    @Override
    public void stop() {
        System.out.println("Elevator idle at floor " + elevator.getCurrentFloor());
    }
    
}
