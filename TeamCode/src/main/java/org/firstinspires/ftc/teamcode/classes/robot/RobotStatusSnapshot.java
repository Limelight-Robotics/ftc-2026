package org.firstinspires.ftc.teamcode.classes.robot;

/**
 * Immutable snapshot implementation of RobotStatus.
 */
public final class RobotStatusSnapshot implements RobotStatus
{
    private final double frontLeftPower;
    private final double frontRightPower;
    private final double backLeftPower;
    private final double backRightPower;
    private final double lastForward;
    private final double lastStrafe;
    private final double lastRotate;
    private final double intakePower;

    private RobotStatusSnapshot(Builder builder)
    {
        this.frontLeftPower  = builder.frontLeftPower;
        this.frontRightPower = builder.frontRightPower;
        this.backLeftPower   = builder.backLeftPower;
        this.backRightPower  = builder.backRightPower;
        this.lastForward     = builder.lastForward;
        this.lastStrafe      = builder.lastStrafe;
        this.lastRotate      = builder.lastRotate;
        this.intakePower     = builder.intakePower;
    }

    public static Builder builder() { return new Builder(); }

    @Override public double getFrontLeftPower() { return frontLeftPower; }
    @Override public double getFrontRightPower() { return frontRightPower; }
    @Override public double getBackLeftPower() { return backLeftPower; }
    @Override public double getBackRightPower() { return backRightPower; }
    @Override public double getLastForward() { return lastForward; }
    @Override public double getLastStrafe() { return lastStrafe; }
    @Override public double getLastRotate() { return lastRotate; }
    @Override public double getIntakePower() { return intakePower; }

    /** Fluent builder for RobotStatusSnapshot. */
    public static class Builder
    {
        private double frontLeftPower;
        private double frontRightPower;
        private double backLeftPower;
        private double backRightPower;
        private double lastForward;
        private double lastStrafe;
        private double lastRotate;
        private double intakePower;

        public Builder frontLeftPower(double v)
        {
            this.frontLeftPower = v;
            return this;
        }
        public Builder frontRightPower(double v)
        {
            this.frontRightPower = v;
            return this;
        }
        public Builder backLeftPower(double v)
        {
            this.backLeftPower = v;
            return this;
        }
        public Builder backRightPower(double v)
        {
            this.backRightPower = v;
            return this;
        }
        public Builder lastForward(double v)
        {
            this.lastForward = v;
            return this;
        }
        public Builder lastStrafe(double v)
        {
            this.lastStrafe = v;
            return this;
        }
        public Builder lastRotate(double v)
        {
            this.lastRotate = v;
            return this;
        }
        public Builder intakePower(double v)
        {
            this.intakePower = v;
            return this;
        }

        public RobotStatusSnapshot build() { return new RobotStatusSnapshot(this); }
    }
}
