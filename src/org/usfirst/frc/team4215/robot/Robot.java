package org.usfirst.frc.team4215.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.CANTalon.TalonControlMode;
import org.usfirst.frc.team4215.robot.coordinates.*;
import org.usfirst.frc.team4215.robot.diagnostics.CSVLogger;
import org.usfirst.frc.team4215.robot.motors.Drivetrain;
import org.usfirst.frc.team4215.robot.teleop.Driver;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	final String leftAutonomous = "Left";
	final String centerAutonomous = "Center";
	final String rightAutonomous = "Right";
	final String PIDAutonomous = "PID";

	String autonomous_selected;
	SendableChooser<String> autonomous_chooser;

	Drivetrain drivetrain;
	Timer timer;
	CSVLogger logger;
	
	Driver driver;
	//Autonomous autonomous;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// TODO: temp log
		// System.out.println("robotInit");

		timer = new Timer();
		autonomous_chooser = new SendableChooser<>();
		autonomous_chooser.addObject(leftAutonomous, leftAutonomous);
		autonomous_chooser.addObject(centerAutonomous, centerAutonomous);
		autonomous_chooser.addObject(rightAutonomous, rightAutonomous);
		autonomous_chooser.addDefault(PIDAutonomous, PIDAutonomous);
		SmartDashboard.putData("Autonomous Choice", autonomous_chooser);
		
		driver = Driver.Create();
		drivetrain = Drivetrain.Create();
	}

	@Override
	public void autonomousInit() {
		// TODO: temp log
		// System.out.println("autonomousInit");
		
		autonomous_selected = autonomous_chooser.getSelected();
		System.out.println("Autonomous Choice:" + autonomous_selected);

		switch (autonomous_selected) {
			case leftAutonomous:
				break;
			case centerAutonomous:
				break;
			case rightAutonomous:
				break;
			default:
				break;
		}

		logger = CSVLogger.create(true);
		timer.start();

		drivetrain.configureFromDashboard();
		
		int auto_x = (int)SmartDashboard.getNumber("Autonomous_x", 20);
		int auto_y = (int)SmartDashboard.getNumber("Autonomous_y", 0);
		SmartDashboard.putNumber("Autonomous_x", auto_x);
		SmartDashboard.putNumber("Autonomous_y", auto_y);
		Polar p = CoordinatesUtility.ToPolar(auto_x, auto_y, false);

		drivetrain.goPolarPosition(p);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		// TODO: temp log
		// System.out.println("autonomousPeriodic");
		//System.out.println("autonomousPeriodic: " + autoSelected);
		logger.append(timer.get());
		drivetrain.log(logger);
		logger.newline();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopInit() {
		drivetrain.disableControl();
		drivetrain.changeControlMode(TalonControlMode.PercentVbus);
		drivetrain.enableControl();		
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		// TODO: temp log
		// System.out.println("teleopPeriodic");

		Polar p = this.driver.readPolar();
		this.drivetrain.goPolar(p);
	}

	/**
	 * This function is called before test mode
	 */
	@Override
	public void testInit() {
		// TODO: temp log
		// System.out.println("testInit");

		/*
		logger = CSVLogger.create(new String[] { "data" }, new String[] { "units"});
		timer.start();

		drivetrain.setTalonControlMode(TalonControlMode.Position);
		//drivetrain.resetEncoderPosition();
		drivetrain.setPID(Kp, Ki, Kd);
		PolarCoordinates p = PolarCoordinates.FromCartesian(20, 0);
		drivetrain.goPolar(p.getRadians(), p.getTheta(), 0.0);
		*/
		//drivetrain.changeControlMode(TalonControlMode.Position);
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {

		//this.driver.testPeriodic();

		// TODO: temp log
		// System.out.println("testPeriodic");
		//logger.append(timer.get());
		//drivetrain.log(logger);
		//logger.newline();
	}
	
	@Override
	public void disabledInit() {
		// TODO: temp log
		// System.out.println("disabledInit");
		
		if (logger != null)
		{
			logger.close();
		}
		timer.stop();
		LiveWindow.setEnabled(false);
		drivetrain.logConsole();
		//drivetrain.disableControl();
	}
	
	@Override
	public void disabledPeriodic() {
		// TODO: temp log
		// System.out.println("disabledPeriodic");
	}

	@Override
	public void robotPeriodic() {
		// TODO: temp log
		// System.out.println("robotPeriodic");
	}
	
	public void updateSmartDashboard() {
		
		double Kp = SmartDashboard.getNumber("PID_Kp", 0.0);
		double Ki = SmartDashboard.getNumber("PID_Ki", 0.0);
		double Kd = SmartDashboard.getNumber("PID_Kd", 0.0);
		SmartDashboard.putNumber("PID_Kp", Kp);
		SmartDashboard.putNumber("PID_Ki", Ki);
		SmartDashboard.putNumber("PID_Kd", Kd);
				
		int auto_x = (int)SmartDashboard.getNumber("Autonomous_x", 20);
		int auto_y = (int)SmartDashboard.getNumber("Autonomous_y", 0);
		SmartDashboard.putNumber("Autonomous_x", (double)auto_x);
		SmartDashboard.putNumber("Autonomous_y", (double)auto_y);
		
		double closeloop_ramp_rate = SmartDashboard.getNumber("Drivetrain_closeloop_ramp_rate", 3.0);
		double voltage_ramp_rate = SmartDashboard.getNumber("Drivetrain_voltage_ramp_rate", 3.0);
		SmartDashboard.putNumber("Drivetrain_closeloop_ramp_rate", closeloop_ramp_rate);
		SmartDashboard.putNumber("Drivetrain_voltage_ramp_rate", voltage_ramp_rate);
	}
}
