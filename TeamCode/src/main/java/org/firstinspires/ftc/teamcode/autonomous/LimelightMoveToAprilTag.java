package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.classes.Robot;
import org.firstinspires.ftc.teamcode.classes.Vision;

@Autonomous(name="Limelight3A Move to April Tag", group="April Tag")
public class LimelightMoveToAprilTag extends OpMode {

    // Robot and vision systems
    private final Robot robot = new Robot();
    private final Vision vision = new Vision();

    @Override
    public void init() {
        robot.init(hardwareMap, vision);
    }

    @Override
    public void start() {
        vision.start();
        vision.clearPositionHistory();
    }

    @Override
    public void loop() {
        Vision.TargetData targetData = vision.processFrame();

        // Display vision telemetry
        vision.displayTelemetry(telemetry, targetData);

        // Use robot's movement logic
        if (targetData.isAcquired) {
            Robot.MovementResult movementResult = robot.moveToAprilTag(targetData);
        } else {
            robot.stopMovement();
        }

        // Display robot telemetry
        robot.displayTelemetry(telemetry);
    }

    @Override
    public void stop() {
        vision.stop();
        robot.stopMovement();
    }
}
