package com.ru.usty.elevator;

import java.util.concurrent.Semaphore;

public class Elevator implements Runnable {

	int CurrentFloorForElevator, NumberOfPeopleInElevator, key, currFloorEle, spaceLeft, numPeopleInElevator;
	static final int SLEEP_TIME = ElevatorScene.VISUALIZATION_WAIT_TIME / 2;
	static int topCounterServicer = 0;
	static int bottomCounterService = ElevatorScene.scene.getNumberOfFloors() - 1;
	boolean dir = true;
	
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

			currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
			if(currFloorEle == 0) {
				dir = true;
			}
			else if(currFloorEle == ElevatorScene.scene.getNumberOfFloors() - 1) {
				dir = false;
			}
			
			
			///---- People Entering Elevator Begin ----///
			try {
				ElevatorScene.elevatorFloorMutexArr.get(currFloorEle).acquire();	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// Release permits for person threads entering elevator for current floor
			{
				ElevatorScene.scene.setCurrentElevatorAtFloor(ElevatorScene.getCurrentFloorForElevator(this.key), this.key);
				currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
				spaceLeft = (6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.key));				

				if(dir) {
					ElevatorScene.goingUpSemArr.get(currFloorEle).release(spaceLeft);
				}else {
					ElevatorScene.goingDownSemArr.get(currFloorEle).release(spaceLeft);
				}
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
	
				if(dir) {
					ElevatorScene.goingUpSemArr.get(currFloorEle).acquire(spaceLeft);
				}else {
					ElevatorScene.goingDownSemArr.get(currFloorEle).acquire(spaceLeft);
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ElevatorScene.elevatorFloorMutexArr.get(currFloorEle).release();
			///---- People Entering Elevator End ----///
			
			///---- Move Elevator Begin ----///
		
			
			if(dir) {
				ElevatorScene.scene.incrementCurrentElevatorFloor(this.key);
			}else {
				ElevatorScene.scene.decrementCurrentElevatorFloor(this.key);
			}
					

			///---- Move Elevator End ----///
			
			
			///---- People Leaving Elevator Begin ----///
			// Release permits for person threads leaving elevator for current floor
			{
				currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
				numPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(this.key);
				ElevatorScene.outOfElevatorSemTwoDemArr[this.key][currFloorEle].release(numPeopleInElevator);
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
				ElevatorScene.outOfElevatorSemTwoDemArr[this.key][currFloorEle].acquire(numPeopleInElevator);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			///---- People Leaving Elevator End ----///
			
			if(currFloorEle == ElevatorScene.scene.getNumberOfFloors() - 1) {
				if(topCounterServicer != ElevatorScene.scene.getNumberOfFloors() - 1) {
					try {
						ElevatorScene.oddNumberElevatorsMutex.acquire();
						
						
						if(this.key %2 != 0) {
							topCounterServicer++;
							ElevatorScene.elevatorsFloor.set(this.key, topCounterServicer);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ElevatorScene.oddNumberElevatorsMutex.release();
					
				}else {
					topCounterServicer = 0;
				}
			}
			
			if(currFloorEle == 0) {
				if(bottomCounterService != 0) {
					try {
						ElevatorScene.oddNumberElevatorsMutex.acquire();
						
						
						if(this.key %2 != 0) {
							bottomCounterService--;
							ElevatorScene.elevatorsFloor.set(this.key, bottomCounterService);
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ElevatorScene.oddNumberElevatorsMutex.release();
					
				}else {
					bottomCounterService = ElevatorScene.scene.getNumberOfFloors() - 1;
				}
			}
	
		}
	}
}

