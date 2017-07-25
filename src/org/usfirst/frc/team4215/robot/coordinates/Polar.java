package org.usfirst.frc.team4215.robot.coordinates;

public class Polar {

	private double r;	
	private double theta;
	private double rotation;
	private double rotation_rate;

	public Polar(double r, double theta, double rotate, double rate) {
		this.r = r;
		this.theta = theta;
		this.rotation = rotate;
		this.rotation_rate = rate;
	}

	public double getR() {
		return this.r;
	}

	public double getTheta() {
		return this.theta;
	}

	public double getRotation() {
		return this.rotation;
	}

	public double getRotationRate() {
		return this.rotation_rate;
	}
}
