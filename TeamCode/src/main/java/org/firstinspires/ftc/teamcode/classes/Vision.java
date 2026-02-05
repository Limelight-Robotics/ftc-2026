package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import java.util.List;
import java.util.Locale;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

/**
 * Vision subsystem for Limelight3A and AprilTag integration.
 * Provides distance and height to goal for trajectory math.
 */
public class Vision
{
    private Limelight3A limelight;
    private int         targetTagId = -1; // -1 means track any visible tag

    // Cached data from last valid detection (all updated together in update())
    private double  lastDistanceMeters = 0;
    private double  lastHeightMeters   = 0;
    private double  lastTx             = 0;
    private double  lastTy             = 0;
    private int     lastTrackedTagId   = -1;
    private boolean hasValidTarget     = false;

    public Vision(HardwareMap hardwareMap)
    {
        try
        {
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limelight.setPollRateHz(100);
            limelight.pipelineSwitch(0); // Default AprilTag pipeline
            limelight.start();
        }
        catch (Exception e)
        {
            limelight = null;
        }
    }

    /**
     * Set the specific AprilTag ID to track.
     * @param tagId The AprilTag ID to track, or -1 to track any visible tag
     */
    public void setTargetTagId(int tagId) { this.targetTagId = tagId; }

    /**
     * Update vision data. Call this in your loop.
     */
    public void update()
    {
        if (limelight == null)
        {
            hasValidTarget = false;
            return;
        }

        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid())
        {
            hasValidTarget = false;
            return;
        }

        // Cache raw angle offsets
        lastTx = result.getTx();
        lastTy = result.getTy();

        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
        if (fiducials.isEmpty())
        {
            hasValidTarget = false;
            return;
        }

        // Find the target tag (or use first visible if targetTagId is -1)
        LLResultTypes.FiducialResult targetFiducial = null;
        for (LLResultTypes.FiducialResult fiducial : fiducials)
        {
            if (targetTagId == -1 || fiducial.getFiducialId() == targetTagId)
            {
                targetFiducial = fiducial;
                break;
            }
        }

        if (targetFiducial == null)
        {
            hasValidTarget = false;
            return;
        }

        lastTrackedTagId = targetFiducial.getFiducialId();

        // Get robot pose relative to the tag
        Pose3D robotPoseTargetSpace = targetFiducial.getRobotPoseTargetSpace();
        if (robotPoseTargetSpace != null)
        {
            Position pos = robotPoseTargetSpace.getPosition();
            // X and Z form the horizontal plane, Y is vertical
            lastDistanceMeters = Math.sqrt(pos.x * pos.x + pos.z * pos.z);
            lastHeightMeters   = pos.y;
            hasValidTarget     = true;
        }
        else
        {
            hasValidTarget = false;
        }
    }

    /**
     * Check if we currently have a valid target lock.
     * @return true if a valid AprilTag is being tracked
     */
    public boolean hasTarget() { return hasValidTarget; }

    /**
     * Get the ID of the currently tracked tag.
     * @return tag ID, or -1 if no target
     */
    public int getTrackedTagId() { return hasValidTarget ? lastTrackedTagId : -1; }

    /**
     * Get horizontal distance to goal (meters).
     *
     * @return distance in meters, or 0 if no valid target
     */
    public double getDistanceToGoalMeters() { return lastDistanceMeters; }

    /**
     * Get vertical difference to goal (meters).
     * Positive means goal is above camera, negative means below.
     *
     * @return height difference in meters, or 0 if no valid target
     */
    public double getHeightToGoalMeters() { return lastHeightMeters; }

    /**
     * Get the raw tx (horizontal angle offset) from Limelight.
     * Useful for aiming the turret.
     *
     * @return horizontal offset in degrees (-29.8 to 29.8)
     */
    public double getTx() { return lastTx; }

    /**
     * Get the raw ty (vertical angle offset) from Limelight.
     *
     * @return vertical offset in degrees (-24.85 to 24.85)
     */
    public double getTy() { return lastTy; }

    /**
     * Switch the Limelight pipeline.
     * @param pipeline Pipeline number (0-9)
     */
    public void setPipeline(int pipeline)
    {
        if (limelight != null)
        {
            limelight.pipelineSwitch(pipeline);
        }
    }

    /**
     * Check if Limelight hardware is connected.
     * @return true if Limelight is available
     */
    public boolean isConnected() { return limelight != null && limelight.isConnected(); }

    /**
     * Get a status string for telemetry display.
     * @return formatted status string with connection, target, and position info
     */
    public String getStatusString()
    {
        if (limelight == null)
        {
            return "Limelight: Not connected";
        }
        if (!hasValidTarget)
        {
            return "Limelight: No target";
        }
        return String.format(Locale.US, "Tag %d | Dist: %.2fm | Height: %.2fm | tx: %.1f°",
            lastTrackedTagId, lastDistanceMeters, lastHeightMeters, lastTx);
    }
}
