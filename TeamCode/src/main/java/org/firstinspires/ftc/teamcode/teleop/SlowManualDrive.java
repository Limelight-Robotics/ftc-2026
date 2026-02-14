package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Reduced-speed manual drive mode. Provides 50% joystick-to-motor control
 * for more precise maneuvering. Includes intake cycling via the Y button.
 */
@TeleOp(name = "Slow Manual Drive", group = "1 - Team Code")
public class SlowManualDrive extends BaseManualDrive {
  private static final double SLOW_SPEED = 0.5;

  @Override
  protected double getSpeedMultiplier() {
    return SLOW_SPEED;
  }
}
