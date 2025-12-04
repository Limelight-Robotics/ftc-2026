package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.classes.Robot;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;

/**
 * Base class for manual drive OpModes. Provides common drive and intake logic.
 * Subclasses specify the speed multiplier via getSpeedMultiplier().
 */
public abstract class BaseManualDrive extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final Robot robot = Robot.createDefault();
    private IntakeMode intakeMode = IntakeMode.OFF;
    private boolean lastIntakeCycleButton = false;

    /** Returns speed multiplier (0.0 to 1.0) for drive inputs. */
    protected abstract double getSpeedMultiplier();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            processGamepadInput();
            processIntakeInput();
            updateTelemetry();
        }
    }

    private void processGamepadInput() {
        double multiplier = getSpeedMultiplier();
        double forward = -gamepad1.left_stick_y * multiplier;
        double strafe = gamepad1.left_stick_x * multiplier;
        double rotate = gamepad1.right_stick_x * multiplier;
        robot.driveWithGamepad(forward, strafe, rotate);
    }

    private void processIntakeInput() {
        boolean cyclePressed = gamepad1.y;
        if (cyclePressed && !lastIntakeCycleButton) {
            intakeMode = intakeMode.next();
        }
        lastIntakeCycleButton = cyclePressed;
        robot.setIntakePower(intakeMode.getPower());
    }

    private void updateTelemetry() {
        RobotStatus status = robot.getStatus();
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                status.getFrontLeftPower(), status.getFrontRightPower(),
                status.getBackLeftPower(), status.getBackRightPower());
        telemetry.addData("Inputs", "Fwd: %.2f, Str: %.2f, Rot: %.2f",
                status.getLastForward(), status.getLastStrafe(), status.getLastRotate());
        telemetry.addData("Intake", "Mode: %s, Power: %.2f", 
                intakeMode.toString(), status.getIntakePower());
        telemetry.update();
    }
}
