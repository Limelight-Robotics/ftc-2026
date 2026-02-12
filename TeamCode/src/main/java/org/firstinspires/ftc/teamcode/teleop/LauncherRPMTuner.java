package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.classes.DefaultRobot;
import org.firstinspires.ftc.teamcode.classes.Vision;

/**
 * Tuning OpMode for building the LauncherHelper RPM lookup table.
 *
 * Controls:
 *   D-pad Up/Down   — adjust target RPM by ±500
 *   D-pad Left/Right — adjust target RPM by ±100
 *   Cross button    — toggle launcher motor on/off at current target RPM
 *   Circle button   — stop motor (safety)
 *
 * Workflow:
 *   1. Set RPM with d-pad, press Cross to spin up
 *   2. Fire a ball and measure the landing distance
 *   3. Record the {distance, RPM} pair
 *   4. Repeat at 3-5 distances, then update RPM_TABLE in LauncherHelper
 */
@TeleOp(name = "Launcher RPM Tuner", group = "Diagnostic")
public class LauncherRPMTuner extends LinearOpMode
{
    private static final String SHOOTER_MOTOR_NAME = "turret";
    private static final double TICKS_PER_REV      = 28.0;
    private static final double MIN_RPM            = 0;
    private static final double MAX_RPM            = 8000;

    @Override public void runOpMode()
    {
        // Init motor
        DcMotorEx motor = null;
        try
        {
            motor = hardwareMap.get(DcMotorEx.class, SHOOTER_MOTOR_NAME);
            motor.setPower(0.0);
        }
        catch (Exception e)
        {
            telemetry.addData("ERROR", "Shooter motor not found: " + e.getMessage());
        }

        // Init vision (optional — shows distance if AprilTag visible)
        DefaultRobot robot  = new DefaultRobot();
        Vision       vision = null;
        try
        {
            robot.init(hardwareMap);
            vision = robot.getVision();
        }
        catch (Exception e)
        {
            telemetry.addData("Vision", "Not available: " + e.getMessage());
        }

        double  targetRPM    = 2000;
        boolean motorRunning = false;

        // Previous d-pad / button states for edge detection
        boolean prevDpadUp    = false;
        boolean prevDpadDown  = false;
        boolean prevDpadLeft  = false;
        boolean prevDpadRight = false;
        boolean prevCross     = false;
        boolean prevCircle    = false;

        telemetry.addData("Status", "Initialized — press Play to start");
        telemetry.addData("Controls", "D-pad ↑↓ ±500 | D-pad ←→ ±100 | Cross toggle | Circle stop");
        telemetry.update();

        waitForStart();

        while (opModeIsActive())
        {
            // --- Edge-detected RPM adjustments ---
            if (gamepad1.dpad_up && !prevDpadUp)
            {
                targetRPM = Math.min(targetRPM + 500, MAX_RPM);
            }
            if (gamepad1.dpad_down && !prevDpadDown)
            {
                targetRPM = Math.max(targetRPM - 500, MIN_RPM);
            }
            if (gamepad1.dpad_right && !prevDpadRight)
            {
                targetRPM = Math.min(targetRPM + 100, MAX_RPM);
            }
            if (gamepad1.dpad_left && !prevDpadLeft)
            {
                targetRPM = Math.max(targetRPM - 100, MIN_RPM);
            }

            // --- Toggle motor on/off ---
            if (gamepad1.cross && !prevCross)
            {
                motorRunning = !motorRunning;
            }
            if (gamepad1.circle && !prevCircle)
            {
                motorRunning = false;
            }

            // --- Save previous button states ---
            prevDpadUp    = gamepad1.dpad_up;
            prevDpadDown  = gamepad1.dpad_down;
            prevDpadLeft  = gamepad1.dpad_left;
            prevDpadRight = gamepad1.dpad_right;
            prevCross     = gamepad1.cross;
            prevCircle    = gamepad1.circle;

            // --- Apply motor velocity ---
            double actualRPM = 0;
            if (motor != null)
            {
                if (motorRunning && targetRPM > 0)
                {
                    double ticksPerSec = targetRPM * TICKS_PER_REV / 60.0;
                    motor.setVelocity(ticksPerSec);
                }
                else
                {
                    motor.setVelocity(0);
                }
                actualRPM = motor.getVelocity() * 60.0 / TICKS_PER_REV;
            }

            // --- Update vision ---
            if (vision != null)
            {
                vision.update();
            }

            // --- Telemetry ---
            telemetry.addLine("=== LAUNCHER RPM TUNER ===");
            telemetry.addData("Target RPM", "%.0f", targetRPM);
            telemetry.addData("Actual RPM", "%.0f", actualRPM);
            telemetry.addData("Motor", motorRunning ? "ON" : "OFF");
            telemetry.addLine();
            telemetry.addData("Controls", "D-pad ↑↓ ±500 | ←→ ±100");
            telemetry.addData("", "Cross = toggle motor | Circle = stop");

            if (vision != null && vision.hasTarget())
            {
                telemetry.addLine();
                telemetry.addLine("=== VISION ===");
                telemetry.addData("Distance (m)", "%.2f", vision.getDistanceToGoalMeters());
                telemetry.addData("Tag ID", vision.getTrackedTagId());
            }

            telemetry.update();
        }

        // Cleanup
        if (motor != null)
        {
            motor.setVelocity(0);
            motor.setPower(0.0);
        }
    }
}
