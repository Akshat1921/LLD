package com.example.LLD.ElevatorSystem.State;

import com.example.LLD.ElevatorSystem.Elevator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoveDownState implements State{
    private Elevator elevator;

    @Override
    public void moveUp() {
        System.out.println("Cant go up while going down");
    }

    @Override
    public void moveDown() {
        int nextFloor = elevator.getCurrentFloor()-1;
        if(nextFloor>0){
            elevator.setCurrentFloor(nextFloor);
            System.out.println("Elevator moving Down -> floor " + nextFloor);
        }else{
            System.out.println("Cant go down more than ground floor");
        }
    }

    @Override
    public void stop() {
        System.out.println("Elevator stopped at floor " + elevator.getCurrentFloor());
        elevator.setState(new IdleState(elevator));
    }
    
}
