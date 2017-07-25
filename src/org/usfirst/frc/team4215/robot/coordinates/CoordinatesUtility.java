package org.usfirst.frc.team4215.robot.coordinates;

import org.usfirst.frc.team4215.robot.Portmap;

public class CoordinatesUtility {
	
	public enum CoordinatesType {
		Cartesian,
		Polar
	}
	
	public CoordinatesUtility() {
		// TODO Auto-generated constructor stub
	}
	
	static public Polar ToPolar(double x, double y, boolean actionIsRotation)
	{
		System.out.println("ToPolar x " + x);
		System.out.println("ToPolar y " + y);
		
		if (x == 0 && y == 0)
		{
			return new Polar(0, 0, 0, 0);
		}
		
		double r, theta, rotate, rotate_rate;
		
		if (actionIsRotation)
		{
			r = 0.0;
			theta = 0.0;

			rotate = 0.0;
			rotate_rate = 0.0;
		}
		else
		{
			r = Math.sqrt( Math.pow(x,2) + Math.pow(y,2) );

			theta = Math.atan2(y,x);
			
			/*
			// TODO: normalize radians to 0 ??
			if (theta == (2 * Math.PI))
			{
				theta = 0.0;
			}
			*/
			
			if (Portmap.RobotCentricCoordinates == true)
			{
				// Need to adjust orientation ???
				// theta = Math.PI / 4 - theta;			
			}
		
			rotate = 0.0;
			rotate_rate = 0.0;
		}
		
		System.out.println("ToPolar r " + r);
		System.out.println("ToPolar theta " + theta);
		System.out.println("ToPolar rotation " + rotate);
		System.out.println("ToPolar rate " + rotate_rate);
		
		return new Polar(r, theta, rotate, rotate_rate);
	}
	
	static public Cartesian ToCartesian(Polar p)
	{
		return CoordinatesUtility.ToCartesian(
				p.getR(), 
				p.getTheta(),
				p.getRotation(),
				p.getRotationRate());
	}

	static public Cartesian ToCartesian(double r, double theta, double rotate, double rate)
	{
		System.out.println("ToCartesian r " + r);
		System.out.println("ToCartesian theta " + theta);
		System.out.println("ToCartesian rotate " + r);
		System.out.println("ToCartesian rate " + theta);

		return new Cartesian(0,0);
	}
}
