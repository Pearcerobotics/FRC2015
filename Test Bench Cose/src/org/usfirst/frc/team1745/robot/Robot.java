
package org.usfirst.frc.team1745.robot;


import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.hal.CanTalonSRX;

/**
 * This is a demo program showing the use of the RobotDrive class.
 * The SampleRobot class is the base of a robot application that will automatically call your
 * Autonomous and OperatorControl methods at the right time as controlled by the switches on
 * the driver station or the field controls.
 *
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SampleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 *
 * WARNING: While it may look like a good choice to use for your code if you're inexperienced,
 * don't. Unless you know what you are doing, complex code will be much more difficult under
 * this system. Use IterativeRobot or Command-Based instead if you're new.
 */
public class Robot extends SampleRobot 
{
    RobotDrive myRobot;
    CANTalon myTalonZero; // Lift
    CANTalon myTalonOne; //Front Left
    CANTalon myTalonTwo; //Back Left
    CANTalon myTalonThree; //Front Right
    CANTalon myTalonFour; //Back Right
    DoubleSolenoid mySolenoid;
    Joystick stick;
    Joystick gamepad;
    Compressor myCompressor;
    /*SpeedController frontLeftMotor;
    SpeedController frontRightMotor;
    SpeedController backLeftMotor;
    SpeedController backRightMotor;*/
    
    

    public Robot() 
    {
       
        myTalonZero = new CANTalon(0); // Lift
        myTalonOne = new CANTalon(1); // Front Left
        myTalonTwo = new CANTalon(2); // Back left
        myTalonThree = new CANTalon(3); // Front Right
        myTalonFour = new CANTalon(4); // Back Right
       
        mySolenoid = new DoubleSolenoid( 2, 0, 1);
        myCompressor = new Compressor(2);
        
        stick = new Joystick(0);
        gamepad = new Joystick(1);
       
        myRobot = new RobotDrive(myTalonOne,myTalonTwo,myTalonThree,myTalonFour);
        myRobot.setExpiration(0.1);
        
    }

    /**
     * Drive left & right motors for 2 seconds then stop
     */
    public void autonomous() 
    {
       // myRobot.setSafetyEnabled(false);
       // myRobot.drive(-0.5, 0.0);	// drive forwards half speed
       // Timer.delay(2.0);		//    for 2 seconds
       // myRobot.drive(0.0, 0.0);	// stop robot
    }

    /**
     * Runs the motors with Mecanum Drive and lift.
     */
    public void operatorControl() 
    {
        myRobot.setSafetyEnabled(true);
        while (isOperatorControl() && isEnabled()) 
        {
           // spin motor for lift on joy stick throttle
        	myTalonZero.set(stick.getThrottle()*0.1);
        	
        	//Mecanum drive
        	myRobot.mecanumDrive_Polar(stick.getX(), stick.getY(), stick.getTwist());
        	
        	//Turn on compressor if more air is needed
            if(myCompressor.getPressureSwitchValue())
            {
            	myCompressor.stop();
            }
            else
            {
            	myCompressor.start();
            }
            
            //Actuate Solenoid for claw
        	if(stick.getRawButton(1))
            	mySolenoid.set(Value.kForward);
            else
            	mySolenoid.set(Value.kReverse);
        	
        	//System.out.println("Tallon Value: " + myTalon..toString());
        	System.out.println("Solenoid Switch Value: " + mySolenoid.get().toString());
        	System.out.println("Compessor Switch Value: " + myCompressor.getPressureSwitchValue());
        	
        	Timer.delay(0.005);		// wait for a motor update time
        }
        
    }
    
    

    /**
     * Runs during test mode
     */
    public void test() 
    {
    	System.out.println("Welcome to Test Mode!");
    }
}
