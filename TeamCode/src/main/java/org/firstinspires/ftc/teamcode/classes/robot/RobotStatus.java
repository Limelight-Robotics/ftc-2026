package org.firstinspires.ftc.teamcode.classes.robot;

import org.firstinspires.ftc.teamcode.classes.Vision;

/**
 * Read-only view of live robot status used by OpModes for telemetry and
 * debugging.
 */
public interface RobotStatus {
    double getFrontLeftPower();

    double getFrontRightPower();

    double getBackLeftPower();

    double getBackRightPower();

    double getLastAxial();

    double getLastLateral();

    double getLastYaw();

    Vision.TargetData getLastTargetData();
}
