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
			//ElevatorScene.elevatorWaitMutexArr.get(sourceFloor).acquire();
			//ElevatorScene.elevatorWaitMutex.acquire();
				ElevatorScene.inToElevatorFloorsSem.get(sourceFloor).acquire();
			//ElevatorScene.elevatorWaitMutexArr.get(sourceFloor).release();
			//ElevatorScene.elevatorWaitMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			ElevatorScene.WatcherOfElevatorKeyMutex.acquire();
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		int myElevator = ElevatorScene.currElevatorAtFloor;
		ElevatorScene.WatcherOfElevatorKeyMutex.release();
		
		
		
		// People going into elevator, mutex used to go in one by one
		// ----
		//ElevatorScene.elevaitorPersonCountMutex.acquire();
			
				// create int here 
				// decrement people waiting at floor
				ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
				System.out.println("Person.java says that people are going into elevator");
				// increment people in elevator
				ElevatorScene.scene.incrementNumberOfPeopleInElevator(myElevator);
			//ElevatorScene.elevaitorPersonCountMutex.release();
	// ----
		
		
		try {
			//ElevatorScene.outOfElevatorFloorsSem.get(destinationFloor).acquire();
			//ElevatorScene.outOfElevatorFloorsSemTwoDemArr.get(myElevator).get(destinationFloor).acquire();
			ElevatorScene.TwoD_ArrayOUT[myElevator][destinationFloor].acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		
			ElevatorScene.scene.decrementNumberOfPeopleInElevator(myElevator);
			System.out.println("Person.java says that people are leaving elevator");
			ElevatorScene.scene.personExitsAtFloor(destinationFloor); // exits at destination floor add parameter
			
	
		
		
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
	}

}
