package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import java.util.List;

@TeleOp(name = "Limelight Diagnostic", group = "Diagnostic")
public class LimelightDiagnostic extends LinearOpMode {

    @Override
    public void runOpMode() {
        Limelight3A limelight = null;
        try {
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limelight.setPollRateHz(100);
            limelight.pipelineSwitch(0);
            limelight.start();
            telemetry.addData("Init", "Limelight initialized OK");
        } catch (Exception e) {
            telemetry.addData("Init", "FAILED: " + e.getMessage());
        }
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (limelight == null) {
                telemetry.addData("ERROR", "Limelight not found in hardwareMap");
                telemetry.update();
                sleep(500);
                continue;
            }

            // Status info
            LLStatus status = limelight.getStatus();
            telemetry.addLine("=== STATUS ===");
            if (status != null) {
                telemetry.addData("Pipeline", status.getPipelineIndex());
                telemetry.addData("Pipeline Type", status.getPipelineType());
                telemetry.addData("Temp (C)", "%.1f", status.getTemp());
                telemetry.addData("FPS", "%.0f", status.getFps());
            } else {
                telemetry.addData("Status", "null");
            }

            // Result info
            LLResult result = limelight.getLatestResult();
            telemetry.addLine("=== RESULT ===");
            if (result == null) {
                telemetry.addData("Result", "null");
            } else {
                telemetry.addData("Valid", result.isValid());
                telemetry.addData("tx", "%.2f", result.getTx());
                telemetry.addData("ty", "%.2f", result.getTy());
                telemetry.addData("ta", "%.2f", result.getTa());
                telemetry.addData("Staleness (ms)", "%d", result.getStaleness());
                telemetry.addData("Pipeline Index", result.getPipelineIndex());

                // Botpose
                Pose3D botpose = result.getBotpose();
                if (botpose != null) {
                    Position pos = botpose.getPosition();
                    telemetry.addData("Botpose", "(%.2f, %.2f, %.2f)", pos.x, pos.y, pos.z);
                } else {
                    telemetry.addData("Botpose", "null");
                }

                // Fiducials (AprilTags)
                List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
                telemetry.addLine("=== APRILTAGS ===");
                telemetry.addData("Tags found", fiducials.size());
                for (LLResultTypes.FiducialResult f : fiducials) {
                    telemetry.addData("  Tag " + f.getFiducialId(),
                            "tx=%.1f ty=%.1f ta=%.1f",
                            f.getTargetXDegrees(), f.getTargetYDegrees(), f.getTargetArea());
                    Pose3D rp = f.getRobotPoseTargetSpace();
                    if (rp != null) {
                        Position p = rp.getPosition();
                        telemetry.addData("    RobotPose", "(%.2f, %.2f, %.2f)", p.x, p.y, p.z);
                    }
                }

                // Color results
                List<LLResultTypes.ColorResult> colors = result.getColorResults();
                if (!colors.isEmpty()) {
                    telemetry.addLine("=== COLOR ===");
                    telemetry.addData("Color targets", colors.size());
                    for (LLResultTypes.ColorResult c : colors) {
                        telemetry.addData("  Color", "tx=%.1f ty=%.1f area=%.1f",
                                c.getTargetXDegrees(), c.getTargetYDegrees(), c.getTargetArea());
                    }
                }

                // Detector results
                List<LLResultTypes.DetectorResult> detectors = result.getDetectorResults();
                if (!detectors.isEmpty()) {
                    telemetry.addLine("=== DETECTOR ===");
                    telemetry.addData("Detections", detectors.size());
                    for (LLResultTypes.DetectorResult d : detectors) {
                        telemetry.addData("  " + d.getClassName(),
                                "tx=%.1f ty=%.1f conf=%.2f",
                                d.getTargetXDegrees(), d.getTargetYDegrees(), d.getConfidence());
                    }
                }

                // Barcode results
                List<LLResultTypes.BarcodeResult> barcodes = result.getBarcodeResults();
                if (!barcodes.isEmpty()) {
                    telemetry.addLine("=== BARCODE ===");
                    for (LLResultTypes.BarcodeResult b : barcodes) {
                        telemetry.addData("  Barcode", b.getData());
                    }
                }
            }

            telemetry.update();
        }

        if (limelight != null) {
            limelight.stop();
        }
    }
}
