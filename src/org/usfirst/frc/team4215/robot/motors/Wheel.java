package org.usfirst.frc.team4215.robot.motors;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.StatusFrameRate;

import edu.wpi.first.wpilibj.PIDSourceType;

public class Wheel {

	////public final static double radius = 3.0; // inches
	////public final static double circumference = 2 * Math.PI * radius;
	public final static double circumference = 18.875;

	public static CANTalon Create(int port, boolean reverseOutput, boolean reverseSensor) {
		CANTalon talon = new CANTalon(port);
		talon.reverseOutput(reverseOutput);
		talon.reverseSensor(reverseSensor);
		talon.setProfile(0);
		talon.configNominalOutputVoltage(0, 0);
		talon.configPeakOutputVoltage(12, -12);
		
		talon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		talon.setPIDSourceType(PIDSourceType.kDisplacement);
		
		//talon.setAllowableClosedLoopErr(0);

		// modify framerate period of a particular status frame
		// general status is 10ms
		// feedback default is 20ms
		//talon.setStatusFrameRateMs(StatusFrameRate.Feedback, 5);

		// change in output throttle per T_ramprate, where T_ramprate = 10ms
		// throttle 10bit signed [-1023,1023]
		//talon.setVoltageRampRate(3);

		// change in output throttle per T_closedloop_ramprate, where T_closedloop_ramprate = 1ms
		// ([1023-0] / 1000 ms * T_RampRate), 
		//talon.setCloseLoopRampRate(.5);

		return talon;
	}
}
