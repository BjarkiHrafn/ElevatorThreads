package com.ru.usty.elevator;

public class Person implements Runnable {
//Note that creating a runnable class means you have to specify everything that will run
	private int sourceFloor, destinationFloor, myElevatorKey;
	
	// Constructor
	public Person(int sourceFloor, int destinationFloor) {
		this.sourceFloor = sourceFloor;
		this.destinationFloor = destinationFloor;
	}
	
	@Override
	public void run() {
		
		///---- People Waiting On Floor Semaphore For Elevator Begin----///
		/*
		 * People threads wait on semaphore array depending if they are
		 * heading up or down, elevator thread will release the permit back
		 * when it is heading the same direction and is on the correct floor.
		 */
		try {
			// Going up
			if(sourceFloor < destinationFloor) {ElevatorScene.goingUpSemArr.get(sourceFloor).acquire();}
			// Going down
			else {ElevatorScene.goingDownSemArr.get(sourceFloor).acquire();}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		///---- People Waiting On Floor Semaphore For Elevator End----///	
		
		
		///---- Person Thread Getting What Elevator They Entered Begin----///
		/*
		 * This is a critical section, to check what elevator the person
		 * thread entered.
		 */
		try {
			ElevatorScene.watcherOfElevatorKeyMutexArr.get(sourceFloor).acquire();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		{
			myElevatorKey = ElevatorScene.scene.getCurrentElevatorAtFloor(this.sourceFloor);
			ElevatorScene.watcherOfElevatorKeyMutexArr.get(sourceFloor).release();
		}
		///---- Person Thread Getting What Elevator They Entered End ----///	
		
		
		///---- People Entering Elevator Begin ----///
		/*
		 * This decrements the persons waiting at floor and increments
		 * the people in elevator, each call is a critical section that is
		 * handled by the function itself.
		 */
		{			
			ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
			ElevatorScene.scene.incrementNumberOfPeopleInElevator(myElevatorKey);
		}
		///---- People Entering Elevator End----///
		
		
		///---- People Waiting In Elevator&Floor Semaphore, For Leaving Elevator Begin----///
		/*
		 * People waiting to get out of elevator, that uses
		 * two dimensional semaphore array for what elevator
		 * person is in and what the destination floor for the 
		 * person is. After the corresponding elevator reaches
		 * its floor the person thread can continue.
		 */
		try {
			ElevatorScene.outOfElevatorSemTwoDemArr[myElevatorKey][destinationFloor].acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		///---- People Waiting In Elevator&Floor Semaphore, For Leaving Elevator End----///	
		
		
		///---- People Leaving Elevator Begin ----///
		/*
		 * This increments the persons entering the floor and decrements
		 * the people in elevator, each call is a critical section that is
		 * handled by the function itself.
		 */
		{
			ElevatorScene.scene.decrementNumberOfPeopleInElevator(myElevatorKey);
			ElevatorScene.scene.personExitsAtFloor(destinationFloor);
		}
		///---- People Leaving Elevator End ----///
	}
}
