package com.example.LLD.ElevatorSystem.State;

import com.example.LLD.ElevatorSystem.Elevator;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MoveUpState implements State{
    private Elevator elevator;

    @Override
    public void moveUp() {
        int nextFloor = elevator.getCurrentFloor()+1;
        elevator.setCurrentFloor(nextFloor);
        System.out.println("Elevator moving UP -> floor " + nextFloor);
    }

    @Override
    public void moveDown() {
        System.out.println("While going up cant move down");
    }

    @Override
    public void stop() {
        System.out.println("Elevator is stopped at floor: " + elevator.getCurrentFloor());
        elevator.setState(new IdleState(elevator));
    }
}
