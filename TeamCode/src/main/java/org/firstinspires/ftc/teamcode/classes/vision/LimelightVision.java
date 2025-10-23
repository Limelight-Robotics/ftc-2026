package org.firstinspires.ftc.teamcode.classes.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.classes.Vision;

/**
 * LimelightVision: Base class to interact with Limelight3A camera hardware.
 */
public class LimelightVision implements Vision {
    private static final String DEFAULT_LIMELIGHT_NAME = "limelight";
    private Limelight3A limelight;
    private Pipeline currentPipeline = Pipeline.APRIL_TAG;
    private int consecutiveNoTargetFrames = 0;
    private Vision.TargetData lastTargetData = null;
    private final VisionStatusImpl status = new VisionStatusImpl();

    @Override
    public void init(HardwareMap hardwareMap, String limelightName, Pipeline initialPipeline) {
        if (hardwareMap == null)
            throw new IllegalArgumentException("hardwareMap is required");
        String name = (limelightName == null) ? DEFAULT_LIMELIGHT_NAME : limelightName;
        limelight = hardwareMap.get(Limelight3A.class, name);
        currentPipeline = (initialPipeline == null) ? Pipeline.APRIL_TAG : initialPipeline;
        if (limelight != null)
            limelight.pipelineSwitch(currentPipeline.ordinal());
    }

    @Override
    public void start() {
        if (limelight != null)
            limelight.start();
    }

    @Override
    public void stop() {
        if (limelight != null)
            limelight.stop();
    }

    @Override
    public Pipeline getCurrentPipeline() {
        return currentPipeline;
    }

    @Override
    public void setPipeline(Pipeline pipeline) {
        if (pipeline == null)
            return;
        currentPipeline = pipeline;
        if (limelight != null)
            limelight.pipelineSwitch(currentPipeline.ordinal());
    }

    @Override
    public LLResult getLatestResult() {
        if (limelight == null)
            return null;
        LLResult r = limelight.getLatestResult();
        return (r != null && r.isValid()) ? r : null;
    }

    @Override
    public TargetData processFrame() {
        LLResult result = getLatestResult();
        TargetData data = new TargetData();
        if (result == null) {
            consecutiveNoTargetFrames++;
            data.consecutiveNoTargetFrames = consecutiveNoTargetFrames;
            lastTargetData = data;
            return data;
        }
        consecutiveNoTargetFrames = 0;
        data.result = result;
        fillPoseData(result.getBotpose(), data);
        data.consecutiveNoTargetFrames = consecutiveNoTargetFrames;
        lastTargetData = data;
        return data;
    }

    private void fillPoseData(Pose3D pose, TargetData data) {
        if (pose == null)
            return;
        data.botPose = pose;
        data.rawX = pose.getPosition().x;
        data.rawY = pose.getPosition().y;
        data.rawZ = pose.getPosition().z;
        data.xPosition = data.rawX;
        data.yPosition = data.rawY;
        data.zPosition = data.rawZ;
        data.isAcquired = true;
    }

    @Override
    public void handlePipelineSwitching(boolean incrementPressed, boolean decrementPressed) {
        Vision.Pipeline[] list = Vision.Pipeline.values();
        int idx = currentPipeline.ordinal();
        if (incrementPressed && idx < list.length - 1)
            setPipeline(list[idx + 1]);
        else if (decrementPressed && idx > 0)
            setPipeline(list[idx - 1]);
    }

    @Override
    public org.firstinspires.ftc.teamcode.classes.vision.VisionStatus getStatus() {
        return status;
    }

    private class VisionStatusImpl implements org.firstinspires.ftc.teamcode.classes.vision.VisionStatus {
        @Override
        public Vision.Pipeline getCurrentPipeline() {
            return currentPipeline;
        }

        @Override
        public LLResult getLatestResult() {
            return LimelightVision.this.getLatestResult();
        }

        @Override
        public Vision.TargetData getLastTargetData() {
            return lastTargetData;
        }

        @Override
        public int getConsecutiveNoTargetFrames() {
            return consecutiveNoTargetFrames;
        }
    }
}
