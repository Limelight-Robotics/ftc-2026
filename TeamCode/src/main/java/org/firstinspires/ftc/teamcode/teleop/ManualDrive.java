package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Robot;
import org.firstinspires.ftc.teamcode.util.Toggle;
import org.firstinspires.ftc.teamcode.util.ToggleManager;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Manual Drive", group = "Linear OpMode")
public class ManualDrive extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final Robot robot = Robot.createDefault();

    /*
     * // Button edge state for cycling direction presets
     * private boolean lastDpadUp = false;
     * private boolean lastDpadDown = false;
     */

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        // Intake mode enum: REGULAR (forward), REVERSE (backward), OFF
        enum IntakeMode {
            REGULAR, REVERSE, OFF
        }
        IntakeMode intakeMode = IntakeMode.OFF;
        boolean lastIntakeCycleButton = false;

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            double axial = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;
            robot.driveWithGamepad(axial, lateral, yaw);

            // Cycle intake mode on rising edge of Y button: REGULAR -> REVERSE -> OFF ->
            // REGULAR
            boolean cyclePressed = gamepad1.y;
            if (cyclePressed && !lastIntakeCycleButton) {
                switch (intakeMode) {
                    case REGULAR:
                        intakeMode = IntakeMode.REVERSE;
                        break;
                    case REVERSE:
                        intakeMode = IntakeMode.OFF;
                        break;
                    case OFF:
                    default:
                        intakeMode = IntakeMode.REGULAR;
                        break;
                }
                telemetry.addData("IntakeMode", intakeMode.toString());
                telemetry.update();
            }
            lastIntakeCycleButton = cyclePressed;

            // Apply intake mode: forward (1.0) for REGULAR, reverse (-1.0) for REVERSE, off
            // (0.0) for OFF
            switch (intakeMode) {
                case REGULAR:
                    robot.setIntakePower(1.0);
                    break;
                case REVERSE:
                    robot.setIntakePower(-1.0);
                    break;
                case OFF:
                default:
                    robot.setIntakePower(0.0);
                    break;
            }

            /*
             * // Cycle direction preset on rising edge of dpad_up
             * boolean dpadUp = gamepad1.dpad_up;
             * boolean dpadDown = gamepad1.dpad_down;
             * if (dpadUp && !lastDpadUp) {
             * robot.cycleDriveDirectionPreset(1);
             * telemetry.addData("DirectionPreset", robot.getDriveDirectionString());
             * telemetry.update();
             * } else if (dpadDown && !lastDpadDown) {
             * robot.cycleDriveDirectionPreset(-1);
             * telemetry.addData("DirectionPreset", robot.getDriveDirectionString());
             * telemetry.update();
             * }
             * lastDpadUp = dpadUp;
             * lastDpadDown = dpadDown;
             */

            updateTelemetry(axial, lateral, yaw);
        }
    }

    private void updateTelemetry(double axial, double lateral, double yaw) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Axial", "%4.2f", axial);
        telemetry.addData("Lateral", "%4.2f", lateral);
        telemetry.addData("Yaw", "%4.2f", yaw);

        // Drive power telemetry (read from RobotStatus)
        org.firstinspires.ftc.teamcode.classes.robot.RobotStatus status = robot.getStatus();
        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                status.getFrontLeftPower(), status.getFrontRightPower(), status.getBackLeftPower(),
                status.getBackRightPower());
        telemetry.addData("Drive Inputs", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f", status.getLastAxial(),
                status.getLastLateral(), status.getLastYaw());
        org.firstinspires.ftc.teamcode.classes.Vision.TargetData t = status.getLastTargetData();
        if (t != null && t.isAcquired) {
            telemetry.addData("Target (X,Y,Z)", "%.2f, %.2f, %.2f", t.xPosition, t.yPosition, t.zPosition);
        }
        // Intake power from RobotStatus
        telemetry.addData("IntakePower", "%.2f", status.getIntakePower());
        // telemetry.addData("DirectionPreset", robot.getDriveDirectionString());
        telemetry.update();
    }
}
