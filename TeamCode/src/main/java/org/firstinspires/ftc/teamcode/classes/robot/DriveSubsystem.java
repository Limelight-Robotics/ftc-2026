package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.classes.Utilities;

/**
 * DriveSubsystem encapsulates mecanum motor hardware and basic drive
 * operations.
 */
public class DriveSubsystem {
  private static final String MOTOR_FL = "leftFront";
  private static final String MOTOR_BL = "leftBack";
  private static final String MOTOR_FR = "rightFront";
  private static final String MOTOR_BR = "rightBack";

  private DcMotor frontLeft;
  private DcMotor backLeft;
  private DcMotor frontRight;
  private DcMotor backRight;

  private final DriveDirectionPresets directionPresets = new DriveDirectionPresets();
  private double lastFLPower, lastFRPower, lastBLPower, lastBRPower;

  /** Initialize motors from hardware map. */
  public DriveSubsystem(HardwareMap hardwareMap) {
    if (hardwareMap == null) {
      throw new IllegalArgumentException("hardwareMap cannot be null");
    }
    frontLeft = hardwareMap.get(DcMotor.class, MOTOR_FL);
    backLeft = hardwareMap.get(DcMotor.class, MOTOR_BL);
    frontRight = hardwareMap.get(DcMotor.class, MOTOR_FR);
    backRight = hardwareMap.get(DcMotor.class, MOTOR_BR);

    validateMotors();
    directionPresets.applyTo(frontLeft, frontRight, backLeft, backRight);
  }

  private void validateMotors() {
    String[] names = {"frontLeft", "backLeft", "frontRight", "backRight"};
    DcMotor[] motors = {frontLeft, backLeft, frontRight, backRight};
    StringBuilder missing = new StringBuilder();
    for (int i = 0; i < motors.length; i++) {
      if (motors[i] == null) {
        missing.append(names[i]).append(" ");
      }
    }
    if (missing.length() > 0) {
      throw new IllegalStateException(
          "Missing drive motor(s) in hardware map: " + missing.toString().trim());
    }
  }

  /** Drive with mecanum kinematics. */
  public void drive(double forward, double strafe, double rotate) {
    double fl = forward + strafe + rotate;
    double fr = forward - strafe - rotate;
    double bl = forward - strafe + rotate;
    double br = forward + strafe - rotate;
    double[] normalized = Utilities.normalizeMecanum(fl, fr, bl, br);
    setMotorPowers(normalized);
  }

  /** Stop all drive motors. */
  public void stop() {
    setMotorPowers(new double[] {0.0, 0.0, 0.0, 0.0});
  }

  private void setMotorPowers(double[] powers) {
    if (powers == null || powers.length < 4)
      return;
    frontLeft.setPower(powers[0]);
    frontRight.setPower(powers[1]);
    backLeft.setPower(powers[2]);
    backRight.setPower(powers[3]);
    updateTelemetryPowers();
  }

  /** Update cached power values for telemetry. */
  public void updateTelemetryPowers() {
    lastFLPower = frontLeft != null ? frontLeft.getPower() : 0.0;
    lastFRPower = frontRight != null ? frontRight.getPower() : 0.0;
    lastBLPower = backLeft != null ? backLeft.getPower() : 0.0;
    lastBRPower = backRight != null ? backRight.getPower() : 0.0;
  }

  public double getFrontLeftPower() {
    return lastFLPower;
  }
  public double getFrontRightPower() {
    return lastFRPower;
  }
  public double getBackLeftPower() {
    return lastBLPower;
  }
  public double getBackRightPower() {
    return lastBRPower;
  }

  /** Reset all drive encoder counts to zero. */
  public void resetEncoders() {
    setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    setRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
  }

  /** Set target positions and switch to RUN_TO_POSITION mode. */
  public void setTargetPositions(int flTicks, int frTicks, int blTicks, int brTicks) {
    frontLeft.setTargetPosition(flTicks);
    frontRight.setTargetPosition(frTicks);
    backLeft.setTargetPosition(blTicks);
    backRight.setTargetPosition(brTicks);
    setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
  }

  /** Set all motors to the same absolute power (RUN_TO_POSITION uses absolute value). */
  public void setAllMotorPower(double power) {
    frontLeft.setPower(power);
    frontRight.setPower(power);
    backLeft.setPower(power);
    backRight.setPower(power);
  }

  /** Returns true if any drive motor is still traveling to its target. */
  public boolean areMotorsBusy() {
    return frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy();
  }

  /** Switch all motors back to RUN_USING_ENCODER mode. */
  public void setRunUsingEncoders() {
    setRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
  }

  private void setRunMode(DcMotor.RunMode mode) {
    frontLeft.setMode(mode);
    frontRight.setMode(mode);
    backLeft.setMode(mode);
    backRight.setMode(mode);
  }
}
