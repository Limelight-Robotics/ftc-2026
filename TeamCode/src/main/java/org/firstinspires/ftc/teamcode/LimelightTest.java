package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name="Limelight3A Test", group="Sensor")
public class LimelightTest extends OpMode {

    private Limelight3A limelight;

    @Override
    public void init() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        if (limelight == null) {
            telemetry.addData("Error", "Limelight not found in hardwareMap (name: 'limelight')");
            telemetry.update();
            return;
        }

        // set default pipeline
        int pipelineIndex = 0;
        limelight.pipelineSwitch(pipelineIndex);

        telemetry.addData("Status", "Initialized");
        telemetry.addData("Pipeline", pipelineIndex);
        telemetry.update();
    }

    @Override
    public void start() {
        if (limelight != null) {
            limelight.start();
        }
    }

    @Override
    public void loop() {
        if (limelight == null) {
            telemetry.addData("Error", "Limelight missing");
            telemetry.update();
            return;
        }

        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            telemetry.addData("Target X Offset (tx)", "%4.2f", result.getTx());
            telemetry.addData("Target Y Offset (ty)", "%4.2f", result.getTy());
            telemetry.addData("Target Area Offset (ta)", "%4.2f", result.getTa());
        } else if (result == null) {
            telemetry.addData("DEBUG", "Result is null");
        } else {
            telemetry.addData("DEBUG", "Result is invalid");
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }
}
