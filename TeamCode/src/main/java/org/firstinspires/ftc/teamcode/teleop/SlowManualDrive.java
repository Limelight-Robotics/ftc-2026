package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Robot;

@TeleOp(name = "2x Slower Manual Drive", group = "Linear OpMode")
public class SlowManualDrive extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final Robot robot = Robot.createDefault();
    private static final double SLOW_SCALE = 0.5;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            double axial = -gamepad1.left_stick_y * SLOW_SCALE;
            double lateral = gamepad1.left_stick_x * SLOW_SCALE;
            double yaw = gamepad1.right_stick_x * SLOW_SCALE;
            robot.driveWithGamepad(axial, lateral, yaw);
            updateTelemetry(axial, lateral, yaw);
        }
    }

    private void updateTelemetry(double axial, double lateral, double yaw) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Axial", "%4.2f", axial);
        telemetry.addData("Lateral", "%4.2f", lateral);
        telemetry.addData("Yaw", "%4.2f", yaw);
        org.firstinspires.ftc.teamcode.classes.robot.RobotStatus status = robot.getStatus();
        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                status.getFrontLeftPower(), status.getFrontRightPower(), status.getBackLeftPower(),
                status.getBackRightPower());
        telemetry.addData("Drive Inputs", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f", status.getLastAxial(),
                status.getLastLateral(), status.getLastYaw());
        telemetry.update();
    }
}
