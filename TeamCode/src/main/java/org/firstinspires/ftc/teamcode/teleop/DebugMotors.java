package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Debug op mode for testing individual motors.
 * Each button controls one motor at full power while held:
 *   Triangle - Front Left
 *   Circle   - Front Right
 *   Square   - Back Left
 *   Cross    - Back Right
 */
@TeleOp(name = "Debug Motors", group = "Diagnostic") public class DebugMotors extends LinearOpMode
{
    private static final double MOTOR_POWER = 1.0;
    private ElapsedTime         runtime     = new ElapsedTime();
    private DcMotor             fL;
    private DcMotor             fR;
    private DcMotor             bL;
    private DcMotor             bR;

    @Override public void runOpMode()
    {
        fL = hardwareMap.get(DcMotor.class, "leftFront");
        fR = hardwareMap.get(DcMotor.class, "rightFront");
        bL = hardwareMap.get(DcMotor.class, "leftBack");
        bR = hardwareMap.get(DcMotor.class, "rightBack");

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Controls", "△=FL, ○=FR, □=BL, ✕=BR");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive())
        {
            // Map all 4 buttons on the PS controller to 4 motors.
            // Hold button = forward, hold button + left bumper = reverse
            double direction = gamepad1.left_bumper ? -1.0 : 1.0;

            fL.setPower(gamepad1.triangle ? MOTOR_POWER * direction : 0);
            fR.setPower(gamepad1.circle ? MOTOR_POWER * direction : 0);
            bL.setPower(gamepad1.square ? MOTOR_POWER * direction : 0);
            bR.setPower(gamepad1.cross ? MOTOR_POWER * direction : 0);

            // Display motor status
            telemetry.addData("Status", "Run Time: %.2f", runtime.seconds());
            telemetry.addData("Controls", "△=FL, ○=FR, □=BL, ✕=BR");
            telemetry.addData("Direction", gamepad1.left_bumper ? "REVERSE (LB held)" : "FORWARD");
            telemetry.addData("Front Left (△)", getMotorStatus(gamepad1.triangle, direction));
            telemetry.addData("Front Right (○)", getMotorStatus(gamepad1.circle, direction));
            telemetry.addData("Back Left (□)", getMotorStatus(gamepad1.square, direction));
            telemetry.addData("Back Right (✕)", getMotorStatus(gamepad1.cross, direction));
            telemetry.update();
        }
    }

    private String getMotorStatus(boolean pressed, double direction)
    {
        if (!pressed)
            return "off";
        return direction > 0 ? "FORWARD" : "REVERSE";
    }
}
