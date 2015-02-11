
package org.usfirst.frc.team1745.robot;


/*import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;*/

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Vector;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.ControlMode;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
//import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
//import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj.vision.AxisCamera;
//import edu.wpi.first.wpilibj.vision.USBCamera;








import edu.wpi.first.wpilibj.vision.AxisCamera;

import org.usfirst.frc.team1745.robot.P51RobotDefine;
import org.usfirst.frc.team1745.robot.P51Camera;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ImageType;

public class Robot extends SampleRobot 
{
    RobotDrive myRobot;
    
    CANTalon frontLeft; //3
    CANTalon frontRight;//4
    CANTalon backLeft; 	//5
    CANTalon backRight; //6
    CANTalon lift; 		//7
    
    /*double frontLeftEnc;
    double frontRightEnc;
    double backLeftEnc;
    double backRightEnc;*/
    
    DoubleSolenoid mySolenoid;	//2
    //Compressor myCompressor;	//2
    
    Joystick stick;		//0
    Joystick gamepad;	//1
    
    SendableChooser dashBoard;
    //Command autoChooser;
    LiveWindow vFeed;
    SmartDashboard dBoard;
    
    boolean autoChoiceBin;
    boolean autoChoiceTote;
    boolean autoChoiceRobot;
    boolean autoChoiceNone;
    int autoChoice;  
    
    //int session;
    //Image frame;
    AxisCamera aCamera;
    //USBCamera usbCamera;
    //CameraServer cServer;
    P51Camera tServer;
    
    static Gyro gyro;
    
    static boolean polarDrive;
    static boolean cartesianDrive;

    JoystickButton armUpDrive;			//7
    JoystickButton armUpOp;				//7
    JoystickButton armDownDrive;		//8
    JoystickButton armDownOp;			//8
    JoystickButton clawDrive;			//10
    JoystickButton clawOp;				//10
    JoystickButton polarDriver;			//12
    JoystickButton polarOp;				//12
    JoystickButton cartesianDriver;		//11
    JoystickButton cartesianOp;			//11
    JoystickButton deadbandInc;			//3
    JoystickButton deadbandDec;			//4
    
    Toggle deadbandUp;
    Toggle deadbandDown;
    
    /*PIDController frontLeftPID;
    PIDController frontRightPID;
    PIDController backLeftPID;
    PIDController backRightPID;*/

    double frontLeftSpeed;
    double frontRightSpeed;
    double backLeftSpeed;
    double backRightSpeed;
    
    /*double xAxis;
    double yAxis;
    double twist;
    double magnitude;
    double angle;*/
    
    //Encoder frontLeftEnc;
    //Encoder frontRightEnc;
    //Encoder backLeftEnc;
    //Encoder backRightEnc;
    
    PrintWriter writer;
    
    
	//Images
	Image frame;
	Image binaryFrame;
	int imaqError;
	//CameraServer server;

	//Constants
	NIVision.Range TOTE_HUE_RANGE = new NIVision.Range(24, 49);	//Default hue range for yellow tote
	NIVision.Range TOTE_SAT_RANGE = new NIVision.Range(67, 255);	//Default saturation range for yellow tote
	NIVision.Range TOTE_VAL_RANGE = new NIVision.Range(49, 255);	//Default value range for yellow tote
	double AREA_MINIMUM = 0.5; //Default Area minimum for particle as a percentage of total image area
	double LONG_RATIO = 2.22; //Tote long side = 26.9 / Tote height = 12.1 = 2.22
	double SHORT_RATIO = 1.4; //Tote short side = 16.9 / Tote height = 12.1 = 1.4
	double SCORE_MIN = 75.0;  //Minimum score to be considered a tote
	double VIEW_ANGLE = 49.4; //View angle fo camera, set to Axis m1011 by default, 64 for m1013, 51.7 for 206, 52 for HD3000 square, 60 for HD3000 640x480
	NIVision.ParticleFilterCriteria2 criteria[] = new NIVision.ParticleFilterCriteria2[1];
	NIVision.ParticleFilterOptions2 filterOptions = new NIVision.ParticleFilterOptions2(0,0,1,1);
	Scores scores = new Scores();
	
    public Robot()  
    {
       
        lift = new CANTalon(P51RobotDefine.winch_CANID); // Lift
        frontLeft = new CANTalon(P51RobotDefine.leftFrontMecanum_CANID); // Front Left
        backLeft = new CANTalon(P51RobotDefine.leftBackMecanum_CANID); // Back left    
        frontRight = new CANTalon(P51RobotDefine.rightFrontMecanum_CANID); // Front Right
        backRight=new CANTalon(P51RobotDefine.rightBackMecanum_CANID); // Back Right
        //myCompressor = new Compressor(P51RobotDefine.PCM_CANID);
        //mySolenoid = new DoubleSolenoid(P51RobotDefine.PCM_CANID, P51RobotDefine.clawSolenoidForward_PCMChan, P51RobotDefine.clawSolenoidBackwards_PCMChan);
        
        stick = new Joystick(P51RobotDefine.driver_USBJoyStick);
        gamepad = new Joystick(P51RobotDefine.operator_USBJoyStick);
       
        myRobot = new RobotDrive(frontLeft,backLeft,frontRight,backRight);
        myRobot.setInvertedMotor(MotorType.kFrontRight, true);
        myRobot.setInvertedMotor(MotorType.kRearRight, true);
        myRobot.setExpiration(0.1);
        
        //unsure exactly how this works 
        //dashBoard.addObject("None", autoChoiceNone);
        //dashBoard.addObject("Bin", autoChoiceBin);
        //dashBoard.addObject("Tote", autoChoiceTote);
        //dashBoard.addDefault("Robot", autoChoiceRobot);
        //SmartDashboard.putData("Auto-Mode Choice", dashBoard);
        //or this
        /*SmartDashboard.putString("nOne", "None");
        SmartDashboard.putString("bIn", "Bin");
        SmartDashboard.putString("tOte", "Tote");
        SmartDashboard.putString("rObot", "Robot");
        //Same here
        autoChoiceBin=SmartDashboard.getBoolean("bIn");
        autoChoiceTote=SmartDashboard.getBoolean("tOte");
        autoChoiceRobot=SmartDashboard.getBoolean("rObott");
        autoChoiceNone=SmartDashboard.getBoolean("nOne");*/
        
        //frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
        // open the camera at the IP address assigned. This is the IP address that the camera
        // can be accessed through the web interface.
        //camera = new AxisCamera("10.1.91.100");
        
        vFeed = new LiveWindow();
        LiveWindow.setEnabled(true);
        
        //usbCamera = new USBCamera();
        //cServer=CameraServer.getInstance();
        tServer= new P51Camera();
        aCamera = new AxisCamera("10.17.45.11");
       
        //Need to get correct channel, 1 or 0
        gyro = new Gyro(P51RobotDefine.gyro_ANAChan);
        
        //Start in Polar Drive Mode
        polarDrive=true;
        cartesianDrive=false;
        
        armUpDrive= new JoystickButton(stick,P51RobotDefine.armUpButton_Driver);
        armUpOp = new JoystickButton(gamepad,P51RobotDefine.armUpButton_Operator);
        armDownDrive= new JoystickButton(stick,P51RobotDefine.armDownButton_Driver);
        armDownOp = new JoystickButton(gamepad,P51RobotDefine.armDownButton_Operator);
        clawDrive= new JoystickButton(stick,P51RobotDefine.clawControl_Driver);
        clawOp = new JoystickButton(gamepad,P51RobotDefine.clawControl_Operator);
        polarDriver= new JoystickButton(stick,P51RobotDefine.mecanumModeToPolar_Driver);
        polarOp = new JoystickButton(gamepad,P51RobotDefine.mecanumModeToPolar_Operator);
        cartesianDriver= new JoystickButton(stick,P51RobotDefine.mecanumModeToCartesian_Driver);
        cartesianOp = new JoystickButton(gamepad,P51RobotDefine.mecanumModeToCartesian_Operator);
        deadbandInc = new JoystickButton(stick, P51RobotDefine.deadbandExpIncrement);
        deadbandDec = new JoystickButton(stick, P51RobotDefine.deadbandExpDecrement);
        
        deadbandUp = new Toggle(false);
        deadbandDown = new Toggle(false);
        
        //LiveWindow.addSensor("Camera", "Camera", tServer);
        //LiveWindow.setEnabled(true);
        //LiveWindow.run();
        
        /*xAxis=0;
        yAxis=0;
        twist=0;
        magnitude=0;
        angle=0;*/
        
        //frontLeftEnc = new Encoder(0,1,false,EncodingType.k2X);
        //frontRightEnc = new Encoder(0,1,true,EncodingType.k2X);
        //backLeftEnc = new Encoder(0,1,false,EncodingType.k2X);
        //backRightEnc = new Encoder(0,1,true,EncodingType.k2X);
        
        //tServer.startAutomaticCapture();
        try {
			writer = new PrintWriter("P51Log.in");
		} catch (FileNotFoundException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
        
    	frontLeft.changeControlMode(ControlMode.valueOf(2));
    	frontRight.changeControlMode(ControlMode.valueOf(2));
    	backLeft.changeControlMode(ControlMode.valueOf(2));
    	backRight.changeControlMode(ControlMode.valueOf(2));
    
    	

    	
    }
		public class ParticleReport implements Comparator<ParticleReport>, Comparable<ParticleReport>
		{
			double PercentAreaToImageArea;
			double Area;
			double ConvexHullArea;
			double BoundingRectLeft;
			double BoundingRectTop;
			double BoundingRectRight;
			double BoundingRectBottom;
			
			public int compareTo(ParticleReport r)
			{
				return (int)(r.Area - this.Area);
			}
			
			public int compare(ParticleReport r1, ParticleReport r2)
			{
				return (int)(r1.Area - r2.Area);
			}
		};

		//Structure to represent the scores for the various tests used for target identification
		public class Scores {
			double Trapezoid;
			double LongAspect;
			double ShortAspect;
			double AreaToConvexHullArea;
		};

    
		public void robotInit() {
		    // create images
			frame = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
			binaryFrame = NIVision.imaqCreateImage(ImageType.IMAGE_U8, 0);
			criteria[0] = new NIVision.ParticleFilterCriteria2(NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA, AREA_MINIMUM, 100.0, 0, 0);
			//server = CameraServer.getInstance();
			
			//Put default values to SmartDashboard so fields will appear
			SmartDashboard.putNumber("Tote hue min", TOTE_HUE_RANGE.minValue);
			SmartDashboard.putNumber("Tote hue max", TOTE_HUE_RANGE.maxValue);
			SmartDashboard.putNumber("Tote sat min", TOTE_SAT_RANGE.minValue);
			SmartDashboard.putNumber("Tote sat max", TOTE_SAT_RANGE.maxValue);
			SmartDashboard.putNumber("Tote val min", TOTE_VAL_RANGE.minValue);
			SmartDashboard.putNumber("Tote val max", TOTE_VAL_RANGE.maxValue);
			SmartDashboard.putNumber("Area min %", AREA_MINIMUM);
		}
        
    

    /**
     * Check Driver Station for which Autonomous mode to Run
     */
    public void autonomous() 
    {
    	myRobot.setSafetyEnabled(false);
    	
		while(isAutonomous()&&isEnabled())
		{
    	
    	
		//read file in from disk. For this example to run you need to copy image20.jpg from the SampleImages folder to the
		//directory shown below using FTP or SFTP: http://wpilib.screenstepslive.com/s/4485/m/24166/l/282299-roborio-ftp
		NIVision.imaqReadFile(frame, "/home/lvuser/SampleImages/image20.jpg");

		//Update threshold values from SmartDashboard. For performance reasons it is recommended to remove this after calibration is finished.
		TOTE_HUE_RANGE.minValue = (int)SmartDashboard.getNumber("Tote hue min", TOTE_HUE_RANGE.minValue);
		TOTE_HUE_RANGE.maxValue = (int)SmartDashboard.getNumber("Tote hue max", TOTE_HUE_RANGE.maxValue);
		TOTE_SAT_RANGE.minValue = (int)SmartDashboard.getNumber("Tote sat min", TOTE_SAT_RANGE.minValue);
		TOTE_SAT_RANGE.maxValue = (int)SmartDashboard.getNumber("Tote sat max", TOTE_SAT_RANGE.maxValue);
		TOTE_VAL_RANGE.minValue = (int)SmartDashboard.getNumber("Tote val min", TOTE_VAL_RANGE.minValue);
		TOTE_VAL_RANGE.maxValue = (int)SmartDashboard.getNumber("Tote val max", TOTE_VAL_RANGE.maxValue);

		//Threshold the image looking for yellow (tote color)
		NIVision.imaqColorThreshold(binaryFrame, frame, 255, NIVision.ColorMode.HSV, TOTE_HUE_RANGE, TOTE_SAT_RANGE, TOTE_VAL_RANGE);

		//Send particle count to dashboard
		int numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
		SmartDashboard.putNumber("Masked particles", numParticles);

		//Send masked image to dashboard to assist in tweaking mask.
		//CameraServer.getInstance().setImage(binaryFrame);

		//filter out small particles
		float areaMin = (float)SmartDashboard.getNumber("Area min %", AREA_MINIMUM);
		criteria[0].lower = areaMin;
		imaqError = NIVision.imaqParticleFilter4(binaryFrame, binaryFrame, criteria, filterOptions, null);

		//Send particle count after filtering to dashboard
		numParticles = NIVision.imaqCountParticles(binaryFrame, 1);
		SmartDashboard.putNumber("Filtered particles", numParticles);

		if(numParticles > 0)
		{
			//Measure particles and sort by particle size
			Vector<ParticleReport> particles = new Vector<ParticleReport>();
			for(int particleIndex = 0; particleIndex < numParticles; particleIndex++)
			{
				ParticleReport par = new ParticleReport();
				par.PercentAreaToImageArea = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA_BY_IMAGE_AREA);
				par.Area = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_AREA);
				par.ConvexHullArea = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_CONVEX_HULL_AREA);
				par.BoundingRectTop = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_TOP);
				par.BoundingRectLeft = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_LEFT);
				par.BoundingRectBottom = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_BOTTOM);
				par.BoundingRectRight = NIVision.imaqMeasureParticle(binaryFrame, particleIndex, 0, NIVision.MeasurementType.MT_BOUNDING_RECT_RIGHT);
				particles.add(par);
			}
			particles.sort(null);

			//This example only scores the largest particle. Extending to score all particles and choosing the desired one is left as an exercise
			//for the reader. Note that the long and short side scores expect a single tote and will not work for a stack of 2 or more totes.
			//Modification of the code to accommodate 2 or more stacked totes is left as an exercise for the reader.
			scores.Trapezoid = TrapezoidScore(particles.elementAt(0));
			SmartDashboard.putNumber("Trapezoid", scores.Trapezoid);
			scores.LongAspect = LongSideScore(particles.elementAt(0));
			SmartDashboard.putNumber("Long Aspect", scores.LongAspect);
			scores.ShortAspect = ShortSideScore(particles.elementAt(0));
			SmartDashboard.putNumber("Short Aspect", scores.ShortAspect);
			scores.AreaToConvexHullArea = ConvexHullAreaScore(particles.elementAt(0));
			SmartDashboard.putNumber("Convex Hull Area", scores.AreaToConvexHullArea);
			boolean isTote = scores.Trapezoid > SCORE_MIN && (scores.LongAspect > SCORE_MIN || scores.ShortAspect > SCORE_MIN) && scores.AreaToConvexHullArea > SCORE_MIN;
			boolean isLong = scores.LongAspect > scores.ShortAspect;

			//Send distance and tote status to dashboard. The bounding rect, particularly the horizontal center (left - right) may be useful for rotating/driving towards a tote
			SmartDashboard.putBoolean("IsTote", isTote);
			SmartDashboard.putNumber("Distance", computeDistance(binaryFrame, particles.elementAt(0), isLong));
		} else {
			SmartDashboard.putBoolean("IsTote", false);
		}

		Timer.delay(0.005);				// wait for a motor update time
    	
    	
    	
    	
    	if(autoChoiceNone)
    		autoChoice=0;
    	if(autoChoiceBin)
    		autoChoice=1;
    	if(autoChoiceTote);
    		autoChoice=2;
		if(autoChoiceRobot)
			autoChoice=3;
		if(!(autoChoiceNone||autoChoiceBin||autoChoiceTote||autoChoiceRobot))
			autoChoice=3;
		

			//@FIX COmpressor
			//Turn on compressor if more air is needed
           // if(myCompressor.getPressureSwitchValue())
            {
            	//@TODO Fix Compressor
            //	myCompressor.stop();
            	//writer.println("Compressor off");
            }
            //else
            {
            	//@TODO Fix COmpressor
            	//myCompressor.start();
            	//writer.println("Compressor on");
            }
            
            //switch between different auto modes via SmartDashboard
			switch(autoChoice)
			{
				case(0)://"None" autonomous selected
				{
					//writer.println("Robot will remain still.");
					break;
				}
				
				case(1)://"Bin" autonomous selected
				{
					//writer.println("Robot will pickup bin and move to auto zone.");
					break;
				}
				
				case(2)://"Tote" autonomous selected
				{
					//writer.println("Robot will pickup yellow tote and move to auto zone.");
					break;
				}
				
				case(3)://"Robot" autonomous selected or no choice made
				{
					//writer.println("Robot will move to auto zone.");
					break;
				}
			}
		}		
    }

    /**
     * Runs the motors for Mecanum Drive and lift along with compressor and claw.
     */
    public void operatorControl() 
    {
       //writer.println("before safety");
       myRobot.setSafetyEnabled(true);
       
  	   //writer.println("before NI Vision");
       //NIVision.Rect rect = new NIVision.Rect(10, 10, 100, 100);
       //writer.println("after NI Vision");
       
       gyro.initGyro();
       gyro.startLiveWindowMode();
       
       //cServer.startAutomaticCapture(); 
       
       while (isOperatorControl() && isEnabled()) 
        {
        	//usbCamera.startCapture();
          	//usbCamera.getImage(frame);
          //NIVision.imaqDrawShapeOnImage(frame, frame, rect,DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
          //CameraServer.getInstance().setImage(frame);
          // //writer.println("drawing on camera");
        	
    	   LiveWindow.run();
    	   LiveWindow.addSensor("Camera", 1, tServer);
    	   
    	   /*frontLeftEnc = frontLeft.getEncVelocity();
    	   frontRightEnc = frontRight.getEncVelocity();
    	   backLeftEnc = backLeft.getEncVelocity();
    	   backRightEnc = backRight.getEncVelocity();*/
    	   
        	////writer.println(frontRightEnc);
        	//arm at rest  
        	if(!(armUpDrive.get()||armUpOp.get()||armDownDrive.get()||armDownOp.get()))
        	{
        		lift.set(0);
        	}
       		//lift up
       		if(armUpDrive.get()||armUpOp.get())        		
       		{
       			lift.set(P51RobotDefine.armUpSpeed);
       			//writer.println("Arm up");
       		}
       		//lift down
       		if(armDownDrive.get()||armDownOp.get())
       		{
       			lift.set(P51RobotDefine.armDownSpeed);
        		//writer.println("Arm down");
        	}
        	
        	//Mecanum drive Switch with buttons 11 & 12
        	if (cartesianDriver.get()||cartesianOp.get())//Cartesian on
        	{
        		polarDrive=false;
        		//writer.println("Polar Drive Off");
        		cartesianDrive=true;
        		//writer.println("Cartesian Drive On");
        	}
        	if (polarDriver.get()||polarOp.get())//Polar on
        	{
        		polarDrive=true;
        		//writer.println("Polar Drive On");
        		cartesianDrive=false;
        		//writer.println("Cartesian Drive Off");
        	}

        	//Mecanum drive
        	if(stick.getX()!=0||stick.getY()!=0||stick.getTwist()!=0)
        	{
        		/*if(stick.getX()>.05)
        			xAxis=Math.pow((stick.getX()-.05)/(.95),3);
        		if(stick.getX()<-.05)
        			xAxis=Math.pow((stick.getX()+.05)/(.95),3);
        	
        		if(stick.getY()>.05)
        			yAxis=Math.pow((stick.getY()-.05)/(.95),3);
        		if(stick.getY()<-.05)
        			yAxis=Math.pow((stick.getY()+.05)/(.95),3);
        	
        		if(stick.getTwist()>.05)
        			twist=Math.pow((stick.getTwist()-.05)/(.95),3);
        		if(stick.getTwist()<-.05)
        			twist=Math.pow((stick.getTwist()+.05)/(.95),3);*/
        		
        		
        		if(polarDrive)
        		{
        			//magnitude = Math.sqrt(Math.pow(xAxis,2)+Math.pow(yAxis,2));
        			//angle = Math.atan2(yAxis,xAxis);
        			myRobot.mecanumDrive_Polar(RobotDriveMath.polarMagnitude(stick.getX(),stick.getY()),
        					RobotDriveMath.polarAngle(stick.getX(),stick.getY()),
        					RobotDriveMath.twistWithDeadband(stick.getTwist()));
        		}
        		if(cartesianDrive)
        			myRobot.mecanumDrive_Cartesian(RobotDriveMath.xWithDeadband(stick.getX()),
        					RobotDriveMath.yWithDeadband(stick.getY()),
        					RobotDriveMath.twistWithDeadband(stick.getTwist()), gyro.getAngle());
        	}
        	
        	frontLeftSpeed = P51Drive.getSpeeds(0, stick.getX(), stick.getY(), stick.getTwist());
        	frontRightSpeed = P51Drive.getSpeeds(1, stick.getX(), stick.getY(), stick.getTwist());
        	backLeftSpeed = P51Drive.getSpeeds(2, stick.getX(), stick.getY(), stick.getTwist());
        	backRightSpeed = P51Drive.getSpeeds(3, stick.getX(), stick.getY(), stick.getTwist());
        	
        	frontLeft.setPID(1, 1, 1);
        	frontRight.setPID(1, 1, 1);
        	backLeft.setPID(1, 1, 1);
        	backRight.setPID(1, 1, 1);
        	
        	//Turn on compressor if more air is needed
            //if(myCompressor.getPressureSwitchValue())
            {
            	//myCompressor.stop();
            	//writer.println("Compressor off");
            }
            //else
            {
            	//@TODO Fix Compressor
            	//myCompressor.start();
            	//writer.println("Compressor on");
            }
            
            
            //Actuate Solenoid for claw
        	if(clawDrive.get()||clawOp.get())
        	{
        		/*@TODO FIX Solenoid */
        		//mySolenoid.set(Value.kForward);
            	//writer.println("claw closed");
        	}
            else
            {/*@TODO FIX Solenoid */
            //	mySolenoid.set(Value.kReverse);
            	//writer.println("claw open");
            }
        	
        	if (deadbandUp.update(deadbandInc.get()))
        	{
        		P51RobotDefine.deadbandExponent += 0.25;
        		//writer.println("Inc to Deadband exp = " + P51RobotDefine.deadbandExponent);
        	}
        	if (deadbandDown.update(deadbandDec.get()))
        	{
        		if (P51RobotDefine.deadbandExponent>1.25)
        			{
        				P51RobotDefine.deadbandExponent -= 0.25;
        				//writer.println("Dec to Deadband exp = " + P51RobotDefine.deadbandExponent);
        			}
        	}
        	
        	Timer.delay(0.005);		// wait for a motor update time
        }
    }
    
    

    /*private ControlMode valueOf(int i) {
		// @TODO Auto-generated method stub
		return null;
	}*/

    
  //Comparator function for sorting particles. Returns true if particle 1 is larger
  		static boolean CompareParticleSizes(ParticleReport particle1, ParticleReport particle2)
  		{
  			//we want descending sort order
  			return particle1.PercentAreaToImageArea > particle2.PercentAreaToImageArea;
  		}

  		/**
  		 * Converts a ratio with ideal value of 1 to a score. The resulting function is piecewise
  		 * linear going from (0,0) to (1,100) to (2,0) and is 0 for all inputs outside the range 0-2
  		 */
  		double ratioToScore(double ratio)
  		{
  			return (Math.max(0, Math.min(100*(1-Math.abs(1-ratio)), 100)));
  		}

  		/**
  		 * Method to score convex hull area. This scores how "complete" the particle is. Particles with large holes will score worse than a filled in shape
  		 */
  		double ConvexHullAreaScore(ParticleReport report)
  		{
  			return ratioToScore((report.Area/report.ConvexHullArea)*1.18);
  		}

  		/**
  		 * Method to score if the particle appears to be a trapezoid. Compares the convex hull (filled in) area to the area of the bounding box.
  		 * The expectation is that the convex hull area is about 95.4% of the bounding box area for an ideal tote.
  		 */
  		double TrapezoidScore(ParticleReport report)
  		{
  			return ratioToScore(report.ConvexHullArea/((report.BoundingRectRight-report.BoundingRectLeft)*(report.BoundingRectBottom-report.BoundingRectTop)*.954));
  		}

  		/**
  		 * Method to score if the aspect ratio of the particle appears to match the long side of a tote.
  		 */
  		double LongSideScore(ParticleReport report)
  		{
  			return ratioToScore(((report.BoundingRectRight-report.BoundingRectLeft)/(report.BoundingRectBottom-report.BoundingRectTop))/LONG_RATIO);
  		}

  		/**
  		 * Method to score if the aspect ratio of the particle appears to match the short side of a tote.
  		 */
  		double ShortSideScore(ParticleReport report){
  			return ratioToScore(((report.BoundingRectRight-report.BoundingRectLeft)/(report.BoundingRectBottom-report.BoundingRectTop))/SHORT_RATIO);
  		}

  		/**
  		 * Computes the estimated distance to a target using the width of the particle in the image. For more information and graphics
  		 * showing the math behind this approach see the Vision Processing section of the ScreenStepsLive documentation.
  		 *
  		 * @param image The image to use for measuring the particle estimated rectangle
  		 * @param report The Particle Analysis Report for the particle
  		 * @param isLong Boolean indicating if the target is believed to be the long side of a tote
  		 * @return The estimated distance to the target in feet.
  		 */
  		double computeDistance (Image image, ParticleReport report, boolean isLong) {
  			double normalizedWidth, targetWidth;
  			NIVision.GetImageSizeResult size;

  			size = NIVision.imaqGetImageSize(image);
  			normalizedWidth = 2*(report.BoundingRectRight - report.BoundingRectLeft)/size.width;
  			targetWidth = isLong ? 26.0 : 16.9;

  			return  targetWidth/(normalizedWidth*12*Math.tan(VIEW_ANGLE*Math.PI/(180*2)));
  		}
    
    
    
	/**
     * Runs during test mode
     */
    public void test() 
    {
    	//writer.printf("Welcome to Test Mode!");
    }
}
