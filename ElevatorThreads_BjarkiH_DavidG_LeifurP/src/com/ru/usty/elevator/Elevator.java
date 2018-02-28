package com.ru.usty.elevator;

import java.util.concurrent.Semaphore;

public class Elevator implements Runnable {

	int CurrentFloorForElevator, NumberOfPeopleInElevator;
	static final int SLEEP_TIME = ElevatorScene.VISUALIZATION_WAIT_TIME / 2;
	
	public Elevator(int CurrentFloorForElevator, int NumberOfPeopleInElevator) {
		this.CurrentFloorForElevator = CurrentFloorForElevator;
		this.NumberOfPeopleInElevator = NumberOfPeopleInElevator;
	}
	
	@Override
	public void run() {
		while(true) {
			if(ElevatorScene.elevatorsMayDie) {
				return; // this stops the program from running for an infinite time - Bjarki
			}
			System.out.println("Elevator.java");
			ElevatorScene.firstFloorInSemaphore.release(6 - NumberOfPeopleInElevator);// signal - Bjarki
			System.out.println("people released into elevator floor");
				
			try {
				// elevator waits for people to get into elevator
				// missing mutex when there are more than one elevator
				ElevatorScene.elevatorWaitSemaphore.acquire();
				System.out.println("now I can go to floor 2");
				//
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			/*
			try {
				Thread.sleep(SLEEP_TIME);
				ElevatorScene.firstFloorInSemaphore.acquire(ElevatorScene.scene.getNumberOfPeopleInElevator(0));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			*/
			
			
			
			
			// move elevator to next floor
			if(ElevatorScene.scene.getCurrentFloorForElevator(0) < ElevatorScene.scene.getNumberOfFloors()) {
				System.out.println("now I can go to next floor");
				System.out.println("Current floor" + ElevatorScene.scene.getCurrentFloorForElevator(0));
				ElevatorScene.incrementCurrentElevatorFloor(0);
				ElevatorScene.outOfElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(0)).release();
			}else {
				System.out.println("now I can go to ground floor");
				ElevatorScene.scene.currentElevatorToFirstFloor(0);
			}
			
			
			// release people from elevator
			System.out.println(ElevatorScene.personCountInElevator.get(0));
			//ElevatorScene.secondFloorOutSemaphore.release(ElevatorScene.personCountInElevator.get(0));
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			/*
			try {
				ElevatorScene.elevatorWaitSemaphore2.acquire();
				System.out.println("now I can go to floor 1");
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			

		}
	}
}


/*
for(Semaphore floorWait :  ElevatorScene.outOfElevatorFloorsSem) {
    floorWait.release();
}
*/
