package com.ru.usty.elevator;

public class Person implements Runnable {
//Note that creating a runnable class means you have to specify everything that will run
	int sourceFloor, destinationFloor;
	
	//This might not be the best way to get src and dst inside Person!
	public Person(int sourceFloor, int destinationFloor) {
		this.sourceFloor = sourceFloor;
		this.destinationFloor = destinationFloor;
	}
	
	@Override
	public void run() {
		
		try {
			ElevatorScene.elevatorWaitMutex.acquire();
				//ElevatorScene.firstFloorInSemaphore.acquire(); //this is equivalent to a wait function
				ElevatorScene.inToElevatorFloorsSem.get(sourceFloor).acquire();
			ElevatorScene.elevatorWaitMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// is now available
		
		
		// People going into elevator, mutex used to go in one by one
		// ----
		try {
			ElevatorScene.elevaitorPersonCountMutex.acquire();
				// decrement people waiting at floor
				ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
				System.out.println("Person thread released into elevator");
				// increment people in elevator
				ElevatorScene.personCountInElevator.set(0, ElevatorScene.personCountInElevator.get(0)+1);
				System.out.println("people in elevator: " + ElevatorScene.scene.getNumberOfPeopleInElevator(0));
				
				
				// check if people are waiting or if elevator is full
				if(ElevatorScene.personCountInElevator.get(0) == 6 || ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(0) == 0) {
					// unlock a semaphore for elevator
					ElevatorScene.elevatorWaitSemaphore.release();
				}
				
			ElevatorScene.elevaitorPersonCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ----
		
		
		
		
		try {
			ElevatorScene.outOfElevatorFloorsSem.get(destinationFloor).acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		/*
		try {
			ElevatorScene.secondFloorOutSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		// People leaving elevator, mutex used to go in one by one
		// ----
		try {
			ElevatorScene.elevaitorPersonCountMutex.acquire();
				ElevatorScene.personCountInElevator.set(0, ElevatorScene.personCountInElevator.get(0)-1);
				System.out.println("people in elevator" + ElevatorScene.personCountInElevator.get(0));
				System.out.println("destination floor: " + destinationFloor);
				ElevatorScene.scene.personExitsAtFloor(destinationFloor); // exits at destination floor add parameter
				
				
				
				if(ElevatorScene.personCountInElevator.get(0) == 0) {
					ElevatorScene.elevatorWaitSemaphore2.release();
				}
				
				
			ElevatorScene.elevaitorPersonCountMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ----
	}

}
