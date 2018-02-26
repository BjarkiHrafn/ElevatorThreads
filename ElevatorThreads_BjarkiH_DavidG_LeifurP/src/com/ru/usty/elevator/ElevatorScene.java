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
	//----Semaphores-----//
	//There will be only one version of this running because it's static
	//Pls cleanup after you new this !	
	public static Semaphore globalSemaphore;
	public static Semaphore personSemaphore;
	public static Semaphore elevatorSemaphore;
	//that also exists mutex instead for Semaphore
	public static Semaphore personCountMutex;
	public static Semaphore elevatorCountMutex;
	
	public static Semaphore elevatorWaitMutex;
	public static Semaphore personWaitMutex;
	//------Semaphores------//
	
	public static boolean elevatorsMayDie;
	public static boolean makePeopleWait;
	public static ElevatorScene scene;
	
	//------threads--------//
	private Thread elevatorThread = null;
	//------threads-------//

	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	// ekki fyrir nedan 50 milliseconds -Bjarki
	public static final int VISUALIZATION_WAIT_TIME = 500;  //milliseconds

	private int numberOfFloors;
	private int numberOfElevators;
	private int numberOfPeopleInLift;

	ArrayList<Integer> personCount = null; 
	ArrayList<Integer> exitedCount = null;
	public static Semaphore exitedCountMutex;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {
		
		elevatorsMayDie = true;
		
		/**for(Thread thread: elevatorThreads) {
			if(thread != null) {
				if(thread.isAlive()) {	DON'T DELETE, we will need it later
					thread.join();
				}
			}
		}*/
		if(elevatorThread != null) {
			if(elevatorThread.isAlive()) {
				try {
					elevatorThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		elevatorsMayDie = false;
		
		scene = this;
		globalSemaphore = new Semaphore(0);// <- the first one that calls wait will be stopped
		personSemaphore = new Semaphore(0);
		elevatorSemaphore = new Semaphore(0);
		personCountMutex = new Semaphore(1);//<- the first one that calls wait gets through, which means: only one at a time
		elevatorCountMutex = new Semaphore(1);
		elevatorWaitMutex = new Semaphore(1);
		personWaitMutex = new Semaphore(1);
		
		
		
		
		/**
		 * ATTENTION
		 * This is not an acceptable way to create threads,
		 * However, it's ok when you are testing. -Bjarki
		 */
		Thread thread = new Thread(new Elevator());
		thread.start();
		
		/**
		 * Important to add code here to make new
		 * threads that run your elevator-runnables
		 * 
		 * Also add any other code that initializes
		 * your system for a new run
		 * 
		 * If you can, tell any currently running
		 * elevator threads to stop
		 */

		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;

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
		exitedCountMutex = new Semaphore(1);
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {
		
		//Go take a look at the person class when you want to add
		//sourceFloor and destinationFloor into the thread
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

		ElevatorScene.scene.incrementNumberOfPeopleWaitingAtFloor(sourceFloor);
	
		 
		return thread;  //this means that the testSuite will not wait for the threads to finish
		//were returning the thread for the base system that will clean up after us - Bjarki
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {

		//dumb code, replace it!
		return 1;
	}
	
	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {
		/*
		 * The person class should be implemented to
		 * get the value.. not elevator
		 */
		System.out.println(exitedCount.get(elevator));
		return exitedCount.get(elevator);
		
	}
	
	public void decrementNumberOfPeopleInElevator(int floor) {
		try {
			ElevatorScene.elevatorCountMutex.acquire();
				exitedCount.set(floor, (exitedCount.get(floor) -1));
			ElevatorScene.elevatorCountMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
	}
	
	public void incrementNumberOfPeopleInElevator(int floor) {
			try {
				ElevatorScene.elevatorCountMutex.acquire();
					exitedCount.set(floor, (exitedCount.get(floor) +1));
				ElevatorScene.elevatorCountMutex.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {
		return personCount.get(floor);
	}
	
	public void decrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			// We might need another mutex for floors
			ElevatorScene.personCountMutex.acquire();
				personCount.set(floor, (personCount.get(floor) -1));
				ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // wait
		
	}// added by Bjarki

	public void incrementNumberOfPeopleWaitingAtFloor(int floor) {
		try {
			// We might need another mutex for floors
			ElevatorScene.personCountMutex.acquire();
				personCount.set(floor, (personCount.get(floor) +1));
				ElevatorScene.personCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // wait
		
	}// added by Bjarki
	//Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	//Base function: no need to change unless you choose
	//				 not to "open the doors" sometimes
	//				 even though there are people there
	public boolean isElevatorOpen(int elevator) {

		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {

		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

	//Person threads must call this function to
	//let the system know that they have exited.
	//Person calls it after being let off elevator
	//but before it finishes its run.
	public void personExitsAtFloor(int floor) {
		try {
			
			exitedCountMutex.acquire();
			exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutex.release();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public int getExitedCountAtFloor(int floor) {
		if(floor < getNumberOfFloors()) {
			return exitedCount.get(floor);
		}
		else {
			return 0;
		}
	}


}
