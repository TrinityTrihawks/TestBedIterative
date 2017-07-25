package org.usfirst.frc.team4215.robot.motors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.usfirst.frc.team4215.robot.*;
import org.usfirst.frc.team4215.robot.coordinates.*;
import org.usfirst.frc.team4215.robot.diagnostics.CSVLogger;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain {

	// 
	private int index_Front_Right = 0;
	private int index_Back_Right  = 1; 
	private int index_Back_Left   = 2;
	private int index_Front_Left  = 3;
	private int index_SIZE  = 4;
	
	private CANTalon.TalonControlMode wheelcontrolmode = CANTalon.TalonControlMode.Position;
	
	private final int profileID = 0;

	private final double MIN_CLOSEDLOOP_R = 0.0;
	private final double MIN_CLOSEDLOOP_ROTATION = 0.0;

	//
	Set<CANTalon> wheelsSet = new HashSet<CANTalon>();
	CANTalon[] wheels = new CANTalon[index_SIZE];
	
	private double controlModePowerScalar = 1.0;
	
	private static Drivetrain instance;
	
	public static Drivetrain Create() {
		if (instance == null) {
			instance = new Drivetrain();
		}
		return instance;
	}
	
	private Drivetrain() {
		this.wheels[index_Front_Right] = Wheel.Create(Portmap.PWM_Talon_Front_Right_ID, false, true);
		this.wheels[index_Back_Right] = Wheel.Create(Portmap.PWM_Talon_Back_Right_ID, false, true);
		this.wheels[index_Back_Left] = Wheel.Create(Portmap.PWM_Talon_Back_Left_ID, true, false);
		this.wheels[index_Front_Left] = Wheel.Create(Portmap.PWM_Talon_Front_Left_ID, true, false);
				
		this.wheelsSet = new HashSet<CANTalon>(Arrays.asList(wheels));
		
		this.changeControlMode(CANTalon.TalonControlMode.Position);
		
		LiveWindow.addActuator("Drivetrain", "front left", this.wheels[index_Front_Left]);
		LiveWindow.addActuator("Drivetrain", "front right", this.wheels[index_Front_Right]);
		LiveWindow.addActuator("Drivetrain", "back left", this.wheels[index_Back_Left]);
		LiveWindow.addActuator("Drivetrain", "back right", this.wheels[index_Back_Right]);
	}

	public void setPID(double Kp, double Ki, double Kd) {
		wheelsSet.forEach(wheel -> wheel.setPID(Kp, Ki, Kd) );
	}
	
	public void setPID(double Kp, double Ki, double Kd, double Kf, int iZone, double closeLoopRampRate) {
		wheelsSet.forEach(wheel -> wheel.setPID(Kp, Ki, Kd, Kf, iZone, closeLoopRampRate, this.profileID) );
	}
	
	public void changeControlMode(CANTalon.TalonControlMode mode) {
		
		this.disableControl();

		// TODO: add lock ??
		this.wheelcontrolmode = mode;
		
		wheelsSet.forEach(wheel -> wheel.changeControlMode(mode) );
		
		if(mode == CANTalon.TalonControlMode.Position){
			// 4096 ticks = 1 axle revolution
			// Go input is inches, scalar = divide dist/wheel circum
			// Talon input is rotations
			this.controlModePowerScalar = 1 / Wheel.circumference;
			wheelsSet.forEach(wheel -> wheel.setPosition(0.0) );
			wheelsSet.forEach(wheel -> wheel.setEncPosition(0) );
		}
		else if(mode == CANTalon.TalonControlMode.Speed) {
			this.controlModePowerScalar = 1/(Wheel.circumference * 60);
		}
		else if(mode == CANTalon.TalonControlMode.PercentVbus) {
			this.controlModePowerScalar = 1.0;
		}
		else
		{
			this.controlModePowerScalar = 0.0;		
		}

		System.out.println("setTalonControlMode: " + mode + " scalar: " + this.controlModePowerScalar);
		
		this.enableControl();
	}
	
	public void resetEncoderPosition()
	{
		int position = 0;
		wheelsSet.forEach(wheel -> wheel.setEncPosition(position) );
		
		System.out.println("resetEncoderPosition: " + position);
	}

	public void resetPosition()
	{
		double position = 0.0;
		wheelsSet.forEach(wheel -> wheel.setPosition(position) );
		
		System.out.println("resetPosition: " + position);
	}

	public void goPolarPosition(Polar p)
	{
		double [] power = new double [] { 0.0, 0.0, 0.0, 0.0 };

		System.out.println("goPolarPosition start");

		this.changeControlMode(CANTalon.TalonControlMode.Position);
		
		// convert distance (inches) to rotations
		double r = p.getR() * (4096 / Wheel.circumference );
		double rotation = p.getRotation();
		double theta = p.getTheta();

		if (Math.abs(r) < MIN_CLOSEDLOOP_R && Math.abs(rotation) < MIN_CLOSEDLOOP_ROTATION) 
		{ 
			return; 
		} 	      

		double xP = r * Math.cos(theta - (Math.PI / 4));
		double yP = r * Math.sin(theta + (Math.PI / 4));

        power[index_Front_Left]  = xP + rotation;
        power[index_Front_Right] = yP - rotation;
        power[index_Back_Left]   = yP + rotation;
        power[index_Back_Right]  = xP - rotation;
		        
        // set POWER (ok, Rotations for closed loop)
		this.wheels[index_Front_Left].set( power[index_Front_Left] );
		this.wheels[index_Back_Left].set( power[index_Back_Left] );
		this.wheels[index_Front_Right].set( power[index_Front_Right] );
		this.wheels[index_Back_Right].set( power[index_Back_Right] );
        
		System.out.println("set lf " + power[index_Front_Left]);
		System.out.println("set lb " + power[index_Back_Left]);
		System.out.println("set rf " + power[index_Front_Right]);
		System.out.println("set rb " + power[index_Back_Right]);
		
		System.out.println("goPolarPosition end");
		return;
	}
	
	public void goPolar(Polar p)
	{
		double [] power = new double [] { 0.0, 0.0, 0.0, 0.0 }; 

		System.out.println("goPolar start");

		// Assume this is set during TeleopInit()
		/*
		if (this.controlmode != CANTalon.TalonControlMode.PercentVbus) {
			this.setTalonControlMode(CANTalon.TalonControlMode.PercentVbus);
		}
		*/

		// if (Math.abs(r) < MIN_DRIVE_VALUE && Math.abs(rotation) < MIN_DRIVE_VALUE) { return; } 	      

		// Validate r ??
		//r = (r > 1.0) ? 1.0 : r; 
		//r = (r < -1.0) ? -1.0 : r; 
				
		double theta, rotation;
		double xP, yP;

		theta = p.getTheta();
		rotation = p.getRotation();
		rotation = 0.0;
		
		if (Portmap.RobotCentricCoordinates) {
			// Need to adjust orientation ???
			//theta = 2 * Math.PI - Math.PI / 2 + theta;			
		}

		xP = p.getR() * Math.cos(theta + (Math.PI / 4));
        yP = p.getR() * Math.sin(theta + (Math.PI / 4));			
		 
        power[index_Front_Left]  = xP + rotation;
        power[index_Front_Right] = yP - rotation;
        power[index_Back_Left]   = yP + rotation;
        power[index_Back_Right]  = xP - rotation;
		
        // find max, scale power
        /*
		double scale = power[0];
		double temp;
		for (int i=1; i<power.length; i++) { 
			temp = Math.abs(power[i]);
			if (temp > scale) {
				scale = temp;
			}
		}
		
		if (scale != 0) {
			for (int i=0; i<power.length; i++) { 
				power[i] /= scale;
			}
		}
        */
        
        // set POWER
		this.wheels[index_Front_Left].set( power[index_Front_Left] );
		this.wheels[index_Back_Left].set( power[index_Back_Left] );
		this.wheels[index_Front_Right].set( power[index_Front_Right] );
		this.wheels[index_Back_Right].set( power[index_Back_Right] );
        
		System.out.println("goPolar power lf " + power[index_Front_Left]);
		System.out.println("goPolar power lb " + power[index_Back_Left]);
		System.out.println("goPolar power rf " + power[index_Front_Right]);
		System.out.println("goPolar power rb " + power[index_Back_Right]);

		System.out.println("goPolar end");
		return;
	}

	@SuppressWarnings("unused")
	public void goPolarSpeed(double r, double theta, double rotation)
	{
		double [] power = new double [] { 0.0, 0.0, 0.0, 0.0 }; 

		wheelsSet.forEach(wheel -> wheel.changeControlMode(CANTalon.TalonControlMode.Speed) );
		wheelsSet.forEach(wheel -> wheel.setEncPosition(0) );
		wheelsSet.forEach(wheel -> wheel.setPosition(0.0) );

		// if (Math.abs(r) < MIN_DRIVE_VALUE && Math.abs(rotation) < MIN_DRIVE_VALUE) { return; } 	      

		// Validate r ??
		// r = (r > 1.0) ? 1.0 : r; 
		// r = (r < -1.0) ? -1.0 : r; 
		
		// if position, convert distance to rotations
		r /= Wheel.circumference;
		
		double xP, yP;
	    
		if (Portmap.RobotCentricCoordinates) {
			// Need to adjust orientation ???
			theta = 2 * Math.PI - Math.PI / 2 + theta;			
		}

		xP = r * Math.cos(theta + (Math.PI / 4));
        yP = r * Math.sin(theta + (Math.PI / 4));			
		 
        power[index_Front_Left]  = xP + rotation;
        power[index_Front_Right] = yP - rotation;
        power[index_Back_Left]   = yP + rotation;
        power[index_Back_Right]  = xP - rotation;
		
        // find max, scale power
        if (false /*speed*/)
        {
			double scale = power[0];
			double temp;
			for (int i=1; i<power.length; i++) { 
				temp = Math.abs(power[i]);
				if (temp > scale) {
					scale = temp;
				}
			}
			
			if (scale != 0) {
				for (int i=0; i<power.length; i++) { 
					power[i] /= scale;
				}
			}
        }
                
        // set POWER
		this.wheels[index_Front_Left].set( power[index_Front_Left] );
		this.wheels[index_Back_Left].set( power[index_Back_Left] );
		this.wheels[index_Front_Right].set( power[index_Front_Right] );
		this.wheels[index_Back_Right].set( power[index_Back_Right] );
        
		System.out.println("goPolar power lf " + power[index_Front_Left]);
		System.out.println("goPolar power lb " + power[index_Back_Left]);
		System.out.println("goPolar power rf " + power[index_Front_Right]);
		System.out.println("goPolar power rb " + power[index_Back_Right]);

		return;
	}

	public void logConsole()
	{
		wheelsSet.forEach(wheel -> System.out.println(
				"WheelID: " + wheel.getDeviceID() +
				//" isSensorPresent: " + wheel.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative).toString() +
				" pos: " + wheel.getPosition() +
				" enc pos: " + wheel.getEncPosition() +
				" Kp: " + wheel.getP() +
				" Ki: " + wheel.getI() +
				" Kd: " + wheel.getD() +
				" Kf: " + wheel.getF() +
				" iZone: " + wheel.getIZone() +
				" closelooperror: " + wheel.getClosedLoopError() +
				" Iaccum: " + wheel.GetIaccum() +
				" PID: " + wheel.getPIDSourceType().toString()
		));
	}

	public void log(CSVLogger log)
	{
		if (log == null)
		{
			return;
		}
		
		// 
		wheelsSet.forEach(wheel -> log.append(wheel.getEncPosition()));
		wheelsSet.forEach(wheel -> log.append(wheel.getBusVoltage()));
		wheelsSet.forEach(wheel -> log.append(wheel.getTemperature()));
		wheelsSet.forEach(wheel -> log.append(wheel.getSpeed()));
		wheelsSet.forEach(wheel -> log.append(wheel.get()));
	}
	
	public void enableControl()
	{
		wheelsSet.forEach( (wheel) -> wheel.enableControl() );
	}

	public void disableControl()
	{
		wheelsSet.forEach( (wheel) -> wheel.set(0.0) );
		wheelsSet.forEach( (wheel) -> wheel.disableControl() );		
	}
	
	public void configureFromDashboard()
	{
		this.disableControl();
		
		double Kp = SmartDashboard.getNumber("PID_Kp", 0.5);
		double Ki = SmartDashboard.getNumber("PID_Ki", 0.0);
		double Kd = SmartDashboard.getNumber("PID_Kd", 0.0);
		double Kf = SmartDashboard.getNumber("PID_Kf", 0.0);
		int iZone = (int)SmartDashboard.getNumber("PID_iZone", 0);
		double closeloop_ramp_rate = SmartDashboard.getNumber("PID_closeloopRampRate", 3.0);
		this.setPID(Kp, Ki, Kd, Kf, iZone, closeloop_ramp_rate);
		SmartDashboard.putNumber("PID_Kp", Kp);
		SmartDashboard.putNumber("PID_Ki", Ki);
		SmartDashboard.putNumber("PID_Kd", Kd);
		SmartDashboard.putNumber("PID_Kf", Kf);
		SmartDashboard.putNumber("PID_iZone", iZone);
		SmartDashboard.putNumber("PID_closeloopRampRate", closeloop_ramp_rate);

//		double closeloop_ramp_rate = SmartDashboard.getNumber("Drivetrain_closeloop_ramp_rate", 3.0);
//		wheelsSet.forEach(wheel -> wheel.setCloseLoopRampRate(closeloop_ramp_rate) );
//		SmartDashboard.putNumber("Drivetrain_voltage_ramp_rate", voltage_ramp_rate);

		double voltage_ramp_rate = SmartDashboard.getNumber("Drivetrain_voltage_ramp_rate", 3.0);
		wheelsSet.forEach(wheel -> wheel.setVoltageRampRate(voltage_ramp_rate) );
		SmartDashboard.putNumber("Drivetrain_voltage_ramp_rate", voltage_ramp_rate);

		this.enableControl();
	}
}
