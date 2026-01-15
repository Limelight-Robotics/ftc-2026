package org.firstinspires.ftc.teamcode.teleop;

import org.firstinspires.ftc.teamcode.classes.Utilities;

/**
 * Helper for launcher logic: distance, velocity, RPM calculation.
 */
public final class LauncherHelper {
    private LauncherHelper() {}

    /**
     * Get distance to goal using DefaultRobot's Vision subsystem.
     */
    public static double getDistanceToGoalMeters(org.firstinspires.ftc.teamcode.classes.DefaultRobot robot) {
        if (robot == null || robot.getVision() == null) return 1.0;
        return robot.getVision().getDistanceToGoalMeters();
    }

    /**
     * Get height to goal using DefaultRobot's Vision subsystem.
     */
    public static double getHeightToGoalMeters(org.firstinspires.ftc.teamcode.classes.DefaultRobot robot) {
        if (robot == null || robot.getVision() == null) return 1.0;
        return robot.getVision().getHeightToGoalMeters();
    }

    /**
     * Launch angle in radians (fixed).
     */
    public static double getFixedLaunchAngleRadians() {
        return Math.toRadians(30.0); // 30 degrees
    }

    /**
     * Launcher wheel radius in meters.
     */
    public static double getLauncherWheelRadiusMeters() {
        return 0.0254 * 1.0; // 1 inch in meters
    }

    /**
     * Calculate required RPM for launcher wheel to hit goal using robot's vision.
     */
    public static double calculateRequiredRPM(org.firstinspires.ftc.teamcode.classes.DefaultRobot robot) {
        double distance = getDistanceToGoalMeters(robot);
        double height = getHeightToGoalMeters(robot);
        double angle = getFixedLaunchAngleRadians();
        double velocity = Utilities.calculateLaunchVelocity(distance, height, angle);
        double radius = getLauncherWheelRadiusMeters();
        return Utilities.velocityToRPM(velocity, radius);
    }
}
