package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.MecanumDrive;

/**
 * Red alliance right starting position autonomous.
 * Customize trajectory based on game objectives.
 */
@Autonomous(name = "Red Right Auto", group = "Autonomous")
public class RedRightAuto extends LinearOpMode
{
    private static final Pose2d START_POSE = new Pose2d(36, -60, Math.toRadians(90));

    @Override public void runOpMode()
    {
        MecanumDrive drive = new MecanumDrive(hardwareMap, START_POSE);

        telemetry.addData("Status", "Red Right - Ready");
        telemetry.update();

        waitForStart();

        if (isStopRequested())
            return;

        runAutonomousPath(drive);

        telemetry.addData("Status", "Complete");
        telemetry.update();
    }

    private void runAutonomousPath(MecanumDrive drive)
    {
        // TODO(zanderlewis): Real pathing
        Actions.runBlocking(drive.actionBuilder(START_POSE)
                .lineToY(-36)
                .turn(Math.toRadians(90))
                .lineToX(48)
                .build());
    }
}
