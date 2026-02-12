package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * ManualShooterControl provides gamepad control of shooter motor.
 * Right trigger: spin shooter forward
 * Left trigger: spin shooter reverse
 */
@TeleOp(name = "Manual Shooter Control", group = "Diagnostic")
public class ManualShooterControl extends LinearOpMode
{
    private static final String SHOOTER_MOTOR_NAME = "shooter";
    private static final double MAX_MOTOR_POWER    = 0.5;

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor           shooterMotor;

    @Override public void runOpMode()
    {
        if (!initializeShooter())
        {
            return;
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive())
        {
            double shooterPower = computeShooterPower();
            shooterMotor.setPower(shooterPower);
            updateTelemetry(shooterPower);
        }

        shooterMotor.setPower(0.0);
    }

    private boolean initializeShooter()
    {
        try
        {
            shooterMotor = hardwareMap.get(DcMotor.class, SHOOTER_MOTOR_NAME);
            shooterMotor.setPower(0.0);
            return true;
        }
        catch (Exception e)
        {
            telemetry.addData("Error", "Failed to init shooter: " + e.getMessage());
            telemetry.update();
            return false;
        }
    }

    private double computeShooterPower()
    {
        double rightTrigger = gamepad1.right_trigger;
        double leftTrigger  = gamepad1.left_trigger;
        return (rightTrigger - leftTrigger) * MAX_MOTOR_POWER;
    }

    private void updateTelemetry(double power)
    {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Shooter Power", "%.2f", power);
        telemetry.addData(
            "Triggers", "R: %.2f, L: %.2f", gamepad1.right_trigger, gamepad1.left_trigger);
        telemetry.update();
    }
}
