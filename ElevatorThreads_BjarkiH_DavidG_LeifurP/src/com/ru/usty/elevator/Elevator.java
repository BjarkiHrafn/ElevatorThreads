package com.ru.usty.elevator;

public class Elevator implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(ElevatorScene.elevatorsMayDie) {
			return; // this stops the program from running for an infinite time - Bjarki
		}
		for(int i = 0; i < 6; i++) {
			ElevatorScene.globalSemaphore.release();// signal - Bjarki
			//ElevatorScene.scene.getNumberOfPeopleInElevator(i);
		}
		
	}

}
