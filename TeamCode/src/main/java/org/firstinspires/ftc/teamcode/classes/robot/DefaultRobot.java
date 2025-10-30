package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.classes.Vision;

/**
 * DefaultRobot is the concrete Robot implementation used by OpModes.
 * It delegates drive work to DriveSubsystem and vision to Vision.
 */
public class DefaultRobot implements org.firstinspires.ftc.teamcode.classes.Robot {
    private final DriveSubsystem drive = new DriveSubsystem();
    private Vision vision;
    private Vision.TargetData cameraData;
    private double lastAxial, lastLateral, lastYaw;

    // Movement tuning constants
    private static final double AXIAL_TOLERANCE = 0.15;
    private static final double LATERAL_TOLERANCE = 0.05;
    private static final double YAW_TOLERANCE_DEG = 5.0;
    private static final double MAX_DRIVE_POWER = 1.0;

    @Override
    public void init(HardwareMap hardwareMap) {
        init(hardwareMap, null);
    }

    @Override
    public void init(HardwareMap hardwareMap, Vision vision) {
        drive.init(hardwareMap);
        this.vision = vision;
        if (this.vision != null)
            this.vision.init(hardwareMap, "limelight", Vision.Pipeline.APRIL_TAG);
    }

    @Override
    public void drive(double axial, double lateral, double yaw) {
        drive.drive(axial, lateral, yaw);
    }

    @Override
    public void driveWithGamepad(double axial, double lateral, double yaw) {
        lastAxial = axial;
        lastLateral = lateral;
        lastYaw = yaw;
        drive.drive(axial, lateral, yaw);
        drive.updateTelemetryPowers();
    }

    @Override
    public void driveAutonomous(double axial, double lateral, double yaw) {
        drive.drive(axial, lateral, yaw);
    }

    @Override
    public MovementResult moveToAprilTag(Vision.TargetData targetData) {
        if (targetData == null || !targetData.isAcquired) {
            drive.stop();
            return new MovementResult(false, "No target", 0.0, 0.0, 0.0);
        }
        this.cameraData = targetData;
        return executeMoveToAprilTag(targetData);
    }

    private MovementResult executeMoveToAprilTag(Vision.TargetData targetData) {
        double xPos = targetData.xPosition;
        double yPos = targetData.yPosition;
        double tx = (targetData.result != null) ? targetData.result.getTx() : 0.0;

        double commandedAxial = computeCommandedAxial(xPos);
        double commandedLateral = computeCommandedLateral(yPos);
        double commandedYaw = computeCommandedYaw(tx);

        drive.drive(commandedAxial, commandedLateral, commandedYaw);
        lastAxial = commandedAxial;
        lastLateral = commandedLateral;
        lastYaw = commandedYaw;
        drive.updateTelemetryPowers();

        boolean atTarget = Math.abs(xPos) <= AXIAL_TOLERANCE
                && Math.abs(yPos) <= LATERAL_TOLERANCE
                && Math.abs(tx) <= YAW_TOLERANCE_DEG;
        return new MovementResult(atTarget, atTarget ? "At target" : "Moving to tag", commandedAxial, commandedLateral,
                commandedYaw);
    }

    private double computeCommandedAxial(double xPos) {
        if (xPos > AXIAL_TOLERANCE)
            return MAX_DRIVE_POWER;
        if (xPos < -AXIAL_TOLERANCE)
            return -MAX_DRIVE_POWER;
        return 0.0;
    }

    private double computeCommandedLateral(double yPos) {
        if (Math.abs(yPos) > LATERAL_TOLERANCE)
            return -Math.signum(yPos) * MAX_DRIVE_POWER;
        return 0.0;
    }

    private double computeCommandedYaw(double tx) {
        if (Math.abs(tx) > YAW_TOLERANCE_DEG)
            return Math.signum(tx) * MAX_DRIVE_POWER;
        return 0.0;
    }

    @Override
    public void stopMovement() {
        drive.stop();
    }

    public void cycleDriveDirectionPreset() {
        drive.cycleDriveDirectionPreset(1);
    }

    @Override
    public void cycleDriveDirectionPreset(int delta) {
        drive.cycleDriveDirectionPreset(delta);
    }

    @Override
    public int getDriveDirectionPreset() {
        return drive.getDirectionPreset();
    }

    @Override
    public String getDriveDirectionString() {
        return drive.getDirectionString();
    }

    @Override
    public org.firstinspires.ftc.teamcode.classes.robot.RobotStatus getStatus() {
        return new RobotStatusImpl();
    }

    private class RobotStatusImpl implements org.firstinspires.ftc.teamcode.classes.robot.RobotStatus {
        @Override
        public double getFrontLeftPower() {
            return drive.getFrontLeftPower();
        }

        @Override
        public double getFrontRightPower() {
            return drive.getFrontRightPower();
        }

        @Override
        public double getBackLeftPower() {
            return drive.getBackLeftPower();
        }

        @Override
        public double getBackRightPower() {
            return drive.getBackRightPower();
        }

        @Override
        public double getLastAxial() {
            return lastAxial;
        }

        @Override
        public double getLastLateral() {
            return lastLateral;
        }

        @Override
        public double getLastYaw() {
            return lastYaw;
        }

        @Override
        public Vision.TargetData getLastTargetData() {
            return cameraData;
        }
    }
}
