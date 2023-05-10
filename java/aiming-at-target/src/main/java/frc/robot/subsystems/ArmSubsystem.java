package org.aa8426.robot2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ArmSubsystem extends SubsystemBase {
    /**
     * How many amps the arm motor can use.
     */
    public static final int ARM_CURRENT_LIMIT_AMP = 30;

    /**
     * Percent output to run the arm up/down at
     */
    public static final double ARM_OUTPUT_POWER = 0.55;
    public static final double ARM_HOLDING_POWER = 0.02;
    public static final int kArmMotorPort = 12;

    public static final int UP = 0;
    public static final int DOWN = 1;
    public int lastAction = DOWN;

    public final CANSparkMax armMotorController = new CANSparkMax(kArmMotorPort, MotorType.kBrushless);    
    
    public double startPosition;    
    
    private double armPositionGain;
    private double maxArmPositionPower;
    private double retractedArmPosition;
    private double retractArmPower;

    public ArmSubsystem() {        
        armPositionGain = 0.1;
        maxArmPositionPower = 0.75;
        retractedArmPosition = 0.5;

        retractArmPower = 0.55;

        setIdleMode(IdleMode.kBrake);
        armMotorController.setSmartCurrentLimit(ARM_CURRENT_LIMIT_AMP);        
        armMotorController.burnFlash();
        startPosition = getArmPosition();        
    }

    /**
     * Sets the arm power as a matter of percentage of the maximum power of the
     * motor.
     */
    private void setArmPower(double percent) {
        armMotorController.set(percent);
        SmartDashboard.putNumber("Arm Power (%)", percent);
    }

    public void setArmPosition(double location) {       
        armMotorController.getPIDController().setOutputRange(-maxArmPositionPower, maxArmPositionPower);
        armMotorController.getPIDController().setP(armPositionGain);
        armMotorController.getPIDController().setI(0);
        armMotorController.getPIDController().setD(0);
        armMotorController.getPIDController().setFF(0);
        armMotorController.getPIDController().setReference(location, ControlType.kPosition);
    }

    public void holdCurrentArmPosition() {       
        armMotorController.getPIDController().setOutputRange(-maxArmPositionPower, maxArmPositionPower);
        armMotorController.getPIDController().setP(armPositionGain);
        armMotorController.getPIDController().setI(0);
        armMotorController.getPIDController().setD(0);
        armMotorController.getPIDController().setFF(0);
        armMotorController.getPIDController().setReference(getArmPosition(), ControlType.kPosition);
    }


    
    public void holdCurrentArmPositionB() {       
        armMotorController.set(-0.04);
       
    }

    public double getArmPosition() {
        return armMotorController.getEncoder().getPosition();        
    }

    private void setIdleMode(IdleMode idleMode) {
        armMotorController.setIdleMode(idleMode);        
    }

    public void stop() {
        if (lastAction == UP) {
            setArmPower(ARM_HOLDING_POWER);
        } else {
            setArmPower(0);
        }
    }

    public void extend() {
        lastAction = UP;
        setArmPower(ARM_OUTPUT_POWER);
    }

    public boolean isFullyRetracted() {
        return getArmPosition() < retractedArmPosition;
    }

    public boolean isFullyRetractedAuto() {
        return getArmPosition() < -2;
    }

    public void retract() {
        lastAction = DOWN;
        if (isFullyRetracted()) {
            holdCurrentArmPosition();
        } else {
            setArmPower(-retractArmPower);
        }
    }

    public void retractFast() {
        lastAction = DOWN;
        if (isFullyRetracted()) {
            holdCurrentArmPosition();
        } else {
            //setArmPower(-0.5);
            setArmPosition(retractedArmPosition);
        }
    }


    public void updateSmartDashboard() {
        SmartDashboard.putNumber("Arm/motor current (amps)", armMotorController.getOutputCurrent());
        SmartDashboard.putNumber("Arm/motor temperature (C)", armMotorController.getMotorTemperature());
    }

    public void disable() {
        armMotorController.setIdleMode(IdleMode.kCoast);
        armMotorController.set(0);
    }



}
