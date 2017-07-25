package org.usfirst.frc.team4215.robot.teleop;

import org.usfirst.frc.team4215.robot.Portmap;
import org.usfirst.frc.team4215.robot.coordinates.CoordinatesUtility.CoordinatesType;
import org.usfirst.frc.team4215.robot.coordinates.Polar;
import org.usfirst.frc.team4215.robot.motors.Drivetrain;

import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Joystick;

public class Driver {
	
	public enum JoytickControlType
	{
		ThreeAxis,
		DualJoytick
	}

	private static Driver instance;

	private Drivetrain drivetrain;
	private Joystick joystick;
	private JoytickControlType controlType;
	private boolean enabled = false;
	private CoordinatesType coordinates = CoordinatesType.Polar;
	
	private Driver() {
		this.drivetrain = Drivetrain.Create();
		this.joystick = new Joystick(Portmap.OPERATOR_DRIVE_LEFT_JOYSTICK_ID);
	}

	public static Driver Create() {
		if (instance == null) {
			instance = new Driver();
			
			// TODO: check drivetrain instance?
			// TODO: check joystick instance ?
		}
		
		return instance;
	}

	public boolean Enable()
	{
		try
		{
			// TODO: add lock
			if (this.enabled)
			{
				return this.enabled;
			}
			
			drivetrain.disableControl();
			drivetrain.changeControlMode(TalonControlMode.PercentVbus);
	
			this.enabled = true;
			
			return this.enabled;
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());

			this.enabled = false;
			return this.enabled;
		}
	}
	
	public boolean Disable()
	{
		try
		{
			// TODO: add lock
			if (!this.enabled)
			{
				return this.enabled;
			}
			
			drivetrain.disableControl();
	
			this.enabled = false;
			
			return this.enabled;
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());

			this.enabled = false;
			return this.enabled;
		}
	}

	public Polar readPolar()
	{
		double r = joystick.getMagnitude();
		double theta = joystick.getDirectionRadians();
		double rotation = joystick.getTwist();
		
		r = (Math.abs(r) < .05) ? 0.0 : r; 
		r = (Math.abs(theta) < .5) ? 0.0 : theta; 
		rotation = (Math.abs(rotation) < .05) ? 0.0 : rotation; 
		
		Polar p = new Polar(r, theta, rotation, 0.0);

		System.out.println("driver r     : " + p.getR());
		System.out.println("driver theta : " + p.getTheta());
		System.out.println("driver rot   : " + p.getRotation());		
		System.out.println("driver rotrt : " + p.getRotationRate());
		
		return p;
	}
	
	public void Go()
	{
		// TODO: add lock
		if (!this.enabled)
		{
			return;
		}
		
		switch (this.coordinates) {
			case Cartesian:
				break;
			case Polar:
				double r = joystick.getMagnitude();
				double theta = joystick.getDirectionRadians();
				double rotation = joystick.getTwist();
				
				r = (Math.abs(r) < .05) ? 0.0 : r; 
				r = (Math.abs(theta) < .5) ? 0.0 : theta; 
				rotation = (Math.abs(rotation) < .05) ? 0.0 : rotation; 
				
				Polar p = new Polar(r, theta, rotation, 0.0);

				System.out.println("driver r     : " + p.getR());
				System.out.println("driver theta : " + p.getTheta());
				System.out.println("driver rot   : " + p.getRotation());		
				System.out.println("driver rotrt : " + p.getRotationRate());		
				
				this.drivetrain.goPolar(p);	
				break;
			default:
				break;
		}
	}
	
	public void testInit() {
		System.out.println("stick : " + joystick.getName());
		return;
	}
	
	public void testPeriodic() {
		Polar p = this.readPolar();
		
		System.out.println("driver r     : " + p.getR());
		System.out.println("driver theta : " + p.getTheta());
		System.out.println("driver rot   : " + p.getRotation());

		return;
	}
}
