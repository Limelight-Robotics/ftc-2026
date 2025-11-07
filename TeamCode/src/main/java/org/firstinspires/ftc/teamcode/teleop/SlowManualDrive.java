package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Reduced-speed manual drive mode. Provides 50% joystick-to-motor control
 * for more precise maneuvering. Includes intake cycling via the Y button.
 */
@TeleOp(name = "2x Slower Manual Drive", group = "Linear OpMode")
public class SlowManualDrive extends BaseManualDrive {
    @Override
    protected double getSpeedMultiplier() {
        return 0.5;
    }
}
