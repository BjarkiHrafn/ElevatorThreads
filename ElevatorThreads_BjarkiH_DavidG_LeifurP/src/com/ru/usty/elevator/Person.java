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
				ElevatorScene.globalSemaphore.acquire(); //this is equivalent to a wait function
			ElevatorScene.elevatorWaitMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		// is now available
		ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
		
		System.out.println("Person thread released");
		
		try {
			ElevatorScene.personWaitMutex.acquire();
				ElevatorScene.globalSemaphore.acquire();
			ElevatorScene.personWaitMutex.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ElevatorScene.scene.decrementNumberOfPeopleInElevator(sourceFloor);
		
		
		
	}

}
