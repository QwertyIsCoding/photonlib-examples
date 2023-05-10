package org.aa8426.robot2023.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
    /**
     * How many amps the intake can use while picking up
     */
    public static final int INTAKE_CURRENT_LIMIT_AMPS = 35;

    /**
     * How many amps the intake can use while holding
     */
    public static final int INTAKE_HOLD_CURRENT_LIMIT_AMPS = 5;

    /**
     * Percent output for intaking
     */
    public static final double INTAKE_OUTPUT_POWER = 0.25;

    public static final double INTAKE_EXTRA_OUTPUT_POWER = 0.15;

    /**
     * Percent output for holding
     */
    public static final double INTAKE_HOLD_POWER = 0.1;

    public static final int kIntakeMotorPort = 11;

    public static final boolean kIntakeMotorReversed = true;

    final int CONE = 1;
    final int CUBE = 2;
    final int NOTHING = 3;
    int lastGamePiece = NOTHING;

    public final CANSparkMax intakeMotorController = new CANSparkMax(kIntakeMotorPort, MotorType.kBrushless);

    public IntakeSubsystem() {
        intakeMotorController.setIdleMode(IdleMode.kBrake);
        intakeMotorController.setSmartCurrentLimit(40, 40);
        intakeMotorController.burnFlash();
    }

    /**
     * Sets the intake power as a matter of percentage of the maximum power of the
     * motor.
     */
    private void setIntakePower(double percent, int amps) {
        intakeMotorController.set(percent);
        if (amps == 0) {
            // do nothing, we don't want to set to zero
            SmartDashboard.putNumber("Intake Amp Limit", amps);
        } else {
            intakeMotorController.setSmartCurrentLimit(amps);
            SmartDashboard.putNumber("Intake Amp Limit", amps);
        }
        SmartDashboard.putNumber("Intake Power (%)", percent);
    }

    public void stop() {
        setIntakePower(0, 0);
    }

    public void updateSmartDashboardFor() {
    }

    public void updateSmartDashboard() {
        SmartDashboard.putNumber("Intake/motor current (amps)", intakeMotorController.getOutputCurrent());
        SmartDashboard.putNumber("Intake/motor temperature (C)", intakeMotorController.getMotorTemperature());
    }

    public double getPower(boolean highPower) {
        if (highPower) {
            return INTAKE_OUTPUT_POWER + INTAKE_EXTRA_OUTPUT_POWER;
        } else {
            return INTAKE_OUTPUT_POWER;
        }
    }

    public void grabCube(boolean highPower) {
        setIntakePower(-getPower(highPower), INTAKE_CURRENT_LIMIT_AMPS);
        //lastGamePiece = CONE;
    }

    public void ejectCube() {
        grabCone(false); // same as
    }
    public void grabCone(boolean highPower) {        
        setIntakePower(getPower(highPower), INTAKE_CURRENT_LIMIT_AMPS);
        //lastGamePiece = CUBE;
    }

    public void ejectCone() {
        grabCube(false);
    }

    public void holdGamePiece() {
        /*if (lastGamePiece == CUBE) {
            setIntakePower(INTAKE_HOLD_POWER, INTAKE_HOLD_CURRENT_LIMIT_AMPS);
        } else if (lastGamePiece == CONE) {
            setIntakePower(INTAKE_HOLD_POWER, INTAKE_HOLD_CURRENT_LIMIT_AMPS);
        } else {*/
            setIntakePower(0, 0);
        //}
    }

}
