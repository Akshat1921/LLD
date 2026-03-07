package com.example.LLD.ElevatorSystem;

import com.example.LLD.ElevatorSystem.ElevatorSelectionStrategy.NearestElevatorStrategy;
import com.example.LLD.ElevatorSystem.Enums.Direction;
import com.example.LLD.ElevatorSystem.Models.OuterPanel;

public class Main {

    public static void main(String[] args) {

        ElevatorManager manager =
        new ElevatorManager(new NearestElevatorStrategy());

        Elevator e1 = new Elevator(0, manager);
        Elevator e2 = new Elevator(5, manager);

        manager.addElevator(e1);
        manager.addElevator(e2);

        OuterPanel p1 = new OuterPanel(manager, 3);
        OuterPanel p2 = new OuterPanel(manager, 7);

        manager.addPanels(p1);
        manager.addPanels(p2);

        p1.requestElevator(Direction.UP);
        p2.requestElevator(Direction.DOWN);
    }
}