package org.aa8426.robot2023.subsystems;

import org.aa8426.robot2023.Constants;
import org.aa8426.robot2023.Constants.DriveConstants;

import com.kauailabs.navx.frc.AHRS;
import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

// https://github.com/Hemlock5712/swerve-test/blob/main/src/main/java/frc/robot/commands/DriveToPoint.java
public class SwerveSubsystem extends SubsystemBase {
    public final AHRS gyro = new AHRS(SPI.Port.kMXP);

    public final SwerveModule frontLeft = new SwerveModule("frontLeft",
            DriveConstants.kFrontLeftDriveMotorPort,
            DriveConstants.kFrontLeftTurningMotorPort,
            DriveConstants.kFrontLeftDriveEncoderReversed,
            DriveConstants.kFrontLeftTurningEncoderReversed,
            DriveConstants.kFrontLeftDriveAbsoluteEncoderPort,
            DriveConstants.kFrontLeftDriveAbsoluteEncoderOffsetRad,
            DriveConstants.kFrontLeftDriveAbsoluteEncoderReversed,
            gyro);

    public final SwerveModule frontRight = new SwerveModule("frontRight",
            DriveConstants.kFrontRightDriveMotorPort,
            DriveConstants.kFrontRightTurningMotorPort,
            DriveConstants.kFrontRightDriveEncoderReversed,
            DriveConstants.kFrontRightTurningEncoderReversed,
            DriveConstants.kFrontRightDriveAbsoluteEncoderPort,
            DriveConstants.kFrontRightDriveAbsoluteEncoderOffsetRad,
            DriveConstants.kFrontRightDriveAbsoluteEncoderReversed,
            gyro);

    public final SwerveModule backLeft = new SwerveModule("backLeft",
            DriveConstants.kBackLeftDriveMotorPort,
            DriveConstants.kBackLeftTurningMotorPort,
            DriveConstants.kBackLeftDriveEncoderReversed,
            DriveConstants.kBackLeftTurningEncoderReversed,
            DriveConstants.kBackLeftDriveAbsoluteEncoderPort,
            DriveConstants.kBackLeftDriveAbsoluteEncoderOffsetRad,
            DriveConstants.kBackLeftDriveAbsoluteEncoderReversed,
            gyro);

    public final SwerveModule backRight = new SwerveModule("backRight",
            DriveConstants.kBackRightDriveMotorPort,
            DriveConstants.kBackRightTurningMotorPort,
            DriveConstants.kBackRightDriveEncoderReversed,
            DriveConstants.kBackRightTurningEncoderReversed,
            DriveConstants.kBackRightDriveAbsoluteEncoderPort,
            DriveConstants.kBackRightDriveAbsoluteEncoderOffsetRad,
            DriveConstants.kBackRightDriveAbsoluteEncoderReversed,
            gyro);

    public final SwerveDriveOdometry odometer = new SwerveDriveOdometry(DriveConstants.kDriveKinematics, new Rotation2d(0), getModulePositions());

    public boolean odometryInitialized = false;

    public SwerveSubsystem() {                
        zeroHeading();
    }

    /** 
     * Whatever direction the robot is facing on the field will be considered "north" or "up" using field orientation after calling this.
     */
    public void zeroHeading() {        
        gyro.reset();
    }

    /**
     * Return the heading of the robot from the gyroscope. Used for field oriented robot navigation.
     */
    public Rotation2d getRotation2d() {
        double heading = -Math.IEEEremainder(gyro.getAngle(), 360);        
        return Rotation2d.fromDegrees(heading);
    }

    public Pose2d getPose() {        
        return odometer.getPoseMeters();
    }

    public void resetOdometry(Pose2d pose) {
        odometryInitialized = true;
        zeroHeading();
        setCurrentRobotFieldOrientation(180);
        if (pose != null) {
            odometer.resetPosition(getRotation2d(), getModulePositions(), pose);
        }
    }

    public SwerveModuleState[] getModuleStates() {
    return new SwerveModuleState[] {
            frontLeft.getState(),
            frontRight.getState(),
            backLeft.getState(),    
            backRight.getState()            
    };
  }  

  public SwerveModulePosition[] getModulePositions() {        
    return new SwerveModulePosition[] {
            frontLeft.getPosition(),
            frontRight.getPosition(),
            backLeft.getPosition(),
            backRight.getPosition()            
    };
  }

    @Override
    public void periodic() {
        // Part of 2022->2023 migration that needs to be done -- not required yet        
        odometer.update(getRotation2d(), getModulePositions());
        //SmartDashboard.putString("Robot Location", getPose().getTranslation().toString());
    }

    public void stopModules() {
        frontLeft.stop();
        frontRight.stop();
        backLeft.stop();
        backRight.stop();
    }

    public void resetEncoders() {
        frontLeft.resetEncoders();
        frontRight.resetEncoders();
        backLeft.resetEncoders();
        backRight.resetEncoders();
    }

    public void setModuleStates(SwerveModuleState[] desiredStates) {
        //SwerveDriveKinematics.normalizeWheelSpeeds(desiredStates, DriveConstants.absoluteMaxDriveSpeed);        
        SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, DriveConstants.absoluteMaxDriveSpeed);
        frontLeft.setDesiredState(desiredStates[0]);
        frontRight.setDesiredState(desiredStates[1]);
        backLeft.setDesiredState(desiredStates[2]);
        backRight.setDesiredState(desiredStates[3]);
    }

    public void updateSmartDashboard() {
        frontLeft.updateSmartDashboard();
        frontRight.updateSmartDashboard();
        backLeft.updateSmartDashboard();
        backRight.updateSmartDashboard();

        SmartDashboard.putNumber("gyro/r2d_deg", gyro.getRotation2d().getDegrees());
        SmartDashboard.putNumber("gyro/r2d_rot", gyro.getRotation2d().getRotations());
        SmartDashboard.putNumber("gyro/raw angle", gyro.getAngle());
    }
    
    public void setWheelsToXOut() {
        setModuleStates(new SwerveModuleState[] {
            new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(45)),
            new SwerveModuleState(0, Rotation2d.fromDegrees(-45))
        });
    }

    public void setCurrentRobotFieldOrientation(double angleAdjustment) {
        gyro.reset();
        gyro.setAngleAdjustment(angleAdjustment);        
    }

    // https://github.com/mjansen4857/pathplanner/wiki/PathPlannerLib:-Java-Usage
    public Command followTrajectoryCommand(String pathName, PathConstraints pathConstraints) {        
        PathPlannerTrajectory traj = PathPlanner.loadPath(pathName, pathConstraints);
        return new SequentialCommandGroup(
             new InstantCommand(() -> {
               // Reset odometry for the first path you run during auto               
                this.resetOdometry(traj.getInitialHolonomicPose());               
             }),
             new PPSwerveControllerCommand(
                 traj, 
                 this::getPose, // Pose supplier
                 Constants.DriveConstants.kDriveKinematics, // SwerveDriveKinematics
                 new PIDController(0, 0, 0), // X controller. Tune these values for your robot. Leaving them 0 will only use feedforwards.
                 new PIDController(0, 0, 0), // Y controller (usually the same values as X controller)
                 new PIDController(0, 0, 0), // Rotation controller. Tune these values for your robot. Leaving them 0 will only use feedforwards.
                 this::setModuleStates, // Module states consumer
                 true, // Should the path be automatically mirrored depending on alliance color. Optional, defaults to true
                 this // Requires this drive subsystem
             )
         );
     }
}