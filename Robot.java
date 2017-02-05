package org.usfirst.frc.team20.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot  implements PIDOutput {
	//drive pid values for turning
	    
		RobotDrive MyDrive;
	    PIDController turnController;   
	    double rotateToAngleRate;
	    SpeedControllers speedController;
	    int state;
	    int auto;
	    AHRS hrs;
	    Utils util;
	    float angleFromCamera = 0.00f;
	  

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		// auto mode call
		auto = AutoModes.AUTO_MODE_1;
		// **********************
		
		hrs = new AHRS(SerialPort.Port.kMXP);
		util = new Utils();
		speedController = new SpeedControllers();
		state = States.nothing;
		speedController.setDriveTalons();
		MyDrive = new RobotDrive(speedController.rightMaster,speedController.leftMaster);
			
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
	     hrs.reset();
	     
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		   switch (auto) {
       		case AutoModes.AUTO_MODE_1 :
       			run_Auto01();
       		  
       		break;
       	default :
       		System.out.println("AUTO MODE = " + auto);
       }

	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		
		
	}
	
// user methods begin
	private void setTurnController(double turnAngle) {
	    turnController = new PIDController(PidTurnValues.kP, PidTurnValues.kI, PidTurnValues.kD, PidTurnValues.kF, hrs,this);
	    turnController.setInputRange(-180.0f,  180.0f);
	    turnController.setOutputRange(-1.0, 1.0);
	    turnController.setAbsoluteTolerance(PidTurnValues.kToleranceDegrees);
	    turnController.setContinuous(true);
	    turnController.setSetpoint(turnAngle);
	    turnController.enable();
	}
	
	private boolean TurnAngle(float cameraAngle )
	{
		double angle = cameraAngle;
		boolean doneTurning = false;
		double	currentRotationRate = rotateToAngleRate;
			 try {
				 MyDrive.arcadeDrive(0.0, currentRotationRate);
				 System.out.println(hrs.getAngle());
			 	} catch ( RuntimeException ex ) {
			 		DriverStation.reportError("Error communicating with drive system: " + ex.getMessage(), true);
			 	}
			 if (Math.abs(currentRotationRate) < .23 && Math.abs(angle - hrs.getAngle()) < .3 ){
					currentRotationRate = 0;
					MyDrive.arcadeDrive(0.0, currentRotationRate);
					turnController.disable();
					doneTurning = true;	
				}
			 return doneTurning;
	}
	
	public boolean driveStraight(double speed, double inches,double multiplier){
		boolean doneDriving = false;
		System.out.println("Speed " + speed); // .5
		System.out.println("multiplier " + multiplier); // 6.5
		System.out.println("distance" + inches); // 80
		
		if(speedController.leftMaster.getEncPosition()/1024*Math.PI*4 > (inches*multiplier)){
			System.out.println("Done");
			speedController.leftMaster.set(0);
			speedController.rightMaster.set(0);
			doneDriving = true;
			//leftMaster.set(0);
			//rightMaster.set(0);
		}
		else{
			speedController.rightMaster.set(speed);
			speedController.leftMaster.set(-speed);
			System.out.println(speedController.leftMaster.getEncPosition());
		}
		
		return doneDriving;
	}	
	
	
	@Override
	public void pidWrite(double output) {
		 rotateToAngleRate = output;
	}	
	
// user methods end
	
	// Auto Modes

	// turn an angle
	// drive straight
	
public void run_Auto01()
{
	if (state == States.nothing){
		angleFromCamera = util.getCameraAngle();
	    setTurnController(angleFromCamera);
	    state = States.TURN_ANGLE;
	}
	if (state == States.TURN_ANGLE){
		if(TurnAngle(angleFromCamera )){
			state = States.GO_DISTANCE;
			//set encoder to zero so it can calculate distance
			speedController.leftMaster.setEncPosition(0);
		}
	}
	if (state == States.GO_DISTANCE) {
		if(driveStraight(.5, 80.00,6.5))
			state = States.DONE;
	}
	
	
	
}
	
}

