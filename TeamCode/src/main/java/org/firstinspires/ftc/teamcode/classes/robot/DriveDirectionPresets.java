package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Encapsulates drive motor direction presets for each wheel position.
 * Provides base directions for FL, FR, BL, and BR motors.
 */
public class DriveDirectionPresets {
  // Base directions for the logical wheel positions
  private static final DcMotor.Direction FL_BASE = DcMotor.Direction.FORWARD;
  private static final DcMotor.Direction FR_BASE = DcMotor.Direction.REVERSE;
  private static final DcMotor.Direction BL_BASE = DcMotor.Direction.FORWARD;
  private static final DcMotor.Direction BR_BASE = DcMotor.Direction.FORWARD;

  /**
   * Apply base directions to motors.
   */
  public void applyTo(DcMotor fl, DcMotor fr, DcMotor bl, DcMotor br) {
    fl.setDirection(FL_BASE);
    fr.setDirection(FR_BASE);
    bl.setDirection(BL_BASE);
    br.setDirection(BR_BASE);
  }
}
