package com.example.LLD.ElevatorSystem;

import java.util.ArrayList;
import java.util.List;

import com.example.LLD.ElevatorSystem.ElevatorSelectionStrategy.ElevatorSelectionStrategy;
import com.example.LLD.ElevatorSystem.Enums.Direction;
import com.example.LLD.ElevatorSystem.Models.OuterPanel;
import com.example.LLD.ElevatorSystem.Observer.ElevatorObserver;

import lombok.Data;

@Data
public class ElevatorManager {
    private List<OuterPanel> panels = new ArrayList<>();
    private List<Elevator> elevators = new ArrayList<>();
    private ElevatorSelectionStrategy strategy;
    private List<ElevatorObserver> observers = new ArrayList<>();

    public ElevatorManager(ElevatorSelectionStrategy strategy) {
        this.strategy = strategy;
    }

    public void notifyObservers(Elevator elevator){
        for(ElevatorObserver obs: observers){
            obs.update(elevator);
        }
    }

    public void addToQueue(int floor, Direction direction){
        Elevator elevator =  strategy.select(elevators, floor, direction);
        System.out.println("Selected elevator at floor " + elevator.getCurrentFloor());

        elevator.addToQueue(floor);
        elevator.processQueue();
    }

    public void addElevator(Elevator elevator){
        elevators.add(elevator);
    }

    public void addPanels(OuterPanel panel){
        panels.add(panel);
        observers.add(panel);
    }

}
