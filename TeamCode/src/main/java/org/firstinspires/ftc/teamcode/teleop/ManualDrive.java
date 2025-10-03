package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.Robot;

@TeleOp(name="Manual Drive", group="Linear OpMode")
public class ManualDrive extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final Robot robot = new Robot();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            double axial   = -gamepad1.left_stick_y;
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;
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
