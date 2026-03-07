package com.example.LLD.ElevatorSystem.ElevatorSelectionStrategy;

import java.util.List;

import com.example.LLD.ElevatorSystem.Elevator;
import com.example.LLD.ElevatorSystem.Enums.Direction;

public class NearestElevatorStrategy implements ElevatorSelectionStrategy{

    @Override
    public Elevator select(List<Elevator> elevators, int floor, Direction direction) {
        int min = Integer.MAX_VALUE;
        Elevator best = null;

        for(Elevator elevator: elevators){
            int distance = Math.abs(elevator.getCurrentFloor()-floor);
            if(distance<min){
                min = distance;
                best = elevator;
            }
        }
        return best;
    }
    
}
