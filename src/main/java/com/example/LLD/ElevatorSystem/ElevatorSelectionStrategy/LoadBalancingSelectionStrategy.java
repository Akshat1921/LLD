package com.example.LLD.ElevatorSystem.ElevatorSelectionStrategy;

import java.util.List;
import java.util.Random;

import com.example.LLD.ElevatorSystem.Elevator;
import com.example.LLD.ElevatorSystem.Enums.Direction;


public class LoadBalancingSelectionStrategy implements ElevatorSelectionStrategy{
    private Random random = new Random();
    @Override
    public Elevator select(List<Elevator> elevators, int floor, Direction direction){
        return elevators.get(random.nextInt(elevators.size()));
    }
}
