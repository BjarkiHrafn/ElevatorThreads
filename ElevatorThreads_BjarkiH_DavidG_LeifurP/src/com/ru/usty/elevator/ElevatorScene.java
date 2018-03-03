package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class ElevatorScene {
	
	/* TO SPEED THINGS UP WHEN TESTING,
	 * feel free to change this.  It will be changed during grading
	 * dont set this below 60
	 */ 
	///---- Public const ----///
	public static final int VISUALIZATION_WAIT_TIME = 500;
	///---- Public const ----///
	
	///---- Public int -----///
	public static int currElevatorAtFloor;
	public static ArrayList<Integer> currElevatorAtFloorArr;
	///---- Public int -----///
	
	///---- Public int -----///
	public static ArrayList<Integer> elevatorsFloor = null;
	///---- Public int -----///
	
	///---- Private int -----///
	private int numberOfFloors;
	private int numberOfElevators;
	private ArrayList<Integer> personCount;
	private ArrayList<Integer> exitedCount = null;
	private static ArrayList<Integer> personCountInElevator = null;
	///---- Private int -----///	
	
	///---- Public Booleans -----///
	public static boolean elevatorsMayDie;
	///---- Public Booleans -----///
	
	///---- ElevatorScenes ----///
	public static ElevatorScene scene;
	///---- ElevatorScenes ----///
	
	///---- Threads ----///
	private Thread elevatorThread = null;
	static ArrayList<Thread> elevatorThreads = new ArrayList<Thread>();
	///---- Threads ----///
	
	///---- Semaphores ----///
	// Semaphores
	static ArrayList<Semaphore> goingUpSemArr = null;
	static ArrayList<Semaphore> goingDownSemArr = null;
	static Semaphore[][] outOfElevatorSemTwoDemArr = null;
	// Mutex arrays
	static ArrayList<Semaphore> elevatorFloorMutexArr = null;
	static ArrayList<Semaphore> elevaitorPersonCountMutexArr = null;
	static ArrayList<Semaphore> numberOfPeopleInElevatorMutexArr = null;
	static ArrayList<Semaphore> elevatorDirectionMutexArr = null;
	static ArrayList<Semaphore> currentElevatorFloorMutexArr = null;
	static ArrayList<Semaphore> personCountMutexArr = null;
	static ArrayList<Semaphore> exitedCountMutexArr = null;
	static ArrayList<Semaphore> watcherOfElevatorKeyMutexArr = null;
	// Mutexes
	static Semaphore oddNumberElevatorsMutex;
	///---- Semaphores ----///
	
	
	// Base function: definition must not change, but add your code
	public void restartScene(int numberOfFloors, int numberOfElevators) {
		
		elevatorsMayDie = true;
		
		for(Thread thread: elevatorThreads) {
			if(thread != null) {
				if(thread.isAlive()) {	//DON'T DELETE, we will need it later
					try {
						thread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		elevatorsMayDie = false;
		scene = this;		
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;	
		
		// Mutexes

		//elevaitorPersonCountMutex2 = new Semaphore(1);
		oddNumberElevatorsMutex = new Semaphore(1);
		
		
		
		// Semaphore arrays initialization
		personCount = new ArrayList<Integer>();
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.personCount.add(0);
		}
		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
		}
		
		
		elevatorsFloor = new ArrayList<Integer>();
		personCountInElevator = new ArrayList<Integer>();
		outOfElevatorSemTwoDemArr = new Semaphore[getNumberOfElevators()][getNumberOfFloors()];
		for(int i = 0; i < getNumberOfElevators(); i++) {
			elevatorsFloor.add(0);
			personCountInElevator.add(0);
			for(int j = 0; j < getNumberOfFloors(); j++) {
				outOfElevatorSemTwoDemArr[i][j] = new Semaphore(0);
			}		
		}
		
		goingUpSemArr = new ArrayList<Semaphore>();
		goingDownSemArr = new ArrayList<Semaphore>();
		currElevatorAtFloorArr = new ArrayList<Integer>(); 
		for(int i = 0; i < getNumberOfFloors(); i++) {
			goingUpSemArr.add(new Semaphore(0));
			goingDownSemArr.add(new Semaphore(0));
			currElevatorAtFloorArr.add(0);
		}
		
		
		// Mutex arrays initialization
		elevaitorPersonCountMutexArr = new ArrayList<Semaphore>();
		elevaitorPersonCountMutexArr = new ArrayList<Semaphore>();
		numberOfPeopleInElevatorMutexArr = new ArrayList<Semaphore>();
		elevatorDirectionMutexArr = new ArrayList<Semaphore>();
		currentElevatorFloorMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < getNumberOfElevators(); i++) {
			elevaitorPersonCountMutexArr.add(new Semaphore(1));
			elevaitorPersonCountMutexArr.add(new Semaphore(1));
			numberOfPeopleInElevatorMutexArr.add(new Semaphore(1));
			elevatorDirectionMutexArr.add(new Semaphore(1));
			currentElevatorFloorMutexArr.add(new Semaphore(1));
		}
		elevatorFloorMutexArr = new ArrayList<Semaphore>();
		personCountMutexArr = new ArrayList<Semaphore>();
		exitedCountMutexArr = new ArrayList<Semaphore>();
		watcherOfElevatorKeyMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < getNumberOfFloors(); i++) {
			elevatorFloorMutexArr.add(new Semaphore(1));
			personCountMutexArr.add(new Semaphore(1));
			exitedCountMutexArr.add(new Semaphore(1));
			watcherOfElevatorKeyMutexArr.add(new Semaphore(1));
		}
				
		// Start Elevator threads		
		for(int i = 0; i < getNumberOfElevators(); i++) {
			elevatorThread = (new Thread(new Elevator(i)));
			elevatorThread.start();
			elevatorThreads.add(elevatorThread);
		}	
	}
	
	// ===== ADDED FUNCTIONS, DEFINITION MUST NOT CHANGE =====
	
	// PUBLIC FUNTIONS
	
	// Update current elevator by key
	public void incrementNumberOfPeopleInElevator(int elevator) {
		try {
			numberOfPeopleInElevatorMutexArr.get(elevator).acquire();
				personCountInElevator.set(elevator, personCountInElevator.get(elevator)+1);
			numberOfPeopleInElevatorMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void decrementNumberOfPeopleInElevator(int elevator) {
		try {
			numberOfPeopleInElevatorMutexArr.get(elevator).acquire();
				personCountInElevator.set(elevator, personCountInElevator.get(elevator)-1);
			numberOfPeopleInElevatorMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public void setCurrentElevatorAtFloor(int floor, int key) {		
		try {
			currentElevatorFloorMutexArr.get(key).acquire();
				currElevatorAtFloorArr.set(floor, key);
			currentElevatorFloorMutexArr.get(key).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public int getCurrentElevatorAtFloor(int floor) {		
		return currElevatorAtFloorArr.get(floor);	
	}
	
	// Increment current elevator by 1 floor
    public void incrementCurrentElevatorFloor(int elevator) {
    	try {
			currentElevatorFloorMutexArr.get(elevator).acquire();
			 	elevatorsFloor.set(elevator, (elevatorsFloor.get(elevator) + 1));
		     currentElevatorFloorMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    // Decrements current elevator by 1 floor 
    public void decrementCurrentElevatorFloor(int elevator) {
	    if(elevatorsFloor.get(elevator) >= 0)
			try {
				currentElevatorFloorMutexArr.get(elevator).acquire();
					elevatorsFloor.set(elevator, (elevatorsFloor.get(elevator) - 1));
		    	currentElevatorFloorMutexArr.get(elevator).release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 	
    }


	// Decrement number of people waiting at floor by one, by current floor
	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			personCountMutexArr.get(floor).acquire();
				personCount.set(floor, (personCount.get(floor) -1));
			personCountMutexArr.get(floor).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Increment number of people waiting at floor by one, by current floor
	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			personCountMutexArr.get(floor).acquire();
				personCount.set(floor, (personCount.get(floor) +1));
			personCountMutexArr.get(floor).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	// ===== BASE FUNCTIONS, DEFINITION MUST NOT CHANGE =====
	
	// PUBLIC FUNTIONS

	// Base function: definition must not change
	// Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {
		Thread thread = new Thread(new Person(sourceFloor, destinationFloor));
		thread.start();
		incrementNumberOfPeopleWaitingAtFloor(sourceFloor);
		
		return thread;
	}

	// Base function: definition must not change, but add your code
	public static int getCurrentFloorForElevator(int elevator) {
		return elevatorsFloor.get(elevator);
	}
	
	// Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {
		return personCountInElevator.get(elevator);
	}

	// Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {
		return personCount.get(floor);
	}
	
	// Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	// Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	// Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	// Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	// Base function: no need to change unless you choose
	//				  not to "open the doors" sometimes
	//				  even though there are people there
	public boolean isElevatorOpen(int elevator) {
		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	
	// Base function: no need to change, just for visualization
	// Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {
		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

	// Person threads must call this function to
	// let the system know that they have exited.
	// Person calls it after being let off elevator
	// but before it finishes its run.
	public void personExitsAtFloor(int floor) {
		try {
			exitedCountMutexArr.get(floor).acquire();
				exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutexArr.get(floor).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Base function: no need to change, just for visualization
	// Feel free to use it though, if it helps
	public int getExitedCountAtFloor(int floor) {
		if(floor < getNumberOfFloors()) {
			return exitedCount.get(floor);
		}
		else {
			return 0;
		}	
	}
}
