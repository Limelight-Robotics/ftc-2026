package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name="Limelight3A Test", group="Sensor")
public class LimelightTest extends OpMode {

    private final Robot robot = new Robot();

    @Override
    public void init() {
        // Initialize the robot hardware
        robot.init(hardwareMap);

        // Initialize the limelight with default pipeline
        int pipelineIndex = 1; // FTC 2024-2025 Season Yellow Detection
        robot.initLimelight(hardwareMap, "limelight", pipelineIndex);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Pipeline", robot.getLimelightPipelineIndex());
        telemetry.update();
    }

    @Override
    public void start() {
        robot.startLimelight();
    }

    @Override
    public void loop() {
        LLResult result = robot.getLimelightResult();
        if (result != null) {
            telemetry.addData("Target X Offset (tx)", "%4.2f", result.getTx());
            telemetry.addData("Target Y Offset (ty)", "%4.2f", result.getTy());
            telemetry.addData("Target Area Offset (ta)", "%4.2f", result.getTa());
        } else {
            telemetry.addData("DEBUG", "No valid target detected");
        }

        telemetry.addData("Pipeline", robot.getLimelightPipelineIndex());
        telemetry.update();
    }

    @Override
    public void stop() {
        robot.stopLimelight();
    }
}
