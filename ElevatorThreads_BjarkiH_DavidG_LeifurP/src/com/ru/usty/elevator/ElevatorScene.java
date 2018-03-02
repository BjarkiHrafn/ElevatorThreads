package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javax.sound.midi.SoundbankResource;


/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {
	
	/**
	 * !This is where you define semaphores!
	 * When you call this semaphore,
	 * put the class name it's reffering to
	 * then a dot and call it, Don't just type globalSemaphore!
	 */
	
	
	/* TO SPEED THINGS UP WHEN TESTING,
	 * feel free to change this.  It will be changed during grading
	 * ekki fyrir nedan 50 milliseconds -Bjarki
	 */ 
	public static final int VISUALIZATION_WAIT_TIME = 1000;  // Ekki fyrir nedan 4 milliseconds
	
	///---- Public int -----///
	public static int currElevatorAtFloor;
	public static ArrayList<Integer> currElevatorAtFloorArr;
	///---- Public int -----///
	
	///---- Private int -----///
	private int numberOfFloors;
	private int numberOfElevators;
	
	ArrayList<Integer> personCount; //TODO::DECLARE, PUBLIC OR PRIVATE?
	ArrayList<Integer> exitedCount = null; //TODO::DECLARE, PUBLIC OR PRIVATE?
	static ArrayList<Integer> elevatorsFloor = null; //TODO::DECLARE, PUBLIC OR PRIVATE?
	static ArrayList<Integer> personCountInElevator = null; //TODO::DECLARE, PUBLIC OR PRIVATE?
	static ArrayList<Boolean> elevatorDirection = null;
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
	/*
	 * There will be only one version of this running because it's static
	 * Pls cleanup after you new this !	
	 * that also exists mutex instead for Semaphore
	 */
	// Semaphores
	static ArrayList<Semaphore> elevatorWaitMutexArr = null;
	static ArrayList<Semaphore> elevatorWaitMutexArr2 = null;
	static ArrayList<Semaphore> goingUpSemArr = null;
	static ArrayList<Semaphore> goingDownSemArr = null;
	static Semaphore[][] TwoD_ArrayOUT = null;
	static ArrayList<Semaphore> outOfElevatorFloorsSem = null;

	// Mutex arrays
	static ArrayList<Semaphore> elevatorFloorMutexArr = null;
	static ArrayList<Semaphore> elevaitorPersonCountMutexArr = null;
	static ArrayList<Semaphore> NumberOfPeopleInElevatorMutexArr = null;
	static ArrayList<Semaphore> elevatorDirectionMutexArr = null;
	static ArrayList<Semaphore> CurrentElevatorFloorMutexArr = null;
	static ArrayList<Semaphore> personCountMutexArr = null;
	static ArrayList<Semaphore> exitedCountMutexArr = null;
	
	// Mutexes
	public static Semaphore elevatorFloorMutex;
	//public static Semaphore elevaitorPersonCountMutex2;
	public static Semaphore firstFloorWaitMutex;
	public static Semaphore elevaitorPersonCountMutex;
	public static Semaphore personCountMutex;
	public static Semaphore elevatorWaitMutex;
	public static Semaphore exitedCountMutex;
	public static Semaphore WatcherOfElevatorKeyMutex;
	public static Semaphore stopElevatorMutex;
	public static Semaphore personCountMutex2;
	public static Semaphore NumberOfPeopleInElevatorMutex;
	public static Semaphore CurrentElevatorFloorMutex;
	public static Semaphore elevatorDirectionMutex;
	public static Semaphore oddNumberElevatorsMutex;
	///---- Semaphores ----///
	

	// TODO::SET UNDER BASE FUNCTIONS WHEN PROJECT IS FINISHED
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
		// TODO::USED FUNCTIONS INSTEAD
		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;	
		
		// Mutexes
		personCountMutex = new Semaphore(1);//<- the first one that calls wait gets through, which means: only one at a time
		elevatorWaitMutex = new Semaphore(1);
		elevatorFloorMutex = new Semaphore(1);
		elevaitorPersonCountMutex = new Semaphore(1);
		firstFloorWaitMutex = new Semaphore(1);
		//elevaitorPersonCountMutex2 = new Semaphore(1);
		exitedCountMutex = new Semaphore(1);
		WatcherOfElevatorKeyMutex = new Semaphore(1);
		stopElevatorMutex = new Semaphore(1);
		personCountMutex2 = new Semaphore(1);
		NumberOfPeopleInElevatorMutex = new Semaphore(1);
		CurrentElevatorFloorMutex = new Semaphore(1);
		elevatorDirectionMutex = new Semaphore(1);
		oddNumberElevatorsMutex = new Semaphore(1);
		
		// Semaphores
		
			

		personCount = new ArrayList<Integer>();
		for(int i = 0; i < numberOfFloors; i++) {
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
		for(int i = 0; i < getNumberOfElevators(); i++) {
			elevatorsFloor.add(0);
		}
		
		personCountInElevator = new ArrayList<Integer>();
		for(int i = 0; i < getNumberOfElevators(); i++) {
			personCountInElevator.add(0);
		}
		
		elevatorDirection = new ArrayList<Boolean>();
		for(int i = 0; i < getNumberOfElevators(); i++) {
			elevatorDirection.add(true);
		}

		TwoD_ArrayOUT = new Semaphore[getNumberOfElevators()][getNumberOfFloors()];
		for(int i = 0; i < getNumberOfElevators(); i++) {
			for(int j = 0; j < getNumberOfFloors(); j++) {
				TwoD_ArrayOUT[i][j] = new Semaphore(0);
			}
		}
		
		
		goingUpSemArr = new ArrayList<Semaphore>();
		for(int i = 0; i < getNumberOfFloors(); i++) {
			goingUpSemArr.add(new Semaphore(0));
		}
		
		goingDownSemArr = new ArrayList<Semaphore>();
		for(int i = 0; i < getNumberOfFloors(); i++) {
			goingDownSemArr.add(new Semaphore(0));
		}
		
		/*elevatorWaitMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < getNumberOfFloors(); i++) {
			elevatorWaitMutexArr.add(new Semaphore(1));
		}*/
		
		/*elevatorWaitMutexArr2 = new ArrayList<Semaphore>();
		for(int i = 0; i < getNumberOfElevators(); i++) {
			elevatorWaitMutexArr2.add(new Semaphore(1));
		}*/
		
		currElevatorAtFloorArr = new ArrayList<Integer>(); 
		for(int i = 0; i < numberOfFloors; i++) 
		{
			// if this is not updated it should crash
			currElevatorAtFloorArr.add(0);
		}
		
		
		// mutex arr initalization
		elevaitorPersonCountMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < getNumberOfElevators(); i++) {
			elevaitorPersonCountMutexArr.add(new Semaphore(1));
		}
		elevatorFloorMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < numberOfElevators; i++) {
			elevatorFloorMutexArr.add(new Semaphore(1));
		}
		elevaitorPersonCountMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < numberOfElevators; i++) {
			elevaitorPersonCountMutexArr.add(new Semaphore(1));
		}
		NumberOfPeopleInElevatorMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < numberOfElevators; i++) {
			NumberOfPeopleInElevatorMutexArr.add(new Semaphore(1));
		}
		elevatorDirectionMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < numberOfElevators; i++) {
			elevatorDirectionMutexArr.add(new Semaphore(1));
		}
		
		CurrentElevatorFloorMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < numberOfElevators; i++) {
			CurrentElevatorFloorMutexArr.add(new Semaphore(1));
		}
		
		personCountMutexArr = new ArrayList<Semaphore>();
		exitedCountMutexArr = new ArrayList<Semaphore>();
		for(int i = 0; i < numberOfFloors; i++) {
			personCountMutexArr.add(new Semaphore(1));
			exitedCountMutexArr.add(new Semaphore(1));
		}


		
		// Start Elevator threads		
		for(int i = 0; i < numberOfElevators; i++) {
			elevatorThread = (new Thread(new Elevator(getCurrentFloorForElevator(i), getNumberOfPeopleInElevator(i), i)));
			elevatorThread.start();
			elevatorThreads.add(elevatorThread);
		}	
	}
	
	


	

	// ===== ADDED FUNCTIONS, DEFINITION MUST NOT CHANGE =====
	
	// PUBLIC FUNTIONS
	
	public void setElevatorDirection(int elevator, boolean bool) {
		try {
			elevatorDirectionMutexArr.get(elevator).acquire();
				elevatorDirection.set(elevator, bool);
			elevatorDirectionMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	// Update current elevator by key
	public void incrementNumberOfPeopleInElevator(int elevator) {
		try {
			NumberOfPeopleInElevatorMutexArr.get(elevator).acquire();
				personCountInElevator.set(elevator, personCountInElevator.get(elevator)+1);
			NumberOfPeopleInElevatorMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void decrementNumberOfPeopleInElevator(int elevator) {
		try {
			NumberOfPeopleInElevatorMutexArr.get(elevator).acquire();
				personCountInElevator.set(elevator, personCountInElevator.get(elevator)-1);
			NumberOfPeopleInElevatorMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void updateCurrentElevator(int key) {		
		currElevatorAtFloor = key;		
	}
	
	public void updateCurrentElevatorAtFloor(int floor, int key) {		
		currElevatorAtFloorArr.set(floor, key);	
	}
	public int getCurrentElevatorAtFloor(int floor) {		
		return currElevatorAtFloorArr.get(floor);	
	}
	
	// Increment current elevator by 1 floor
    public static void incrementCurrentElevatorFloor(int elevator) {
    	try {
			CurrentElevatorFloorMutexArr.get(elevator).acquire();
			 	elevatorsFloor.set(elevator, (elevatorsFloor.get(elevator) + 1));
		     CurrentElevatorFloorMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
       
    }
    
    // Decrements current elevator by 1 floor FIXME::REMOVE THIS IF NOT USED??
    public void decrementCurrentElevatorFloor(int elevator) {
	    if(elevatorsFloor.get(elevator) >= 0)
			try {
				CurrentElevatorFloorMutexArr.get(elevator).acquire();
					elevatorsFloor.set(elevator, (elevatorsFloor.get(elevator) - 1));
		    	CurrentElevatorFloorMutexArr.get(elevator).release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	
    }
    
    // Set elevator floor to 0, by elevator
    public void setCurrentElevatorToFirstFloor(int elevator) {
    	try {
			CurrentElevatorFloorMutexArr.get(elevator).acquire();
				elevatorsFloor.set(elevator, 0);
	    	CurrentElevatorFloorMutexArr.get(elevator).release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    }
    
    // Set elevator floor a selected floor, by elevator and floor
   /* public void setCurrentElevatorToFirstFloor(int elevator, int floor) {
    	elevatorsFloor.set(elevator, floor);
    }*/

	// Decrement number of people waiting at floor by one, by current floor
	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {
	
			try {
				ElevatorScene.personCountMutexArr.get(floor).acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				personCount.set(floor, (personCount.get(floor) -1));
			ElevatorScene.personCountMutexArr.get(floor).release();
	
	}

	// Increment number of people waiting at floor by one, by current floor
	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {
		
			try {
				ElevatorScene.personCountMutexArr.get(floor).acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				personCount.set(floor, (personCount.get(floor) +1));
			ElevatorScene.personCountMutexArr.get(floor).release();
		
	}
	
	
	// ===== BASE FUNCTIONS, DEFINITION MUST NOT CHANGE =====
	
	// PUBLIC FUNTIONS

	// Base function: definition must not change
	// Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {
		// Go take a look at the person class when you want to add
		// sourceFloor and destinationFloor into the thread
		Thread thread = new Thread(new Person(sourceFloor, destinationFloor));
		thread.start();
		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */

		incrementNumberOfPeopleWaitingAtFloor(sourceFloor);
		
		return thread;  //this means that the testSuite will not wait for the threads to finish
		//were returning the thread for the base system that will clean up after us - Bjarki
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
