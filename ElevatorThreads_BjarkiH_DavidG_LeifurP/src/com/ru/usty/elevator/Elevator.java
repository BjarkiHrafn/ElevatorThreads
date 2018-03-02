package com.ru.usty.elevator;

import java.util.concurrent.Semaphore;

public class Elevator implements Runnable {

	int CurrentFloorForElevator, NumberOfPeopleInElevator, key, currFloorEle, spaceLeft, numPeopleInElevator;
	static final int SLEEP_TIME = ElevatorScene.VISUALIZATION_WAIT_TIME / 2;
	
	public Elevator(int CurrentFloorForElevator, int NumberOfPeopleInElevator, int key) {
		this.CurrentFloorForElevator = CurrentFloorForElevator;
		this.NumberOfPeopleInElevator = NumberOfPeopleInElevator;
		this.key = key;
	}
	
	@Override
	public void run() {
		while(true) {
			///---- Stop The Program From Running Infinitely Begin----///
			if(ElevatorScene.elevatorsMayDie) {
				return; // this stops the program from running for an infinite time - Bjarki
			}
			///---- Stop The Program From Running Infinitely End----///

			
			///---- People Entering Elevator Begin ----///
			try {
				ElevatorScene.elevatorFloorMutex.acquire();	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Release permits for person threads entering elevator for current floor
			{
				ElevatorScene.scene.updateCurrentElevator(this.key);
				currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
				spaceLeft = (6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.key));
				ElevatorScene.inToElevatorFloorsSem.get(currFloorEle).release(spaceLeft);
			}
			
			// Sleep time proportional to visualization wait time, to make sure persons
			// have time to get into elevator
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			// Acquire permits back before next step
			try {
				currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
				spaceLeft = (6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.key));
				ElevatorScene.inToElevatorFloorsSem.get(currFloorEle).acquire(spaceLeft);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ElevatorScene.elevatorFloorMutex.release();
			///---- People Entering Elevator End ----///
			

			///---- Move Elevator Begin ----///
			/*
			 * Move elevator to up one floor until 
			 * final floor is reached, then reset elevator 
			 * to first floor.
			 */
			if(ElevatorScene.getCurrentFloorForElevator(this.key) < ElevatorScene.scene.getNumberOfFloors() - 1) {
				ElevatorScene.incrementCurrentElevatorFloor(this.key);
			}else {
				ElevatorScene.scene.setCurrentElevatorToFirstFloor(this.key);				
			}		
			///---- Move Elevator End ----///
			
			
			///---- People Leaving Elevator Begin ----///
			// Release permits for person threads leaving elevator for current floor
			{
				currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
				numPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(this.key);
				ElevatorScene.TwoD_ArrayOUT[this.key][currFloorEle].release(numPeopleInElevator);
				//System.out.println("Elevator.java says: number of people in elevator are" + numPeopleInElevator);
			}

			// Sleep time proportional to visualization wait time, to make sure persons
			// have time to get out of elevator
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			// Acquire permits back before next step
			try {
				numPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(this.key);
				ElevatorScene.TwoD_ArrayOUT[this.key][currFloorEle].acquire(numPeopleInElevator);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			///---- People Leaving Elevator End ----///
	
		}
	}
}

