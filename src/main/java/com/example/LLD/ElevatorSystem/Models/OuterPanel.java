package com.example.LLD.ElevatorSystem.Models;

import com.example.LLD.ElevatorSystem.Elevator;
import com.example.LLD.ElevatorSystem.ElevatorManager;
import com.example.LLD.ElevatorSystem.Enums.Direction;
import com.example.LLD.ElevatorSystem.Observer.ElevatorObserver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OuterPanel implements ElevatorObserver{
    private ElevatorManager manager;
    private int floor;

    @Override
    public void update(Elevator elevator){
        System.out.println(
                "Panel floor " + floor +
                " notified: elevator now at " +
                elevator.getCurrentFloor()
        );
    }

    public void requestElevator(Direction direction){
        System.out.println("Floor " + floor + " requested elevator " + direction);
        manager.addToQueue(floor, direction);
    }

}
