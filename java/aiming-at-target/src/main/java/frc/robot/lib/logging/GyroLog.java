package org.aa8426.lib.logging;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GyroLog {
    
    // https://pdocs.kauailabs.com/navx-mxp/examples/
    static public void update(AHRS ahrs) {
        /* Display 6-axis Processed Angle Data                                      */
        SmartDashboard.putBoolean(  "AHRS/IMU/Connected",        ahrs.isConnected());
        SmartDashboard.putBoolean(  "AHRS/IMU/IsCalibrating",    ahrs.isCalibrating());
        SmartDashboard.putNumber(   "AHRS/IMU/Yaw",              ahrs.getYaw());
        SmartDashboard.putNumber(   "AHRS/IMU/Pitch",            ahrs.getPitch());
        SmartDashboard.putNumber(   "AHRS/IMU/Roll",             ahrs.getRoll());
        
        /* Display tilt-corrected, Magnetometer-based heading (requires             */
        /* magnetometer calibration to be useful)                                   */
        
        SmartDashboard.putNumber(   "AHRS/IMU/CompassHeading",   ahrs.getCompassHeading());
        
        /* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
        SmartDashboard.putNumber(   "AHRS/IMU/FusedHeading",     ahrs.getFusedHeading());

        /* These functions are compatible w/the WPI Gyro Class, providing a simple  */
        /* path for upgrading from the Kit-of-Parts gyro to the navx-MXP            */
        
        SmartDashboard.putNumber(   "AHRS/IMU/TotalYaw",         ahrs.getAngle());
        SmartDashboard.putNumber(   "AHRS/IMU/YawRateDPS",       ahrs.getRate());

        /* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */
        
        SmartDashboard.putNumber(   "AHRS/IMU/Accel_X",          ahrs.getWorldLinearAccelX());
        SmartDashboard.putNumber(   "AHRS/IMU/Accel_Y",          ahrs.getWorldLinearAccelY());
        SmartDashboard.putBoolean(  "AHRS/IMU/IsMoving",         ahrs.isMoving());
        SmartDashboard.putBoolean(  "AHRS/IMU/IsRotating",       ahrs.isRotating());

        /* Display estimates of velocity/displacement.  Note that these values are  */
        /* not expected to be accurate enough for estimating robot position on a    */
        /* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
        /* of these errors due to single (velocity) integration and especially      */
        /* double (displacement) integration.                                       */
        
        SmartDashboard.putNumber(   "AHRS/Velocity_X",           ahrs.getVelocityX());
        SmartDashboard.putNumber(   "AHRS/Velocity_Y",           ahrs.getVelocityY());
        SmartDashboard.putNumber(   "AHRS/Displacement_X",       ahrs.getDisplacementX());
        SmartDashboard.putNumber(   "AHRS/Displacement_Y",       ahrs.getDisplacementY());
        
        /* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
        /* NOTE:  These values are not normally necessary, but are made available   */
        /* for advanced users.  Before using this data, please consider whether     */
        /* the processed data (see above) will suit your needs.                     */
        
        SmartDashboard.putNumber(   "AHRS/RawGyro_X",            ahrs.getRawGyroX());
        SmartDashboard.putNumber(   "AHRS/RawGyro_Y",            ahrs.getRawGyroY());
        SmartDashboard.putNumber(   "AHRS/RawGyro_Z",            ahrs.getRawGyroZ());
        SmartDashboard.putNumber(   "AHRS/RawAccel_X",           ahrs.getRawAccelX());
        SmartDashboard.putNumber(   "AHRS/RawAccel_Y",           ahrs.getRawAccelY());
        SmartDashboard.putNumber(   "AHRS/RawAccel_Z",           ahrs.getRawAccelZ());
        SmartDashboard.putNumber(   "AHRS/RawMag_X",             ahrs.getRawMagX());
        SmartDashboard.putNumber(   "AHRS/RawMag_Y",             ahrs.getRawMagY());
        SmartDashboard.putNumber(   "AHRS/RawMag_Z",             ahrs.getRawMagZ());
        SmartDashboard.putNumber(   "AHRS/IMU_Temp_C",           ahrs.getTempC());
        
        /* Omnimount Yaw Axis Information                                           */
        /* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
        AHRS.BoardYawAxis yaw_axis = ahrs.getBoardYawAxis();
        SmartDashboard.putString(   "AHRS/YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
        SmartDashboard.putNumber(   "AHRS/YawAxis",              yaw_axis.board_axis.getValue() );
        
        /* Sensor Board Information                                                 */
        SmartDashboard.putString(   "AHRS/FirmwareVersion",      ahrs.getFirmwareVersion());
        
        /* Quaternion Data                                                          */
        /* Quaternions are fascinating, and are the most compact representation of  */
        /* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
        /* from the Quaternions.  If interested in motion processing, knowledge of  */
        /* Quaternions is highly recommended.                                       */
        SmartDashboard.putNumber(   "AHRS/Quaternion/W",          ahrs.getQuaternionW());
        SmartDashboard.putNumber(   "AHRS/Quaternion/X",          ahrs.getQuaternionX());
        SmartDashboard.putNumber(   "AHRS/Quaternion/Y",          ahrs.getQuaternionY());
        SmartDashboard.putNumber(   "AHRS/Quaternion/Z",          ahrs.getQuaternionZ());
        
        /* Connectivity Debugging Support                                           */
        SmartDashboard.putNumber(   "AHRS/IMU_Byte_Count",       ahrs.getByteCount());
        SmartDashboard.putNumber(   "AHRS/IMU_Update_Count",     ahrs.getUpdateCount());
   
    }
}
