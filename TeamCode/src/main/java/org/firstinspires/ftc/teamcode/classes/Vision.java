package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Vision class to handle all Limelight operations and target tracking
 */
public class Vision {

    /**
     * Enum for Limelight3A pipeline configurations
     */
    public enum Pipeline {
        UNUSED_0(0, "Unused Pipeline 0"),
        YELLOW_DETECTION(1, "2024-2025 Season Yellow Detection"),
        APRIL_TAG(2, "April Tag Detection"),
        UNUSED_3(3, "Unused Pipeline 3"),
        UNUSED_4(4, "Unused Pipeline 4"),
        UNUSED_5(5, "Unused Pipeline 5"),
        UNUSED_6(6, "Unused Pipeline 6"),
        UNUSED_7(7, "Unused Pipeline 7"),
        UNUSED_8(8, "Unused Pipeline 8"),
        UNUSED_9(9, "Unused Pipeline 9");

        private final int index;
        private final String description;

        Pipeline(int index, String description) {
            this.index = index;
            this.description = description;
        }

        public int getIndex() {
            return index;
        }

        public String getDescription() {
            return description;
        }

        public static Pipeline fromIndex(int index) {
            for (Pipeline pipeline : values()) {
                if (pipeline.index == index) {
                    return pipeline;
                }
            }
            return UNUSED_0; // Default fallback
        }
    }

    // Constants
    private static final int AVERAGE_FRAME_COUNT = 90; // Number of frames to average

    // Hardware and tracking variables
    private Limelight3A limelight = null;
    private Pipeline currentPipeline = Pipeline.APRIL_TAG;
    private int consecutiveNoTargetFrames = 0;

    // Position history for averaging
    private final List<Double> xPositionHistory = new ArrayList<>();
    private final List<Double> yPositionHistory = new ArrayList<>();
    private final List<Double> zPositionHistory = new ArrayList<>();
    private int validFrameCount = 0;

    /**
     * Initialize the Limelight hardware
     */
    public void init(HardwareMap hardwareMap, String limelightName, Pipeline initialPipeline) {
        limelight = hardwareMap.get(Limelight3A.class, limelightName);
        currentPipeline = initialPipeline;
        limelight.pipelineSwitch(currentPipeline.getIndex());
    }

    /**
     * Start the Limelight
     */
    public void start() {
        if (limelight != null) {
            limelight.start();
        }
    }

    /**
     * Stop the Limelight
     */
    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }

    /**
     * Get the current pipeline
     */
    public Pipeline getCurrentPipeline() {
        return currentPipeline;
    }

    /**
     * Set the pipeline
     */
    public void setPipeline(Pipeline pipeline) {
        currentPipeline = pipeline;
        if (limelight != null) {
            limelight.pipelineSwitch(currentPipeline.getIndex());
        }
    }

    /**
     * Get the latest Limelight result
     */
    public LLResult getLatestResult() {
        if (limelight != null) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                return result;
            }
        }
        return null;
    }

    /**
     * Process the current frame and update target tracking
     */
    public TargetData processFrame() {
        LLResult result = getLatestResult();
        TargetData targetData = new TargetData();

        if (result != null) {
            consecutiveNoTargetFrames = 0;
            targetData.result = result;
            targetData.botPose = result.getBotpose();

            if (targetData.botPose != null) {
                // Store raw position data
                targetData.rawX = targetData.botPose.getPosition().x;
                targetData.rawY = targetData.botPose.getPosition().y;
                targetData.rawZ = targetData.botPose.getPosition().z;

                // Update position history for averaging
                updatePositionHistory(targetData.rawX, targetData.rawY, targetData.rawZ);

                // Calculate averaged positions
                double[] averagedPosition = getAveragedPosition();
                if (averagedPosition != null) {
                    targetData.xPosition = averagedPosition[0];
                    targetData.yPosition = averagedPosition[1];
                    targetData.zPosition = averagedPosition[2];
                    targetData.isAcquired = true;
                }
            }
        } else {
            consecutiveNoTargetFrames++;
            // Clear history if we haven't seen a target for too long
            if (consecutiveNoTargetFrames >= AVERAGE_FRAME_COUNT) {
                clearPositionHistory();
            }
        }

        targetData.consecutiveNoTargetFrames = consecutiveNoTargetFrames;
        return targetData;
    }

    /**
     * Update position history with new values
     */
    private void updatePositionHistory(double x, double y, double z) {
        xPositionHistory.add(x);
        yPositionHistory.add(y);
        zPositionHistory.add(z);

        // Keep only the last AVERAGE_FRAME_COUNT entries
        if (xPositionHistory.size() > AVERAGE_FRAME_COUNT) {
            xPositionHistory.remove(0);
            yPositionHistory.remove(0);
            zPositionHistory.remove(0);
        }

        validFrameCount++;
    }

    /**
     * Get averaged position from history
     */
    private double[] getAveragedPosition() {
        if (xPositionHistory.isEmpty()) {
            return null;
        }

        double avgX = xPositionHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgY = yPositionHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgZ = zPositionHistory.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        return new double[]{avgX, avgY, avgZ};
    }

    /**
     * Clear position history
     */
    public void clearPositionHistory() {
        xPositionHistory.clear();
        yPositionHistory.clear();
        zPositionHistory.clear();
        validFrameCount = 0;
        consecutiveNoTargetFrames = 0;
    }

    /**
     * Get the number of valid frames processed
     */
    public int getValidFrameCount() {
        return validFrameCount;
    }

    /**
     * Get the maximum number of frames used for averaging
     */
    public int getAverageFrameCount() {
        return AVERAGE_FRAME_COUNT;
    }

    /**
     * Display vision telemetry data
     */
    public void displayTelemetry(Telemetry telemetry, TargetData targetData) {
        if (targetData.result != null) {
            telemetry.addData("Target X Offset (tx)", "%.2f", targetData.result.getTx());
            telemetry.addData("Target Y Offset (ty)", "%.2f", targetData.result.getTy());
            telemetry.addData("Target Area Offset (ta)", "%.2f", targetData.result.getTa());

            // Only display bot pose for April Tag pipeline
            if (targetData.botPose != null && currentPipeline == Pipeline.APRIL_TAG) {
                telemetry.addData("BotPose", targetData.botPose.toString());
                telemetry.addData("Yaw", "%.2f", targetData.botPose.getOrientation().getYaw());
                telemetry.addData("X (Raw)", "%.2f", targetData.rawX);
                telemetry.addData("X (Averaged)", "%.2f", targetData.xPosition);
                telemetry.addData("Y (Raw)", "%.2f", targetData.rawY);
                telemetry.addData("Y (Averaged)", "%.2f", targetData.yPosition);
                telemetry.addData("Z (Raw)", "%.2f", targetData.rawZ);
                telemetry.addData("Z (Averaged)", "%.2f", targetData.zPosition);
            }

            telemetry.addData("Valid Frames", validFrameCount);
        } else {
            telemetry.addData("DEBUG", "No valid target detected");
            telemetry.addData("No Target Frames", targetData.consecutiveNoTargetFrames);
        }

        telemetry.addData("Pipeline", "%s (%d)", currentPipeline.getDescription(), currentPipeline.getIndex());
    }

    /**
     * Handle pipeline switching based on gamepad input
     */
    public void handlePipelineSwitching(boolean incrementPressed, boolean decrementPressed) {
        Pipeline[] pipelines = Pipeline.values();
        int currentIndex = currentPipeline.ordinal();

        if (incrementPressed && currentIndex < pipelines.length - 1) {
            setPipeline(pipelines[currentIndex + 1]);
        } else if (decrementPressed && currentIndex > 0) {
            setPipeline(pipelines[currentIndex - 1]);
        }
    }

    /**
     * Data class to hold all target information
     */
    public static class TargetData {
        public boolean isAcquired = false;
        public double xPosition = 0.0;
        public double yPosition = 0.0;
        public double zPosition = 0.0;
        public double rawX = 0.0;
        public double rawY = 0.0;
        public double rawZ = 0.0;
        public Pose3D botPose = null;
        public LLResult result = null;
        public int consecutiveNoTargetFrames = 0;
    }
}
