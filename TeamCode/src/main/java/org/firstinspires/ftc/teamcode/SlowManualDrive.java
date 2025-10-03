package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Robot;

@TeleOp(name="2x Slower Manual Drive", group="Linear OpMode")
public class SlowManualDrive extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final Robot robot = new Robot();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            double axial   = -gamepad1.left_stick_y * 0.5;
            double lateral =  gamepad1.left_stick_x * 0.5;
            double yaw     =  gamepad1.right_stick_x * 0.5;
            robot.driveWithGamepad(axial, lateral, yaw);
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Axial", "%4.2f", axial);
            telemetry.addData("Lateral", "%4.2f", lateral);
            telemetry.addData("Yaw", "%4.2f", yaw);
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", robot.getFrontLeftPower(), robot.getFrontRightPower());
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", robot.getBackLeftPower(), robot.getBackRightPower());
            telemetry.update();
        }
    }
}

