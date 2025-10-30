package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

/**
 * Vision interface: small contract used by OpModes and Robot implementations.
 * The concrete implementation lives in the classes.vision subpackage.
 */
public interface Vision {
    enum Pipeline {
        UNUSED_0, YELLOW_DETECTION, APRIL_TAG, UNUSED_3, UNUSED_4, UNUSED_5, UNUSED_6, UNUSED_7, UNUSED_8, UNUSED_9
    }

    void init(HardwareMap hardwareMap, String limelightName, Pipeline initialPipeline);

    void start();

    void stop();

    Pipeline getCurrentPipeline();

    void setPipeline(Pipeline pipeline);

    LLResult getLatestResult();

    TargetData processFrame();

    void handlePipelineSwitching(boolean incrementPressed, boolean decrementPressed);

    /** Return a read-only status view for telemetry and debugging. */
    org.firstinspires.ftc.teamcode.classes.vision.VisionStatus getStatus();

    class TargetData {
        public boolean isAcquired = false;
        public double xPosition = 0.0, yPosition = 0.0, zPosition = 0.0;
        public double rawX = 0.0, rawY = 0.0, rawZ = 0.0;
        public Pose3D botPose = null;
        public LLResult result = null;
        public int consecutiveNoTargetFrames = 0;
    }

    static Vision createDefault() {
        return new org.firstinspires.ftc.teamcode.classes.vision.LimelightVision();
    }
}
