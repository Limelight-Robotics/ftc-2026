package org.firstinspires.ftc.teamcode.classes.robot.builder;

import org.firstinspires.ftc.teamcode.classes.Vision;

/**
 * Immutable data holder for robot state snapshots.
 * Stores motor powers, movement commands, camera data, and intake power
 * at a specific point in time. Package-private access via builder.
 */
final class RobotStatusData {
    final double frontLeftPower;
    final double frontRightPower;
    final double backLeftPower;
    final double backRightPower;
    final double lastForward;
    final double lastStrafe;
    final double lastRotate;
    final Vision.TargetData lastTargetData;
    final double intakePower;

    /**
     * Constructs immutable status data with all 9 state values.
     * Only RobotStatusFactory should create instances.
     */
    RobotStatusData(double flp, double frp, double blp, double brp,
            double lf, double ls, double lr, Vision.TargetData ltd, double ip) {
        this.frontLeftPower = flp;
        this.frontRightPower = frp;
        this.backLeftPower = blp;
        this.backRightPower = brp;
        this.lastForward = lf;
        this.lastStrafe = ls;
        this.lastRotate = lr;
        this.lastTargetData = ltd;
        this.intakePower = ip;
    }
}
