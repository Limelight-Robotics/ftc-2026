package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Full-speed manual drive mode. Provides 1:1 joystick-to-motor control
 * with intake cycling via the Y button.
 */
@TeleOp(name = "Manual Drive", group = "Linear OpMode")
public class ManualDrive extends BaseManualDrive {
    @Override
    protected double getSpeedMultiplier() {
        return 1.0;
    }
}
