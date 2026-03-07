package com.example.LLD.ElevatorSystem.ElevatorSelectionStrategy;

import java.util.List;

import com.example.LLD.ElevatorSystem.Elevator;
import com.example.LLD.ElevatorSystem.Enums.Direction;

public interface ElevatorSelectionStrategy {
    Elevator select(List<Elevator> elevators, int floor, Direction direction);
}
