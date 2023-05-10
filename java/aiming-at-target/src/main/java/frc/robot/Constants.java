package org.aa8426.robot2023;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;



/**
 * https://github.com/FRC930/Robot2023/blob/dd877fa1ece4fa8666d86501ff239de0d7efe9e5/src/main/java/frc/robot/subsystems/SwerveModule.java
 * https://github.com/ORF-4450/Sun-Swerve-Drive/blob/c6698fa581c3a0989402fb0f9d24571b4ff85c22/src/main/java/frc/robot/Constants.java
 * https://www.chiefdelphi.com/t/how-use-encoder-offsets-to-automatically-have-swerve-modules-align-themselves/361520/9
 * https://www.reddit.com/r/FRC/comments/xd8qvd/swerve_drive/
 * https://www.swervedrivespecialties.com/products/mk4i-swerve-module
 * https://www.chiefdelphi.com/t/swerve-odometry-has-y-axis-direction-reversed/426363
 * https://www.chiefdelphi.com/t/to-x-lock-or-not-that-is-the-question/423999/6
 */
public final class Constants {
    
    // 150/7:1 
    // The steering gear ratio of the MK4i is 150/7:1.
    // Overall gear ratios for MK4i:
    // L1 - 8.14: 1 (NEO Free Speed: 12ft/s, Falcon: 13.5ft/s)
    // L2 - 6.75: 1 (NEO Free Speed: 14.5ft/s, Falcon: 16.3ft/s)
    // L3 - 6.12: 1 (NEO Free Speed: 16ft/s, Falcon: 18ft/s)
    public static final class ModuleConstants {
        private static final double kWheelDiameterMeters = Units.inchesToMeters(4);
        private static final double kDriveMotorGearRatio = 1 / 6.75;
        //private static final double kTurningMotorGearRatio = 1 / 18.0; // The steering gear ratio of the MK4i is 150/7:1.
        //private static final double kTurningMotorGearRatio = 1 / (150/7);

        @SuppressWarnings("unused")
        private static final double kTurningMotorGearRatio = 1 / (150/7);        
        public static final double kDriveEncoderRot2Meter = kDriveMotorGearRatio * Math.PI * kWheelDiameterMeters;
        //public static final double kTurningEncoderRot2Rad = kTurningMotorGearRatio * 2 * Math.PI;
        public static final double kTurningEncoderRot2Rad = 1;
        public static final double kDriveEncoderRPM2MeterPerSec = kDriveEncoderRot2Meter / 60;
        public static final double kTurningEncoderRPM2RadPerSec = kTurningEncoderRot2Rad / 60;

        public static final double kPTurning = 0.6;
        public static final double kITurning = 0.01;
        public static final double kdTurning = 0.005;
    }

    public static final class DriveConstants {

        public static final double kTrackWidth = Units.inchesToMeters(21);
        // Distance between right and left wheels
        public static final double kWheelBase = Units.inchesToMeters(25.5);
        // Distance between front and back wheels
        
        public static final SwerveDriveKinematics kDriveKinematics = new SwerveDriveKinematics(
            new Translation2d(-kWheelBase / 2, -kTrackWidth / 2), // back left 
            new Translation2d(-kWheelBase / 2, kTrackWidth / 2),
            new Translation2d(kWheelBase / 2, -kTrackWidth / 2), // front right?
            new Translation2d(kWheelBase / 2, kTrackWidth / 2) // front left
            ); 

         
        public static final int kFrontRightDriveMotorPort = 8;
        public static final int kFrontRightTurningMotorPort = 7;        
        public static final int kFrontLeftDriveMotorPort = 5;
        public static final int kFrontLeftTurningMotorPort = 6;
        public static final int kBackRightDriveMotorPort = 10;
        public static final int kBackRightTurningMotorPort = 9;
        public static final int kBackLeftDriveMotorPort = 4;
        public static final int kBackLeftTurningMotorPort = 3;

        public static final double kFrontLeftDriveAbsoluteEncoderOffsetRad = -0.210;
        public static final double kBackLeftDriveAbsoluteEncoderOffsetRad = -0.241;
        public static final double kFrontRightDriveAbsoluteEncoderOffsetRad = -0.502;
        public static final double kBackRightDriveAbsoluteEncoderOffsetRad = -0.443;    

        public static final boolean kFrontLeftTurningEncoderReversed = true;
        public static final boolean kBackLeftTurningEncoderReversed = true;
        public static final boolean kFrontRightTurningEncoderReversed = true;
        public static final boolean kBackRightTurningEncoderReversed = true;

        public static final boolean kFrontLeftDriveEncoderReversed = false;
        public static final boolean kBackLeftDriveEncoderReversed = false;
        public static final boolean kFrontRightDriveEncoderReversed = false;
        public static final boolean kBackRightDriveEncoderReversed = false;

        public static final int kFrontLeftDriveAbsoluteEncoderPort = 2;
        public static final int kBackLeftDriveAbsoluteEncoderPort = 3;
        public static final int kFrontRightDriveAbsoluteEncoderPort = 1;
        public static final int kBackRightDriveAbsoluteEncoderPort = 0;

        public static final boolean kFrontLeftDriveAbsoluteEncoderReversed = false;
        public static final boolean kBackLeftDriveAbsoluteEncoderReversed = false;
        public static final boolean kFrontRightDriveAbsoluteEncoderReversed = false;
        public static final boolean kBackRightDriveAbsoluteEncoderReversed = false;
                
        public static final double absoluteMaxDriveSpeed = 5;

        public static final double drivePadMaxMetersPerSec = 5;
        public static final double drivePadMaxRadsPerSec = 1 * 2 * Math.PI;
        public static final double drivePadMaxAccel = 1.5; // was 3 
        public static final double drivePadMaxAngularAccel = 3;

        public static final double drivePadNudgeMaxMetersPerSec = 1;
        public static final double drivePadNudgeMaxRadsPerSec = Math.PI / 2;
        public static final double drivePadNudgeMaxAccel = .75;
        public static  final double drivePadNudgeMaxAngularAccel = 0.5;
        public static double autoMaxMetersPerSec = 3;
        public static double autoMaxRadsPerSec = 0;

    }
    
}

