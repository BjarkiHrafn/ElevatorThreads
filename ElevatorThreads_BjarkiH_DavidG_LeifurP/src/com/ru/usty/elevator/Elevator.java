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
			
			// Permits for how many spaces there are left in elevator
			System.out.println("Elevator.java");
			ElevatorScene.inToElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(0)).release(6 - ElevatorScene.personCountInElevator.get(0));
			System.out.println("people released into elevator floor");
				
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			// Fix permits back in case there are not 6 going into elevator
			try {
				ElevatorScene.inToElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(0)).acquire(6 - ElevatorScene.personCountInElevator.get(0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// close the doors
			
			
			
			
			
			
			// move elevator to next floor
			System.out.println("----MY DEBUG----");
			System.out.println("Current elevator floor: " + CurrentFloorForElevator);
			System.out.println("People waiting at floor: " + ElevatorScene.scene.getNumberOfPeopleWaitingAtFloor(ElevatorScene.scene.getCurrentFloorForElevator(0)));
			System.out.println("Current people in elivator: " + ElevatorScene.scene.getNumberOfPeopleInElevator(0));
			System.out.println("----MY DEBUG----");
			if(ElevatorScene.scene.getCurrentFloorForElevator(0) < ElevatorScene.scene.getNumberOfFloors() - 1) {
				System.out.println("now I can go to next floor");
				ElevatorScene.incrementCurrentElevatorFloor(0);
			}else {
				System.out.println("now I can go to ground floor");
				ElevatorScene.scene.currentElevatorToFirstFloor(0);
				
			}
			ElevatorScene.outOfElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(0)).release(ElevatorScene.scene.getNumberOfPeopleInElevator(0));
			
			
			
			// release people from elevator
			System.out.println(ElevatorScene.personCountInElevator.get(0));
			
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			
			try {
				ElevatorScene.outOfElevatorFloorsSem.get(ElevatorScene.scene.getCurrentFloorForElevator(0)).acquire(ElevatorScene.personCountInElevator.get(0));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

