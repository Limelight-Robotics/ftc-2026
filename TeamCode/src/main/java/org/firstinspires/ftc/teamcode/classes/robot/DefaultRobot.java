package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.classes.Vision;
import org.firstinspires.ftc.teamcode.classes.robot.builder.RobotStatusBuilder;

/**
 * DefaultRobot is the concrete Robot implementation used by OpModes.
 * It delegates drive work to DriveSubsystem and vision to Vision.
 */
public class DefaultRobot implements org.firstinspires.ftc.teamcode.classes.Robot {
    private final DriveSubsystem drive = new DriveSubsystem();
    private Vision vision;
    private Vision.TargetData cameraData;
    private double lastForward, lastStrafe, lastRotate;
    private DcMotor intakeMotor = null;
    private double intakePower = 0.0;

    // Movement tuning constants
    private static final double FORWARD_TOLERANCE = 0.15;
    private static final double STRAFE_TOLERANCE = 0.05;
    private static final double ROTATE_TOLERANCE_DEG = 5.0;
    private static final double MAX_DRIVE_POWER = 1.0;

    @Override
    public void init(HardwareMap hardwareMap) {
        init(hardwareMap, null);
    }

    @Override
    public void init(HardwareMap hardwareMap, Vision vision) {
        drive.init(hardwareMap);
        try {
            intakeMotor = hardwareMap.get(DcMotor.class, "intake");
            intakeMotor.setPower(0.0);
        } catch (Exception ignored) {
            intakeMotor = null;
        }
        this.vision = vision;
        if (this.vision != null)
            this.vision.init(hardwareMap, "limelight", Vision.Pipeline.APRIL_TAG);
    }

    @Override
    public void drive(double forward, double strafe, double rotate) {
        drive.drive(forward, strafe, rotate);
    }

    @Override
    public void driveWithGamepad(double forward, double strafe, double rotate) {
        lastForward = forward;
        lastStrafe = strafe;
        lastRotate = rotate;
        drive.drive(forward, strafe, rotate);
        drive.updateTelemetryPowers();
    }

    @Override
    public void driveAutonomous(double forward, double strafe, double rotate) {
        drive.drive(forward, strafe, rotate);
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

        double commandedForward = computeCommandedForward(xPos);
        double commandedStrafe = computeCommandedStrafe(yPos);
        double commandedRotate = computeCommandedRotate(tx);

        drive.drive(commandedForward, commandedStrafe, commandedRotate);
        lastForward = commandedForward;
        lastStrafe = commandedStrafe;
        lastRotate = commandedRotate;
        drive.updateTelemetryPowers();

        boolean atTarget = Math.abs(xPos) <= FORWARD_TOLERANCE
                && Math.abs(yPos) <= STRAFE_TOLERANCE
                && Math.abs(tx) <= ROTATE_TOLERANCE_DEG;
        return new MovementResult(atTarget, atTarget ? "At target" : "Moving to tag", commandedForward, commandedStrafe,
                commandedRotate);
    }

    private double computeCommandedForward(double xPos) {
        if (xPos > FORWARD_TOLERANCE)
            return MAX_DRIVE_POWER;
        if (xPos < -FORWARD_TOLERANCE)
            return -MAX_DRIVE_POWER;
        return 0.0;
    }

    private double computeCommandedStrafe(double yPos) {
        if (Math.abs(yPos) > STRAFE_TOLERANCE)
            return -Math.signum(yPos) * MAX_DRIVE_POWER;
        return 0.0;
    }

    private double computeCommandedRotate(double tx) {
        if (Math.abs(tx) > ROTATE_TOLERANCE_DEG)
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
        return RobotStatusBuilder.create()
                .frontLeftPower(drive.getFrontLeftPower())
                .frontRightPower(drive.getFrontRightPower())
                .backLeftPower(drive.getBackLeftPower())
                .backRightPower(drive.getBackRightPower())
                .lastForward(lastForward)
                .lastStrafe(lastStrafe)
                .lastRotate(lastRotate)
                .lastTargetData(cameraData)
                .intakePower(intakePower)
                .build();
    }

    @Override
    public void setIntakePower(double power) {
        intakePower = clamp(power);
        if (intakeMotor != null)
            intakeMotor.setPower(intakePower);
    }

    private double clamp(double v) {
        if (v > 1.0)
            return 1.0;
        if (v < -1.0)
            return -1.0;
        return v;
    }
}
