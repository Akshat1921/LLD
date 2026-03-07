package com.example.LLD.ElevatorSystem;

import java.util.LinkedList;
import java.util.Queue;

import com.example.LLD.ElevatorSystem.State.IdleState;
import com.example.LLD.ElevatorSystem.State.MoveDownState;
import com.example.LLD.ElevatorSystem.State.MoveUpState;
import com.example.LLD.ElevatorSystem.State.State;

import lombok.Data;

@Data
public class Elevator {
    private ElevatorManager manager;
    private State state;
    private int currentFloor;
    private Queue<Integer> queue;

    public Elevator(int currentFloor, ElevatorManager manager){
        this.manager = manager;
        this.currentFloor = currentFloor;
        this.state = new IdleState(this);
        queue = new LinkedList<>();
    }

    public void addToQueue(int floor){
        queue.add(floor);
    }

    public void processQueue(){
        while (!queue.isEmpty()) {
            int target = queue.poll();
            if(target>currentFloor){
                setState(new MoveUpState(this));
            }else if(target<currentFloor){
                setState(new MoveDownState(this));
            }

            while(currentFloor!=target){
                if(target>currentFloor){
                    state.moveUp();
                }else if(target<currentFloor){
                    state.moveDown();
                }
            }
            state.stop();
            manager.notifyObservers(this);
        }
    }

}
