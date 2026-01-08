package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Debug op mode for testing individual motors.
 * Each button controls one motor at full power while held:
 *   Y - Front Left
 *   B - Front Right
 *   X - Back Left
 *   A - Back Right
 */
@TeleOp(name = "Debug Motors", group = "Linear OpMode")
public class DebugMotors extends LinearOpMode {
    private static final double MOTOR_POWER = 1.0;
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor fL;
    private DcMotor fR;
    private DcMotor bL;
    private DcMotor bR;

    @Override
    public void runOpMode() {

        fL = hardwareMap.get(DcMotor.class, "leftFront");
        fR = hardwareMap.get(DcMotor.class, "rightFront");
        bL = hardwareMap.get(DcMotor.class, "leftBack");
        bR = hardwareMap.get(DcMotor.class, "rightBack");

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Controls", "Y=FL, B=FR, X=BL, A=BR");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Map all 4 buttons on the PS controller to 4 motors.
            // Hold button = forward, hold button + left bumper = reverse
            double direction = gamepad1.left_bumper ? -1.0 : 1.0;

            fL.setPower(gamepad1.y ? MOTOR_POWER * direction : 0);
            fR.setPower(gamepad1.b ? MOTOR_POWER * direction : 0);
            bL.setPower(gamepad1.x ? MOTOR_POWER * direction : 0);
            bR.setPower(gamepad1.a ? MOTOR_POWER * direction : 0);

            // Display motor status
            telemetry.addData("Status", "Run Time: %.2f", runtime.seconds());
            telemetry.addData("Controls", "Y=FL, B=FR, X=BL, A=BR");
            telemetry.addData("Direction", gamepad1.left_bumper ? "REVERSE (LB held)" : "FORWARD");
            telemetry.addData("Front Left (Y)", getMotorStatus(gamepad1.y, direction));
            telemetry.addData("Front Right (B)", getMotorStatus(gamepad1.b, direction));
            telemetry.addData("Back Left (X)", getMotorStatus(gamepad1.x, direction));
            telemetry.addData("Back Right (A)", getMotorStatus(gamepad1.a, direction));
            telemetry.update();
        }
    }

    private String getMotorStatus(boolean pressed, double direction) {
        if (!pressed) return "off";
        return direction > 0 ? "FORWARD" : "REVERSE";
    }
}
