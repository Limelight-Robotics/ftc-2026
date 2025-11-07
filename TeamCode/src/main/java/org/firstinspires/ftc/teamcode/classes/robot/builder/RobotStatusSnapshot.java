package org.firstinspires.ftc.teamcode.classes.robot.builder;

import org.firstinspires.ftc.teamcode.classes.Vision;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;

/**
 * Immutable snapshot implementation of RobotStatus.
 * Provides read-only access to robot state captured at a specific moment.
 * Package-private; constructed via RobotStatusBuilder.build().
 */
final class RobotStatusSnapshot implements RobotStatus {
    private final RobotStatusData data;

    /**
     * Constructs snapshot with captured state data.
     * Package-private to restrict creation to builder pattern.
     */
    RobotStatusSnapshot(RobotStatusData data) {
        this.data = data;
    }

    @Override
    public double getFrontLeftPower() {
        return data.frontLeftPower;
    }

    @Override
    public double getFrontRightPower() {
        return data.frontRightPower;
    }

    @Override
    public double getBackLeftPower() {
        return data.backLeftPower;
    }

    @Override
    public double getBackRightPower() {
        return data.backRightPower;
    }

    @Override
    public double getLastForward() {
        return data.lastForward;
    }

    @Override
    public double getLastStrafe() {
        return data.lastStrafe;
    }

    @Override
    public double getLastRotate() {
        return data.lastRotate;
    }

    @Override
    public Vision.TargetData getLastTargetData() {
        return data.lastTargetData;
    }

    @Override
    public double getIntakePower() {
        return data.intakePower;
    }
}
