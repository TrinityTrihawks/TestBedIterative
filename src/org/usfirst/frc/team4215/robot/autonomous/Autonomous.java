package org.usfirst.frc.team4215.robot.autonomous;

import org.usfirst.frc.team4215.robot.teleop.Driver;

public class Autonomous {

	private static Autonomous instance;
	
	public static Autonomous Create(String routineName)
	{
		if (instance == null) {
			instance = new Autonomous();
		}
		
		return instance;
	}

	private Autonomous() {
		// TODO Auto-generated constructor stub
	}

}
