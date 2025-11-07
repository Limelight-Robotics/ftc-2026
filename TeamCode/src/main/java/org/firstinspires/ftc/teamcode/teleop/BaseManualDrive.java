package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Robot;

enum IntakeMode {
    REGULAR, REVERSE, OFF
}

/**
 * Base class for manual drive OpModes. Provides common drive and intake control
 * logic. Subclasses specify the speed multiplier via getSpeedMultiplier().
 */
public abstract class BaseManualDrive extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final Robot robot = Robot.createDefault();

    /**
     * Returns the speed multiplier for drive inputs (0.0 to 1.0).
     * Override this in subclasses to set different speeds.
     */
    protected abstract double getSpeedMultiplier();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        IntakeMode intakeMode = IntakeMode.OFF;
        boolean lastIntakeCycleButton = false;

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            double multiplier = getSpeedMultiplier();
            double forward = -gamepad1.left_stick_y * multiplier;
            double strafe = gamepad1.left_stick_x * multiplier;
            double rotate = gamepad1.right_stick_x * multiplier;
            robot.driveWithGamepad(forward, strafe, rotate);

            intakeMode = updateIntakeMode(intakeMode, gamepad1.y, lastIntakeCycleButton);
            lastIntakeCycleButton = gamepad1.y;

            applyIntakeMode(intakeMode);
            updateTelemetry(forward, strafe, rotate);
        }
    }

    private IntakeMode updateIntakeMode(IntakeMode current, boolean cyclePressed,
            boolean lastPressed) {
        if (cyclePressed && !lastPressed) {
            IntakeMode next;
            switch (current) {
                case REGULAR:
                    next = IntakeMode.REVERSE;
                    break;
                case REVERSE:
                    next = IntakeMode.OFF;
                    break;
                case OFF:
                default:
                    next = IntakeMode.REGULAR;
                    break;
            }
            telemetry.addData("IntakeMode", next.toString());
            telemetry.update();
            return next;
        }
        return current;
    }

    private void applyIntakeMode(IntakeMode mode) {
        double power;
        switch (mode) {
            case REGULAR:
                power = 1.0;
                break;
            case REVERSE:
                power = -1.0;
                break;
            case OFF:
            default:
                power = 0.0;
                break;
        }
        robot.setIntakePower(power);
    }

    private void updateTelemetry(double forward, double strafe, double rotate) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Forward", "%4.2f", forward);
        telemetry.addData("Strafe", "%4.2f", strafe);
        telemetry.addData("Rotate", "%4.2f", rotate);

        org.firstinspires.ftc.teamcode.classes.robot.RobotStatus status = robot.getStatus();
        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                status.getFrontLeftPower(), status.getFrontRightPower(),
                status.getBackLeftPower(), status.getBackRightPower());
        telemetry.addData("Drive Inputs", "Forward: %.2f, Strafe: %.2f, Rotate: %.2f",
                status.getLastForward(), status.getLastStrafe(), status.getLastRotate());

        org.firstinspires.ftc.teamcode.classes.Vision.TargetData t = status.getLastTargetData();
        if (t != null && t.isAcquired) {
            telemetry.addData("Target (X,Y,Z)", "%.2f, %.2f, %.2f",
                    t.xPosition, t.yPosition, t.zPosition);
        }
        telemetry.addData("IntakePower", "%.2f", status.getIntakePower());
        telemetry.update();
    }
}
