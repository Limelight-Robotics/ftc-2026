package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Vision subsystem for Limelight3A and AprilTag integration.
 * Provides distance and height to goal for trajectory math.
 */
public class Vision {
    private final HardwareMap hardwareMap;

    public Vision(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    /**
     * Get horizontal distance to goal (meters).
     * 
     * @return distance in meters
     */
    public double getDistanceToGoalMeters() {
        return 0;
    }

    /**
     * Get vertical difference to goal (meters).
     * 
     * @return height difference in meters
     */
    public double getHeightToGoalMeters() {
        return 0;
    }
}
