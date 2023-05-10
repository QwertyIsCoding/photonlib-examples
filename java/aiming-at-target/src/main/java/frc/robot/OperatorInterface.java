package org.aa8426.robot2023;

import org.aa8426.lib.OptimizeTurn;
import org.aa8426.robot2023.Constants.DriveConstants;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** 
 * 
 */
public class OperatorInterface {
    
  public static final class OIConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kArmPadControllerPort = 1;

    public static final int kDriverYAxis = 1;
    public static final int kDriverXAxis = 0;
    public static final int kDriverRotAxis = 4;
    public static final int kDriverFieldOrientedButtonIdx = 1;

    public static final double kDeadband = 0.15;
  }

    public final XboxController drivePad = new XboxController(OIConstants.kDriverControllerPort);
    public final XboxController armPad = new XboxController(OIConstants.kArmPadControllerPort);

    private final SlewRateLimiter xLimiter = new SlewRateLimiter(DriveConstants.drivePadMaxAccel);
    private final SlewRateLimiter yLimiter = new SlewRateLimiter(DriveConstants.drivePadMaxAccel);    
    private final SlewRateLimiter xAutoLimiter = new SlewRateLimiter(DriveConstants.drivePadMaxAccel);
    private final SlewRateLimiter yAutoLimiter = new SlewRateLimiter(DriveConstants.drivePadMaxAccel);    
    private final SlewRateLimiter turningLimiter = new SlewRateLimiter(DriveConstants.drivePadMaxAngularAccel);

    private final SlewRateLimiter nudgeLimiter = new SlewRateLimiter(DriveConstants.drivePadNudgeMaxAccel);    
    private final SlewRateLimiter nudgeTurnLimiter = new SlewRateLimiter(DriveConstants.drivePadNudgeMaxAngularAccel);

    private Double requestedHeadingRads = null;
    private RobotContainer robotContainer;

    public enum DriveMode {
      NORMAL,
      NUDGE,
      AUTO, XOUT
    }

    public OperatorInterface(RobotContainer robotContainer) {
      this.robotContainer = robotContainer;        
    }    

    static PIDController pid = new PIDController(0.1, 0.05, 0.001);
    public static double optimizedTurnSpeed(double currentAngle, double desiredAngle) {      
      return 0;      
    }

    private boolean handleDriveTrain(XboxController pad) {
      double xSpeed = -pad.getRawAxis(OIConstants.kDriverYAxis);
      double ySpeed = -pad.getRawAxis(OIConstants.kDriverXAxis);
      double turningSpeed = pad.getRawAxis(OIConstants.kDriverRotAxis);
      xSpeed = Math.abs(xSpeed) > OIConstants.kDeadband ? xSpeed : 0.0;
      ySpeed = Math.abs(ySpeed) > OIConstants.kDeadband ? ySpeed : 0.0;      
      turningSpeed = Math.abs(turningSpeed) > OIConstants.kDeadband ? turningSpeed : 0.0;
      if (turningSpeed != 0.0) {
        requestedHeadingRads = null;
      }
      
      boolean deadSticks = (xSpeed == 0.0) && (ySpeed == 0.0) && (turningSpeed == 0.0);
      
      if (pad.getStartButtonPressed()) {
        robotContainer.swerveSubsystem.zeroHeading();
        robotContainer.swerveSubsystem.gyro.setAngleAdjustment(0);        
      }
            
      if (pad.getYButtonPressed()) {
        requestedHeadingRads = Math.toRadians(0);
      } else if (pad.getAButtonPressed()) {
        requestedHeadingRads = Math.toRadians(180);
      } else if (pad.getXButtonPressed()) {
        requestedHeadingRads = Math.toRadians(90);
      } else if (pad.getBButtonPressed()) {
        requestedHeadingRads = Math.toRadians(270);
      }
      
      DriveMode mode = DriveMode.NORMAL;
      if (pad.getRightBumper()) {
        mode = DriveMode.NUDGE;
      }
      //if (pad.getLeftBumper()) {
      //  mode = DriveMode.XOUT;
      //}

      SmartDashboard.putNumber("xSpeed", xSpeed);
      SmartDashboard.putNumber("ySpeed", ySpeed);
      SmartDashboard.putNumber("turningSpeed", turningSpeed);      

      doDriving(xSpeed, ySpeed, turningSpeed, mode);
      return deadSticks;
    }
    
    public void doDriving(double xSpeed, double ySpeed, double turningSpeed, DriveMode mode) {
      
      turningSpeed = Math.abs(turningSpeed) > OIConstants.kDeadband ? turningSpeed : 0.0;

      if (requestedHeadingRads != null) {        
        turningSpeed = OptimizeTurn.getPowerForTurnToByRads(this.robotContainer.swerveSubsystem.getRotation2d().getRadians(), requestedHeadingRads, 0.1, 1.0);
        if (turningSpeed == 0.0) { // we're at the angle we need to be at
          requestedHeadingRads = null;
        }
      }

      // 3. Make the driving smoother
      double maxDriveSpeed;
      double maxTurnSpeed;
      
      if (mode == DriveMode.NORMAL) {
        maxDriveSpeed = DriveConstants.drivePadMaxMetersPerSec;
        maxTurnSpeed = DriveConstants.drivePadMaxRadsPerSec;        
        xSpeed = xLimiter.calculate(xSpeed) * maxDriveSpeed;
        ySpeed = yLimiter.calculate(ySpeed) * maxDriveSpeed;
        turningSpeed = turningLimiter.calculate(turningSpeed) * maxTurnSpeed;                      
      } else if (mode == DriveMode.AUTO) {        
        maxDriveSpeed = DriveConstants.autoMaxMetersPerSec;
        maxTurnSpeed = DriveConstants.autoMaxRadsPerSec;                
        xSpeed = xAutoLimiter.calculate(xSpeed) * maxDriveSpeed;
        ySpeed = yAutoLimiter.calculate(ySpeed) * maxDriveSpeed;
        turningSpeed = turningLimiter.calculate(turningSpeed) * maxTurnSpeed;              
      } else if (mode == DriveMode.NUDGE) {
        maxDriveSpeed = DriveConstants.drivePadNudgeMaxMetersPerSec;
        maxTurnSpeed = DriveConstants.drivePadNudgeMaxRadsPerSec;
        xSpeed = nudgeLimiter.calculate(xSpeed) * maxDriveSpeed;
        ySpeed = nudgeLimiter.calculate(ySpeed) * maxDriveSpeed;
        turningSpeed = nudgeTurnLimiter.calculate(turningSpeed) * maxTurnSpeed;              
      } else if (mode == DriveMode.XOUT) {
        robotContainer.swerveSubsystem.setWheelsToXOut();
        return;
      }

      /*if (robotOriented) { -- removed, not sure we'd ever use it
          // Relative to robot
          chassisSpeeds = new ChassisSpeeds(xSpeed, ySpeed, turningSpeed);
      } else {*/
      // Relative to field
      ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(
              xSpeed, ySpeed, turningSpeed, robotContainer.swerveSubsystem.getRotation2d());

      // 5. Convert chassis speeds to individual module states
      SwerveModuleState[] moduleStates = DriveConstants.kDriveKinematics.toSwerveModuleStates(chassisSpeeds);

      // 6. Output each module states to wheels
      robotContainer.swerveSubsystem.setModuleStates(moduleStates);
    }

    private boolean userAskingForMoreIntakePower(XboxController pad) {
      return pad.getRightTriggerAxis() > 0.15;
    }

    private void handleIntake(XboxController pad) {
      
      if (pad.getXButton()) { // "Grab Cube" or "Eject Cone"
        // Grabbing a cube effectively turns the cube wheels backwards to reject a cone
        robotContainer.intakeSubsystem.grabCube(userAskingForMoreIntakePower(pad));
      } else if (pad.getBButton()) { // "Eject Cube" or "Grab Cone"        
        // Grabbing a cone effectively turns the cube wheels backwards to reject a cube
        robotContainer.intakeSubsystem.grabCone(userAskingForMoreIntakePower(pad)); 
      } else {
        robotContainer.intakeSubsystem.holdGamePiece();
      }      
    }

    private void handleArm(XboxController pad) {      
      if (pad.getYButton()) {        
        robotContainer.armSubsystem.extend();        
      } else if (pad.getAButton()) {        
        robotContainer.armSubsystem.retract();        
      } else if(pad.getLeftBumper()){
        robotContainer.armSubsystem.setArmPosition(28);     

      } else {
        //robotContainer.armSubsystem.stop();
        robotContainer.armSubsystem.holdCurrentArmPosition();
        //robotContainer.armSubsystem.setArmPosition(3);
      }      
    }

    public void onTeleopPeriodic() {      
      handleDriveTrain(drivePad);            
      handleIntake(armPad);
      // handleIntake(drivePad);
      handleArm(armPad);
      // handleArm(drivePad);
    }
}
