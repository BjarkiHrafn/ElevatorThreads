package com.ru.usty.elevator;

import java.util.concurrent.Semaphore;

public class Elevator implements Runnable {

	int CurrentFloorForElevator, NumberOfPeopleInElevator, key;
	static final int SLEEP_TIME = ElevatorScene.VISUALIZATION_WAIT_TIME / 2;
	
	public Elevator(int CurrentFloorForElevator, int NumberOfPeopleInElevator, int key) {
		this.CurrentFloorForElevator = CurrentFloorForElevator;
		this.NumberOfPeopleInElevator = NumberOfPeopleInElevator;
		this.key = key;
	}
	
	@Override
	public void run() {
		while(true) {
			if(ElevatorScene.elevatorsMayDie) {
				return; // this stops the program from running for an infinite time - Bjarki
			}
			
			try {
				ElevatorScene.elevatorFloorMutex.acquire();
					ElevatorScene.updateCurrentElevator(key);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Permits for how many spaces there are left in elevator
			System.out.println("Elevator.java");
			ElevatorScene.inToElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(key)).release(6 - ElevatorScene.personCountInElevator.get(key));
			System.out.println("people released into elevator floor");	
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			// Fix permits back in case there are not 6 going into elevator
			try {
				ElevatorScene.inToElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(key)).acquire(6 - ElevatorScene.personCountInElevator.get(key));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ElevatorScene.elevatorFloorMutex.release();
						
			//for(int i = 0;i < ElevatorScene.scene.getNumberOfFloors();i++){
				// move elevator to next floor
			if(ElevatorScene.scene.getCurrentFloorForElevator(key) < ElevatorScene.scene.getNumberOfFloors() - 1) {
				System.out.println("now I can go to next floor");
				ElevatorScene.incrementCurrentElevatorFloor(key);
			}else {
				System.out.println("now I can go to ground floor");
				ElevatorScene.scene.currentElevatorToFirstFloor(key);				
			}
			

			
			ElevatorScene.outOfElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(key)).release(ElevatorScene.scene.getNumberOfPeopleInElevator(key));
			
			// release people from elevator
			System.out.println(ElevatorScene.personCountInElevator.get(key));
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			try {
				ElevatorScene.outOfElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(key)).acquire(ElevatorScene.personCountInElevator.get(key));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
		}
	}
}

