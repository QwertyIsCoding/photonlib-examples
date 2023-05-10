package org.aa8426.robot2023.commands;

import org.aa8426.lib.OptimizeTurn;
import org.aa8426.lib.TrackChange;
import org.aa8426.robot2023.OperatorInterface;
import org.aa8426.robot2023.RobotContainer;
import org.aa8426.robot2023.OperatorInterface.DriveMode;

import com.pathplanner.lib.PathConstraints;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public class CmdFactory {    
    // Time to throw game piece in auto       
    public static final double AUTO_THROW_TIME_S = 0.375;

    private RobotContainer robotContainer;
    private OperatorInterface operatorInterface;

    public CmdFactory(RobotContainer robotContainer, OperatorInterface operatorInterface) {
        this.robotContainer = robotContainer;
        this.operatorInterface = operatorInterface;   
    }

    public Command grabCube() {
        return new TimedCommand(1.5, () -> {
            this.robotContainer.intakeSubsystem.grabCube(false);
        });       
    }

    public Command drive(double xSpeed, double ySpeed, double turningSpeed) {
        return new InstantCommand(() -> {
            this.operatorInterface.doDriving(xSpeed, ySpeed, turningSpeed, DriveMode.AUTO);
        }).repeatedly();
    }    
    
    public Command driveTime(double speed, double seconds) {
        return drive(speed, 0, 0).withTimeout(seconds);
    }

    public Command driveUntilPitchGreaterThan(double speed, double degree) {
        return drive(speed, 0, 0).until(() -> {
            //System.out.println(this.robotContainer.swerveSubsystem.gyro.getPitch());
            return this.robotContainer.swerveSubsystem.gyro.getPitch() > degree;
        }).withTimeout(6);
    }

    public Command driveUntilPitchLessThan(double speed, double degree) {
        return drive(speed, 0, 0).until(() -> {
            //System.out.println(this.robotContainer.swerveSubsystem.gyro.getPitch());
            return this.robotContainer.swerveSubsystem.gyro.getPitch() < degree;
        }).withTimeout(6);
    }

    public Command balance(double realPitchCenter) {
        TrackChange tc = new TrackChange();
        return new InstantCommand(() -> {                        
            double pitch = this.robotContainer.swerveSubsystem.gyro.getPitch() - realPitchCenter;
            tc.addVal(Timer.getFPGATimestamp(), pitch);            
            // raw rate is the rate uncorrected to degrees/sec -- we just assume that we're getting a sample
            // rate pretty close to we've only observed the graph that way
            double rate = tc.getRawRate();
            double power;
            if (tc.isRateValid() && (rate > 1.5)) { 
                power = OptimizeTurn.calcProportionalPower(rate, 0, 1, 10, 0.05, 0.14);
            } else { // if we're tilted, go up the hill until we start falling, or stay still if we're balanced
                power = OptimizeTurn.calcProportionalPower(pitch, 0, 1, 20, 0.1, 0.19);
            }
            this.operatorInterface.doDriving(power, 0, 0, DriveMode.AUTO); // stop hopefully
        }).repeatedly().withTimeout(15);
    }

    public Command stopDriving(double howLong) {
        return new TimedCommand(howLong, () -> {
            //System.out.println("STOPPPED");
            this.operatorInterface.doDriving(0, 0, 0, DriveMode.AUTO);        
        });
    }

    public Command ejectCube() {
        return new TimedCommand(AUTO_THROW_TIME_S, () -> {
        this.robotContainer.intakeSubsystem.ejectCube();
        }).andThen(new InstantCommand(() -> {
            this.robotContainer.intakeSubsystem.stop();
        }));
    }

    public Command grabCone() { 
        return new TimedCommand(0.5, () -> {
            this.robotContainer.intakeSubsystem.grabCone(false);
        });
    }
    
    public Command ejectCone() { 
        return new TimedCommand(AUTO_THROW_TIME_S, () -> {
            this.robotContainer.intakeSubsystem.ejectCone();
        }).andThen(new InstantCommand(() -> {
            this.robotContainer.intakeSubsystem.stop();
        }));
    }

    public Command extendArm(double autonArmExtendTime) { 
        return new TimedCommand(autonArmExtendTime, () -> {
            this.robotContainer.armSubsystem.extend();
        });
    }

    public Command oldRetractArm() { 
        return new TimedCommand(1.0, () -> {
            this.robotContainer.armSubsystem.retract();
        });     
    }

    public Command retractArm() { // faster?
        return new InstantCommand(() -> {this.robotContainer.armSubsystem.retractFast(); });
              //.repeatedly()              
              //.until(() -> {return this.robotContainer.armSubsystem.isFullyRetracted(); })              
              //.withTimeout(1.0);
    }
    
    public Command stopArm() { 
        return new TimedCommand(0.25, () -> {
            this.robotContainer.armSubsystem.stop();
        });
    }

    public Command xOut() { 
        return new TimedCommand(0.25, () -> {
        this.robotContainer.swerveSubsystem.setWheelsToXOut();
        }); 
    }

    public Command getSetAngleAdjustment(double angleAdjustmentInDegrees) {
        return new InstantCommand(() -> {
            this.robotContainer.swerveSubsystem.setCurrentRobotFieldOrientation(angleAdjustmentInDegrees);
        });
    }

    public Command followPath(String name) {
        PathConstraints pathConstraints = new PathConstraints(3, 1.5);                   
        return robotContainer.swerveSubsystem.followTrajectoryCommand(name, pathConstraints);
    }

}
