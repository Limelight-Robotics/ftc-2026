package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * ManualTurretControl provides gamepad control of turret motor.
 * Right trigger: rotate turret clockwise
 * Left trigger: rotate turret counterclockwise
 */
@TeleOp(name = "Manual Turret Control", group = "Turret")
public class ManualTurretControl extends LinearOpMode {
    private static final String TURRET_MOTOR_NAME = "turret";
    private static final double MAX_MOTOR_POWER = 0.5;

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor turretMotor;

    @Override
    public void runOpMode() {
        if (!initializeTurret()) {
            return;
        }

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            double turretPower = computeTurretPower();
            turretMotor.setPower(turretPower);
            updateTelemetry(turretPower);
        }

        turretMotor.setPower(0.0);
    }

    private boolean initializeTurret() {
        try {
            turretMotor = hardwareMap.get(DcMotor.class, TURRET_MOTOR_NAME);
            turretMotor.setPower(0.0);
            return true;
        } catch (Exception e) {
            telemetry.addData("Error", "Failed to init turret: " + e.getMessage());
            telemetry.update();
            return false;
        }
    }

    private double computeTurretPower() {
        double rightTrigger = gamepad1.right_trigger;
        double leftTrigger = gamepad1.left_trigger;
        return (rightTrigger - leftTrigger) * MAX_MOTOR_POWER;
    }

    private void updateTelemetry(double power) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Turret Power", "%.2f", power);
        telemetry.addData("Triggers", "R: %.2f, L: %.2f", 
                gamepad1.right_trigger, gamepad1.left_trigger);
        telemetry.update();
    }
}
