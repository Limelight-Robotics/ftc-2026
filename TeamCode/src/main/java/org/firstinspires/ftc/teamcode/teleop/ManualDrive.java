package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Full-speed manual drive mode. Provides 1:1 joystick-to-motor control
 * with intake cycling via the Y button.
 */
@TeleOp(name = "Manual Drive", group = "1 - Team Code")
public class ManualDrive extends BaseManualDrive
{
    private static final double FULL_SPEED = 1.0;

    @Override protected double getSpeedMultiplier() { return FULL_SPEED; }
}
