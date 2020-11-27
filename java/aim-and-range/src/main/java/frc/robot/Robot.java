/**
 * Copyright (C) 2018-2020 Photon Vision.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonUtils;

import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.util.Units;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
   // Constants such as camera and target height stored. Change per robot and goal!
   final double CAMERA_HEIGHT_METERS = Units.inchesToMeters(24);
   final double TARGET_HEIGHT_METERS = Units.feetToMeters(5);
   // Angle between horizontal and the camera.
   final double CAMERA_PITCH_RADIANS = Units.degreesToRadians(0);

   // How far from the target we want to be
   final double GOAL_RANGE_METERS = Units.feetToMeters(3);

   // Change this to match the name of your camera
   PhotonCamera camera = new PhotonCamera("photonvision");

   // PID constants should be tuned per robot
   final double LINEAR_P = 0.1;
   final double LINEAR_D = 0.0;
   PIDController forwardController = new PIDController(LINEAR_P, 0, LINEAR_D);

   final double ANGULAR_P = 0.1;
   final double ANGULAR_D = 0.0;
   PIDController turnController = new PIDController(ANGULAR_P, 0, ANGULAR_D);

   XboxController xboxController = new XboxController(0);

   // Drive motors
   PWMVictorSPX leftMotor = new PWMVictorSPX(0);
   PWMVictorSPX rightMotor = new PWMVictorSPX(1);
   DifferentialDrive drive = new DifferentialDrive(leftMotor, rightMotor);

   @Override
   public void teleopPeriodic() {
      double forwardSpeed;
      double rotationSpeed;

      if (xboxController.getAButton()) {
         // Vision-alignment mode
         // Query the latest result from PhotonVision
         var result = camera.getLatestResult();

         if (result.hasTargets()) {
            // First calculate range
            double range = PhotonUtils.calculateDistanceToTargetMeters(CAMERA_HEIGHT_METERS, TARGET_HEIGHT_METERS,
                  CAMERA_PITCH_RADIANS, result.getBestTarget().getPitch());

            // Use this range as the measurement we give to the PID controller.
            forwardSpeed = forwardController.calculate(range, GOAL_RANGE_METERS);

            // Also calculate angular power
            rotationSpeed = turnController.calculate(result.getBestTarget().getYaw(), 0);
         } else {
            // If we have no targets, stay still.
            forwardSpeed = 0;
            rotationSpeed = 0;
         }
      } else {
         // Manual Driver Mode
         forwardSpeed = xboxController.getY(GenericHID.Hand.kRight);
         rotationSpeed = xboxController.getX(GenericHID.Hand.kLeft);
      }

      // Use our forward/turn speeds to control the drivetrain
      drive.arcadeDrive(forwardSpeed, rotationSpeed);
   }
}
