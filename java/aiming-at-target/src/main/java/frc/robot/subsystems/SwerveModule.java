package org.aa8426.robot2023.subsystems;

import org.aa8426.robot2023.Constants.DriveConstants;
import org.aa8426.robot2023.Constants.ModuleConstants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.AnalogEncoder;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class SwerveModule {

    public final CANSparkMax driveMotor;
    public final CANSparkMax turningMotor;

    public final RelativeEncoder driveEncoder;
    public final RelativeEncoder turningEncoder;

    public final PIDController turningPidController;

    public final AnalogEncoder absoluteEncoder;
    private final boolean absoluteEncoderReversed;

    @SuppressWarnings("unused")
    private final double absoluteEncoderOffsetRad;
    
    public final String name;
    private Gyro gyro;

    public SwerveModule(String name, int driveMotorId, int turningMotorId, boolean driveMotorReversed, boolean turningMotorReversed,
            int absoluteEncoderId, double absoluteEncoderOffset, boolean absoluteEncoderReversed, Gyro gyro) {                                
        this.name = name;        
        this.gyro = gyro;

        this.absoluteEncoderOffsetRad = absoluteEncoderOffset;
        this.absoluteEncoderReversed = absoluteEncoderReversed;
        
        absoluteEncoder = new AnalogEncoder(absoluteEncoderId);

        driveMotor = new CANSparkMax(driveMotorId, MotorType.kBrushless);
        //REVPhysicsSim.getInstance().addSparkMax(driveMotor, DCMotor.getNEO(1));

        //driveMotor.setSmartCurrentLimit(40);
        driveMotor.setSmartCurrentLimit(120, 60);
        driveMotor.burnFlash();

        turningMotor = new CANSparkMax(turningMotorId, MotorType.kBrushless);
        //REVPhysicsSim.getInstance().addSparkMax(driveMotor, DCMotor.getNEO(1));
        
        driveMotor.setInverted(driveMotorReversed);
        turningMotor.setInverted(turningMotorReversed);

        driveEncoder = driveMotor.getEncoder();        
        turningEncoder = turningMotor.getEncoder();
                
        driveEncoder.setPositionConversionFactor(ModuleConstants.kDriveEncoderRot2Meter);
        driveEncoder.setVelocityConversionFactor(ModuleConstants.kDriveEncoderRPM2MeterPerSec);
        turningEncoder.setPositionConversionFactor(ModuleConstants.kTurningEncoderRot2Rad);
        turningEncoder.setVelocityConversionFactor(ModuleConstants.kTurningEncoderRPM2RadPerSec);

        turningPidController = new PIDController(ModuleConstants.kPTurning, ModuleConstants.kITurning, ModuleConstants.kdTurning);
        turningPidController.enableContinuousInput(-Math.PI, Math.PI);

        resetEncoders();
    }

    private void updateSmartDashboardForMotor(String name, CANSparkMax motor) {
        //SmartDashboard.putNumber(name+"/Encoder/Position", motor.getEncoder().getPosition());
        //SmartDashboard.putNumber(name+"/Encoder/Pos. Cnvrsn Fctr", motor.getEncoder().getPositionConversionFactor()); // has setter
        ////SmartDashboard.putNumber(name+"/Encoder/Velocity", motor.getEncoder().getVelocity());
        ////SmartDashboard.putNumber(name+"/Encoder/Vlcty Cnvrsn Fctr", motor.getEncoder().getVelocityConversionFactor()); // has setter
        //SmartDashboard.putNumber(name+"/Encoder/CountsPerRevolution", motor.getEncoder().getCountsPerRevolution());
        ////SmartDashboard.putNumber(name+"/Encoder/Measurement Period", motor.getEncoder().getMeasurementPeriod()); // has setter
        //SmartDashboard.putBoolean(name+"/Encoder/Inverted", motor.getEncoder().getInverted());

        //SmartDashboard.putNumber(name+"/Absolute Encoder/Position", motor.getAbsoluteEncoder(Type.kDutyCycle).getPosition());
        //SmartDashboard.putNumber(name+"/Absolute Encoder/Pos. Cnvrsn Fctr", motor.getAbsoluteEncoder(Type.kDutyCycle).getPositionConversionFactor()); // has setter
        ////SmartDashboard.putNumber(name+"/Absolute Encoder/Velocity", motor.getAbsoluteEncoder(Type.kDutyCycle).getVelocity());
        ////SmartDashboard.putNumber(name+"/Absolute Encoder/Vlcty Cnvrsn Fctr", motor.getAbsoluteEncoder(Type.kDutyCycle).getVelocityConversionFactor()); // has setter
        //SmartDashboard.putBoolean(name+"/Absolute Encoder/Inverted", motor.getAbsoluteEncoder(Type.kDutyCycle).getInverted());

        //SmartDashboard.putString(name+"/Firmware", motor.getFirmwareString());
        SmartDashboard.putNumber(name+"/Temperature", motor.getMotorTemperature());
        SmartDashboard.putNumber(name+"/Applied Output", motor.getAppliedOutput());
        SmartDashboard.putNumber(name+"/Bus Voltage", motor.getBusVoltage());
        SmartDashboard.putNumber(name+"/Output Current", motor.getOutputCurrent());                
    }

    public void updateSmartDashboard() {    
        updateSmartDashboardForMotor(name+"/Drive Motor", driveMotor);
        updateSmartDashboardForMotor(name+"/Turn Motor", turningMotor);        

        SmartDashboard.putNumber(name+"/Absolute Encoder/Position", absoluteEncoder.getAbsolutePosition());
        SmartDashboard.putNumber(name+"/Absolute Encoder/TurningPos", getTurningPosition());
        SmartDashboard.putNumber(name+"/Absolute Encoder/Offset", this.absoluteEncoderOffsetRad);
        

    }

    private double getDriveMeters() {
        if (RobotBase.isReal()) {          
          return driveEncoder.getPosition();
        } else {
          //return m_simDriveEncoderPosition;
          return 2;
        }   
    }
      
    private double getDriveVelocity() {
        return driveEncoder.getVelocity();
    }

    private double getAbsoluteEncoderRad() {            
        //double angle = Math.toRadians(absoluteEncoder.get()  + absoluteEncoderOffsetRad)* 360);
        double angle = Math.toRadians(absoluteEncoder.get() * 360);
        //SmartDashboard.putNumber(name+"_intang", angle);
        return angle * (absoluteEncoderReversed ? -1.0 : 1.0);
    }

    public void resetEncoders() {
        driveEncoder.setPosition(0);
        turningEncoder.setPosition(getAbsoluteEncoderRad());
    }    

    /**
     * The current absolute position of the wheel. 
     */
    private double getTurningPosition() {
        /*  
          The absolute position encoder returns values between 0 and 1. It should be scaled to Radians, but 
          then shifted to a range of -PI to PI to support continious operation on the pid Controller.

          We specified the continious range in the constructor of the PID controller. I am unsure if
          we set a range of 0 to 2PI if that would mean we could avoid shifting the range.
        */
        double absWithOffset = (absoluteEncoder.getAbsolutePosition() + absoluteEncoderOffsetRad);
        if (absWithOffset < 0) {
            absWithOffset = absWithOffset + 1;
        }
        SmartDashboard.putNumber(name+"/Absolute Encoder/AbsPlusOffset", absWithOffset);
        double turningPosition = (absWithOffset *  (2*Math.PI)) - Math.PI;
        return turningPosition;
    }

    public SwerveModuleState getState() {
        return new SwerveModuleState(getDriveVelocity(), new Rotation2d(getTurningPosition()));
    }

    /**
     * Odometry related, helps with field orientation. 
     */
    private double getHeadingDegrees() {
        if (RobotBase.isReal()) {
            return Math.IEEEremainder(gyro.getAngle(), 360);
        } else {
            return 90; // stuck on 90 for sim
        }
    }

    /**
     * Odometry related, helps with field orientation.
     */
    private Rotation2d getHeadingRotation2d() {
        return Rotation2d.fromDegrees(getHeadingDegrees());
    }

    /**
     * Odometry related, helps with field orientation.
     */
    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(getDriveMeters(), getHeadingRotation2d());
    }

    public void setDesiredState(SwerveModuleState state) {
        if (Math.abs(state.speedMetersPerSecond) < 0.001) {
            stop();
            return;
        }

        // reduces the amount of rotation needed to get the wheel in the direction you want
        SmartDashboard.putNumber(name+"/Absolute Encoder/Target", state.angle.getRadians());
        state = SwerveModuleState.optimize(state, getState().angle);
        double turningPosition = getTurningPosition();
        double speed = turningPidController.calculate(turningPosition, state.angle.getRadians());
        
        driveMotor.set(state.speedMetersPerSecond / DriveConstants.absoluteMaxDriveSpeed);        
        turningMotor.set(speed);
        
        //SmartDashboard.putString("Swerve[" + absoluteEncoder.getChannel() + "] state", state.toString());
    }

    public void stop() {
        driveMotor.set(0);
        turningMotor.set(0);
    }
}