package com.ru.usty.elevator;

public class Person implements Runnable {
//Note that creating a runnable class means you have to specify everything that will run
	int sourceFloor, destinationFloor, myElevatorKey;
	
	
	//This might not be the best way to get src and dst inside Person!
	public Person(int sourceFloor, int destinationFloor) {
		this.sourceFloor = sourceFloor;
		this.destinationFloor = destinationFloor;
	}
	
	@Override
	public void run() {
		
		try {
			ElevatorScene.inToElevatorFloorsSem.get(sourceFloor).acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Alternative code
			//ElevatorScene.elevatorWaitMutexArr.get(sourceFloor).acquire();
			//ElevatorScene.elevatorWaitMutex.acquire();
			//ElevatorScene.elevatorWaitMutexArr.get(sourceFloor).release();
			//ElevatorScene.elevatorWaitMutex.release();
		///---- People Waiting On Floor Semaphore For Elevator End----///	
		
		
		///---- Person Thread Getting What Elevator They Entered Begin----///	
		try {
			ElevatorScene.WatcherOfElevatorKeyMutex.acquire();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		{
			myElevatorKey = ElevatorScene.currElevatorAtFloor;
			ElevatorScene.WatcherOfElevatorKeyMutex.release();
		}
		///---- Person Thread Getting What Elevator They Entered End ----///	
		
		
		///---- People Entering Elevator Begin ----///
		{			
			ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
			//System.out.println("Person.java says that people are going into elevator");
			ElevatorScene.scene.incrementNumberOfPeopleInElevator(myElevatorKey);
		}
		// Alternative code
			//ElevatorScene.elevaitorPersonCountMutex.acquire();
			//ElevatorScene.elevaitorPersonCountMutex.release();
		///---- People Entering Elevator End----///
		
		
		///---- People Waiting In Elevator&Floor Semaphore, For Leaving Elevator Begin----///	
		try {
			ElevatorScene.TwoD_ArrayOUT[myElevatorKey][destinationFloor].acquire();
			
			// Alternative code
				//ElevatorScene.outOfElevatorFloorsSem.get(destinationFloor).acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		///---- People Waiting In Elevator&Floor Semaphore, For Leaving Elevator End----///	
		
		
		///---- People Leaving Elevator Begin ----///
		{
			ElevatorScene.scene.decrementNumberOfPeopleInElevator(myElevatorKey);
			//System.out.println("Person.java says that people are leaving elevator");
			ElevatorScene.scene.personExitsAtFloor(destinationFloor); // exits at destination floor add parameter
		}
		///---- People Leaving Elevator End ----///
	}
}




/*
// People leaving elevator, mutex used to go in one by one
// ----
try {
	ElevatorScene.elevatorWaitMutexArr2.get(myElevator).acquire();
	//ElevatorScene.elevaitorPersonCountMutex2.acquire();
		ElevatorScene.personCountInElevator.set(myElevator, ElevatorScene.personCountInElevator.get(myElevator)-1);
		System.out.println("people in elevator" + ElevatorScene.personCountInElevator.get(myElevator));
		System.out.println("destination floor: " + destinationFloor);
		ElevatorScene.scene.personExitsAtFloor(destinationFloor); // exits at destination floor add parameter
	//ElevatorScene.elevaitorPersonCountMutex2.release();
	ElevatorScene.elevatorWaitMutexArr2.get(myElevator).release();
} catch (InterruptedException e) {
	e.printStackTrace();
}*/
// ----