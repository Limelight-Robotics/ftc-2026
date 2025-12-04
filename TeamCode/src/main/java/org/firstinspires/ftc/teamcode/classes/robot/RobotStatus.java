package org.firstinspires.ftc.teamcode.classes.robot;

/**
 * Read-only view of live robot status for telemetry and debugging.
 */
public interface RobotStatus {
    double getFrontLeftPower();
    double getFrontRightPower();
    double getBackLeftPower();
    double getBackRightPower();
    double getLastForward();
    double getLastStrafe();
    double getLastRotate();
    double getIntakePower();
}
