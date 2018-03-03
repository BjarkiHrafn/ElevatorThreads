package com.ru.usty.elevator;


public class Elevator implements Runnable {

	private int key, currFloorEle, spaceLeft, numPeopleInElevator;
	private static final int SLEEP_TIME = ElevatorScene.VISUALIZATION_WAIT_TIME / 2;
	private static int topCounterServicer = 0;
	private static int bottomCounterService = ElevatorScene.scene.getNumberOfFloors() - 1;
	private boolean dir = true;
	
	// Constructor
	public Elevator(int key) {
		this.key = key;
	}
	
	@Override
	public void run() {
		while(true) {
			///---- Stop The Program From Running Infinitely Begin----///
			/*
			 * The ElevatorScene class has function restart scene that calls
			 * the function start scene that sets this elevatorsMayDie boolean
			 * to true to kill all elevators while loops that where running 
			 * previously.
			 */
			if(ElevatorScene.elevatorsMayDie) {return;}
			///---- Stop The Program From Running Infinitely End----///

			
			///---- Set Direction Of Elevator Begin----///
			/*
			 * Sets the current direction of the elevator,
			 * boolean true==up, false==down.
			 */
			currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
			if(currFloorEle == 0) {dir = true;}
			else if(currFloorEle == ElevatorScene.scene.getNumberOfFloors()-1) {dir = false;}
			///---- Set Direction Of Elevator End----///
			
			
			///---- People Entering Elevator Begin ----///
			/*
			 * This section is a critical section, that that releases permits 
			 * for persons to enter the elevator depending if it is going up
			 * or down (set in previous section), then gives a proportional wait 
			 * time according to VISUALIZATION_WAIT_TIME and then acquires permits
			 * back depending on how many persons entered the elevator.
			 */
			try {
				ElevatorScene.elevatorFloorMutexArr.get(currFloorEle).acquire();	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Release permits for person threads entering elevator for current floor
			{				
				currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
				spaceLeft = (6 - ElevatorScene.scene.getNumberOfPeopleInElevator(this.key));	
				ElevatorScene.scene.setCurrentElevatorAtFloor(currFloorEle, this.key);				
				if(dir) {ElevatorScene.goingUpSemArr.get(currFloorEle).release(spaceLeft);
				}else {ElevatorScene.goingDownSemArr.get(currFloorEle).release(spaceLeft);}
			}
			
			// Sleep time proportional to VISUALIZATION_WAIT_TIME wait time, to make sure persons
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
				if(dir) {ElevatorScene.goingUpSemArr.get(currFloorEle).acquire(spaceLeft);
				}else {ElevatorScene.goingDownSemArr.get(currFloorEle).acquire(spaceLeft);}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ElevatorScene.elevatorFloorMutexArr.get(currFloorEle).release();
			///---- People Entering Elevator End ----///
			
			
			///---- Move Elevator Begin ----///
			/*
			 * Increments elevator floor depending on direction of 
			 * elevator (found in previous section).
			 */
			if(dir) {ElevatorScene.scene.incrementCurrentElevatorFloor(this.key);
			}else {ElevatorScene.scene.decrementCurrentElevatorFloor(this.key);}
			///---- Move Elevator End ----///
			
			
			///---- People Leaving Elevator Begin ----///
			/*
			 * 
			 */
			// Release permits for person threads leaving elevator for current floor
			{
				currFloorEle = ElevatorScene.getCurrentFloorForElevator(this.key);
				numPeopleInElevator = ElevatorScene.scene.getNumberOfPeopleInElevator(this.key);
				ElevatorScene.outOfElevatorSemTwoDemArr[this.key][currFloorEle].release(numPeopleInElevator);
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
			
			
			///---- Odd Numbered Elevators Direction Change Begin ----///
			/*
			 * Odd numbered elevators are changing directions differently then
			 * other elevators, they go from top or bottom floor and then release
			 * all persons for each floor then instead of changing directions when
			 * reaching the end (top or bottom) it resets the elevator to bottom + 1
			 * or top - 1, and so on until reaching end, therefore making sure no 
			 * person on any floor gets starved.
			 */
			
			// Elevator is at top floor
			if(currFloorEle == ElevatorScene.scene.getNumberOfFloors() - 1) {
				if(topCounterServicer != ElevatorScene.scene.getNumberOfFloors() - 1) {
					try {
						ElevatorScene.oddNumberElevatorsMutex.acquire();
						if(this.key %2 != 0) {
							topCounterServicer++;
							ElevatorScene.elevatorsFloor.set(this.key, topCounterServicer);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ElevatorScene.oddNumberElevatorsMutex.release();	
				}else {
					topCounterServicer = 0;
				}
			}
			
			// Elevator is at bottom floor
			if(currFloorEle == 0) {
				if(bottomCounterService != 0) {
					try {
						ElevatorScene.oddNumberElevatorsMutex.acquire();
						if(this.key %2 != 0) {
							bottomCounterService--;
							ElevatorScene.elevatorsFloor.set(this.key, bottomCounterService);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ElevatorScene.oddNumberElevatorsMutex.release();
				}else {
					bottomCounterService = ElevatorScene.scene.getNumberOfFloors() - 1;
				}
			}
			///---- Odd Numbered Elevators Direction Change End ----///
		}
	}
}

