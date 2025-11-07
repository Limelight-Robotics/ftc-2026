package org.firstinspires.ftc.teamcode.classes.robot.builder;

import org.firstinspires.ftc.teamcode.classes.Vision;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;

/**
 * Java 8 compatible fluent builder for constructing RobotStatus snapshots.
 * Captures robot state at build time and returns immutable RobotStatus.
 *
 * Usage example:
 * RobotStatus status = RobotStatusBuilder.create()
 * .frontLeftPower(0.5)
 * .frontRightPower(0.5)
 * .build();
 */
public class RobotStatusBuilder {
    private double frontLeftPower;
    private double frontRightPower;
    private double backLeftPower;
    private double backRightPower;
    private double lastForward;
    private double lastStrafe;
    private double lastRotate;
    private Vision.TargetData lastTargetData;
    private double intakePower;

    /**
     * Create new builder for constructing RobotStatus.
     */
    public static RobotStatusBuilder create() {
        return new RobotStatusBuilder();
    }

    public RobotStatusBuilder frontLeftPower(double value) {
        this.frontLeftPower = value;
        return this;
    }

    public RobotStatusBuilder frontRightPower(double value) {
        this.frontRightPower = value;
        return this;
    }

    public RobotStatusBuilder backLeftPower(double value) {
        this.backLeftPower = value;
        return this;
    }

    public RobotStatusBuilder backRightPower(double value) {
        this.backRightPower = value;
        return this;
    }

    public RobotStatusBuilder lastForward(double value) {
        this.lastForward = value;
        return this;
    }

    public RobotStatusBuilder lastStrafe(double value) {
        this.lastStrafe = value;
        return this;
    }

    public RobotStatusBuilder lastRotate(double value) {
        this.lastRotate = value;
        return this;
    }

    public RobotStatusBuilder lastTargetData(Vision.TargetData value) {
        this.lastTargetData = value;
        return this;
    }

    public RobotStatusBuilder intakePower(double value) {
        this.intakePower = value;
        return this;
    }

    /**
     * Build and return immutable RobotStatus snapshot.
     * Captures all builder state at this moment.
     */
    public RobotStatus build() {
        RobotStatusData data = new RobotStatusData(
                frontLeftPower, frontRightPower, backLeftPower, backRightPower,
                lastForward, lastStrafe, lastRotate, lastTargetData, intakePower);
        return new RobotStatusSnapshot(data);
    }
}
