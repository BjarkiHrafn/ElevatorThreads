package com.ru.usty.elevator;

public class Elevator implements Runnable {

	int CurrentFloorForElevator, NumberOfPeopleInElevator;
	
	public Elevator(int CurrentFloorForElevator, int NumberOfPeopleInElevator) {
		this.CurrentFloorForElevator = CurrentFloorForElevator;
		this.NumberOfPeopleInElevator = NumberOfPeopleInElevator;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
			//
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("now I can go to floor 2");
		// move elevator to second floor
		ElevatorScene.incrementCurrentElevatorFloor(0);
		// release people from elevator
		System.out.println(ElevatorScene.personCountInElevator.get(0));
		ElevatorScene.secondFloorOutSemaphore.release(ElevatorScene.personCountInElevator.get(0));
	}
}
