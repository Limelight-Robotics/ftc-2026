package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

/**
 * Example autonomous OpMode using RoadRunner trajectories.
 * Demonstrates basic movement patterns for competition.
 */
@Autonomous(name = "RoadRunner Auto Example", group = "Autonomous")
public class RoadRunnerAutoExample extends LinearOpMode {
    private static final Pose2d START_POSE = new Pose2d(0, 0, 0);

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive(hardwareMap, START_POSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        // Example trajectory: drive forward, strafe, and turn
        Actions.runBlocking(
            drive.actionBuilder(START_POSE)
                .lineToX(24)
                .turn(Math.toRadians(90))
                .lineToY(24)
                .splineTo(new Vector2d(48, 48), Math.toRadians(0))
                .build()
        );

        telemetry.addData("Status", "Complete");
        telemetry.update();
    }
}
