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
					
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ElevatorScene.scene.updateCurrentElevator(this.key);
			// Permits for how many spaces there are left in elevator
			//System.out.println("Elevator.java");
			ElevatorScene.inToElevatorFloorsSem.get(ElevatorScene.getCurrentFloorForElevator(this.key)).release(6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.key));
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			// Fix permits back in case there are not 6 going into elevator
			try {
				ElevatorScene.inToElevatorFloorsSem.get(ElevatorScene.getCurrentFloorForElevator(this.key)).acquire(6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.key));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ElevatorScene.elevatorFloorMutex.release();

			// Move elevator to up one floor until 
			// final floor is reached, then reset elevator 
			// to first floor.
			if(ElevatorScene.getCurrentFloorForElevator(this.key) < ElevatorScene.scene.getNumberOfFloors() - 1) {
				ElevatorScene.incrementCurrentElevatorFloor(this.key);
			}else {
				ElevatorScene.scene.setCurrentElevatorToFirstFloor(this.key);				
			}		
		
			int currElevatorFloor = ElevatorScene.getCurrentFloorForElevator(this.key);
			
			int numPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(this.key);
			//ElevatorScene.outOfElevatorFloorsSem.get(currElevatorFloor).release(numPeopleInElevator);
			//ElevatorScene.outOfElevatorFloorsSemTwoDemArr.get(key).get(currElevatorFloor).release(numPeopleInElevator);
			ElevatorScene.TwoD_ArrayOUT[this.key][currElevatorFloor].release(numPeopleInElevator);
			System.out.println("Elevator.java says: number of people in elevator are" + numPeopleInElevator);
			
			// Print out how many persons in elevator
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			try {
				//ElevatorScene.outOfElevatorFloorsSem.get(ElevatorScene.getCurrentFloorForElevator(key)).acquire(ElevatorScene.personCountInElevator.get(key));
				//ElevatorScene.outOfElevatorFloorsSemTwoDemArr.get(key).get(ElevatorScene.getCurrentFloorForElevator(key)).acquire(ElevatorScene.personCountInElevator.get(key));
				ElevatorScene.TwoD_ArrayOUT[this.key][currElevatorFloor].acquire(ElevatorScene.personCountInElevator.get(this.key));
				

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	
		}
	}
}

