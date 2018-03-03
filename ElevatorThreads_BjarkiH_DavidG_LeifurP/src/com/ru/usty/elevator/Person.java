package com.ru.usty.elevator;

public class Person implements Runnable {
//Note that creating a runnable class means you have to specify everything that will run
	int sourceFloor, destinationFloor, myElevatorKey;
	boolean dir;
	
	
	//This might not be the best way to get src and dst inside Person!
	public Person(int sourceFloor, int destinationFloor) {
		this.sourceFloor = sourceFloor;
		this.destinationFloor = destinationFloor;
	}
	
	@Override
	public void run() {
		
		try {
	
				// Going up
				if(sourceFloor < destinationFloor) {
					ElevatorScene.goingUpSemArr.get(sourceFloor).acquire();
				}
				// Going down
				else {
					ElevatorScene.goingDownSemArr.get(sourceFloor).acquire();
				}
			//ElevatorScene.elevatorWaitMutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		///---- People Waiting On Floor Semaphore For Elevator End----///	
		
		
		///---- Person Thread Getting What Elevator They Entered Begin----///	
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
		{			
			ElevatorScene.scene.decrementNumberOfPeopleWaitingAtFloor(sourceFloor);
			//System.out.println("Person.java says that people are going into elevator");
			ElevatorScene.scene.incrementNumberOfPeopleInElevator(myElevatorKey);
		}
		///---- People Entering Elevator End----///
		
		
		///---- People Waiting In Elevator&Floor Semaphore, For Leaving Elevator Begin----///	
		try {
			ElevatorScene.outOfElevatorSemTwoDemArr[myElevatorKey][destinationFloor].acquire();
			
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
