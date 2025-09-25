package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Limelight3A Test", group="Sensor")
public class LimelightTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        Limelight3A limelight;

        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // make sure the camera exists
        assert limelight != null;

        // default pipeline
        int pipelineIndex = 0;
        limelight.pipelineSwitch(pipelineIndex);
        // start polling
        limelight.start();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                telemetry.addData("tx", "%4.2f", result.getTx());
                telemetry.addData("ty", "%4.2f", result.getTy());
                telemetry.addData("ta", "%4.2f", result.getTa());
            } else {
                telemetry.addData("tx", "N/A");
                telemetry.addData("ty", "N/A");
                telemetry.addData("ta", "N/A");
            }

            telemetry.update();

            // small delay to avoid spamming the CPU
            sleep(50);
        }

        // stop limelight polling on exit
        limelight.stop();
    }
}
