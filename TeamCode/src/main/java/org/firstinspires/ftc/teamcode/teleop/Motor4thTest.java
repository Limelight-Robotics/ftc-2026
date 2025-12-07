package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Motor 4th Test", group = "Test")
public class Motor4thTest extends LinearOpMode {
    private static final String MOTOR_NAME = "test";
    private static final double MAX_MOTOR_POWER = 1.0;

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor motor;

    @Override
    public void runOpMode() {
        if (!initializeMotor()) {
            return;
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        // We move the motor forward as fast as possible a
        // fourth of the way, then immediately back to starting position.
        while (opModeIsActive()) {
            motor.setPower(MAX_MOTOR_POWER);
            sleep(5000);
            motor.setPower(-MAX_MOTOR_POWER);
            sleep(5000);
            motor.setPower(0.0);
            // exit after one cycle
            throw new RuntimeException("Test complete");
        }

        motor.setPower(0.0);
    }

    private boolean initializeMotor() {
        try {
            motor = hardwareMap.get(DcMotor.class, MOTOR_NAME);
            motor.setPower(0.0);
            return true;
        } catch (Exception e) {
            telemetry.addData("Error", "Failed to init motor: " + e.getMessage());
            telemetry.update();
            return false;
        }
    }

    private void updateTelemetry(double power) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motor Power", "%.2f", power);
        telemetry.addData("Triggers", "R: %.2f, L: %.2f", 
                gamepad1.right_trigger, gamepad1.left_trigger);
        telemetry.update();
    }
}
