package org.firstinspires.ftc.teamcode.classes.robot;

/**
 * Immutable data holder describing a movement outcome.
 */
public class MovementResult {
  public final boolean atTarget;
  public final String status;
  public final double axialPower;
  public final double lateralPower;
  public final double yawPower;

  /**
   * Constructs a movement result with the specified state.
   */
  public MovementResult(
      boolean atTarget, String status, double axialPower, double lateralPower, double yawPower) {
    this.atTarget = atTarget;
    this.status = status;
    this.axialPower = axialPower;
    this.lateralPower = lateralPower;
    this.yawPower = yawPower;
  }
}
