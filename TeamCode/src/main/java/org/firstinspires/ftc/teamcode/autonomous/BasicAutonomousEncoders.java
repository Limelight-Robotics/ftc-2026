package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.classes.DefaultRobot;

/**
 * Encoder-based autonomous: strafes left 2 feet, then drives backward 4 feet.
 *
 * Uses RUN_TO_POSITION mode for accurate distance control.
 * Update TICKS_PER_REV and WHEEL_DIAMETER_INCHES to match your motor/wheel hardware.
 */
@Autonomous(name = "Basic Autonomous (Encoders)", group = "1 - Team Code")
public class BasicAutonomousEncoders extends LinearOpMode {
  private final DefaultRobot robot = new DefaultRobot();

  // goBILDA Yellow Jacket 5202/3/4 series, 19.2:1 ratio, 312 RPM
  private static final double TICKS_PER_REV = 537.7;
  // goBILDA GripForce mecanum wheels (104mm diameter)
  private static final double WHEEL_DIAMETER_INCHES = 4.094;

  private static final double TICKS_PER_INCH = TICKS_PER_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

  // Mecanum strafing is less efficient than forward/back driving.
  // This multiplier compensates so the requested distance is accurate.
  // Tune on the real robot (typical range 1.1 – 1.5).
  private static final double STRAFE_CORRECTION = 1.41;

  private static final double DRIVE_POWER = 0.5;

  @Override
  public void runOpMode() {
    robot.init(hardwareMap);

    telemetry.addData("Status", "Initialized - Ready to run");
    telemetry.addData("Plan", "Strafe left 2 ft, then drive backward 4 ft");
    telemetry.update();

    waitForStart();

    // Step 1: Strafe left 2 feet (24 inches)
    strafeInches(-24, DRIVE_POWER);

    // Step 2: Drive backward 4 feet (48 inches)
    driveInches(-48, DRIVE_POWER);

    robot.stopMovement();
    telemetry.addData("Status", "Autonomous complete");
    telemetry.update();
  }

  /**
   * Drive forward/backward a given distance in inches.
   * Positive = forward, negative = backward.
   */
  private void driveInches(double inches, double power) {
    int ticks = (int) (inches * TICKS_PER_INCH);

    robot.resetEncoders();
    robot.setTargetPositions(ticks, ticks, ticks, ticks);
    robot.setAllMotorPower(power);

    telemetry.addData("Step", "Driving %.1f inches (%d ticks)", inches, ticks);
    telemetry.update();

    while (opModeIsActive() && robot.areMotorsBusy()) {
      sleep(50);
    }

    robot.stopMovement();
    robot.setRunUsingEncoders();
  }

  /**
   * Strafe left/right a given distance in inches.
   * Negative = left, positive = right.
   *
   * Mecanum strafe wheel math (for the directions set in DriveDirectionPresets):
   *   Left strafe:  FL backward, FR forward, BL forward, BR backward
   *   Right strafe: FL forward, FR backward, BL backward, BR forward
   */
  private void strafeInches(double inches, double power) {
    int ticks = (int) (inches * TICKS_PER_INCH * STRAFE_CORRECTION);

    // Strafe: FL and BR get +ticks, FR and BL get -ticks
    robot.resetEncoders();
    robot.setTargetPositions(ticks, -ticks, -ticks, ticks);
    robot.setAllMotorPower(power);

    telemetry.addData("Step", "Strafing %.1f inches (%d ticks)", inches, ticks);
    telemetry.update();

    while (opModeIsActive() && robot.areMotorsBusy()) {
      sleep(50);
    }

    robot.stopMovement();
    robot.setRunUsingEncoders();
  }
}
