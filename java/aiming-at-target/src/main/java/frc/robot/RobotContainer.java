package org.aa8426.robot2023;

import org.aa8426.robot2023.subsystems.ArmSubsystem;
import org.aa8426.robot2023.subsystems.IntakeSubsystem;
import org.aa8426.robot2023.subsystems.SwerveSubsystem;

import edu.wpi.first.wpilibj.PowerDistribution;

/** 
 * Contains all the subsystems of the robot, basically the hardware.
 * 
 * @TODO Make it possible to configure and save settings of the robot hardware via Shuffleboard and save/load them as a set.
 * 
 * Useful for passing into globally acting classes like the OperatorInterface, RobotState, and the Shuffleboard.
 * 
 */
public class RobotContainer {
 
    public SwerveSubsystem swerveSubsystem;    
    public ArmSubsystem armSubsystem;
    public IntakeSubsystem intakeSubsystem;      
    public PowerDistribution pdp = new PowerDistribution();
    //public UsbCamera cameraServer = CameraServer.startAutomaticCapture();

    public RobotContainer() {
        this.swerveSubsystem = new SwerveSubsystem();
        this.armSubsystem = new ArmSubsystem();
        this.intakeSubsystem = new IntakeSubsystem();
        
        //CameraServer.startAutomaticCapture();
    }

    
}
