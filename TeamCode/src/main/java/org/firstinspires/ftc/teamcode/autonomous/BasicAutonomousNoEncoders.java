package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.classes.DefaultRobot;

/**
 * Basic time-based autonomous: strafes left ~2 feet, then drives backward ~4 feet.
 *
 * Because there are no encoders, movement distances are controlled by duration.
 * Tune the TIME constants on the actual robot to get accurate distances.
 */
@Autonomous(name = "Basic Autonomous (No Encoders)", group = "1 - Team Code")
public class BasicAutonomousNoEncoders extends LinearOpMode
{
    private final DefaultRobot robot = new DefaultRobot();
    private final ElapsedTime timer = new ElapsedTime();

    // Drive power (0.0 to 1.0). Lower = more controllable but slower.
    private static final double DRIVE_POWER = 0.5;

    // Time in seconds for each movement. Tune these on the real robot.
    private static final double STRAFE_LEFT_TIME_SEC  = 2.0;  // ~2 feet left
    private static final double DRIVE_BACKWARD_TIME_SEC = 4.0; // ~4 feet backward

    @Override
    public void runOpMode()
    {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized - Ready to run");
        telemetry.addData("Plan", "Strafe left ~2 ft, then drive backward ~4 ft");
        telemetry.update();

        waitForStart();

        // Step 1: Strafe left for STRAFE_LEFT_TIME_SEC seconds
        telemetry.addData("Step", "1 - Strafing left");
        telemetry.update();
        driveForTime(0, -DRIVE_POWER, 0, STRAFE_LEFT_TIME_SEC);

        // Step 2: Drive backward for DRIVE_BACKWARD_TIME_SEC seconds
        telemetry.addData("Step", "2 - Driving backward");
        telemetry.update();
        driveForTime(-DRIVE_POWER, 0, 0, DRIVE_BACKWARD_TIME_SEC);

        // Stop and report
        robot.stopMovement();
        telemetry.addData("Status", "Autonomous complete");
        telemetry.update();
    }

    /**
     * Drives at the given power for the specified duration, then stops.
     * Exits early if the OpMode is stopped.
     */
    private void driveForTime(double forward, double strafe, double rotate, double seconds)
    {
        timer.reset();
        robot.drive(forward, strafe, rotate);

        while (opModeIsActive() && timer.seconds() < seconds)
        {
            telemetry.addData("Time", "%.1f / %.1f sec", timer.seconds(), seconds);
            telemetry.update();
            sleep(50);
        }

        robot.stopMovement();
    }
}
