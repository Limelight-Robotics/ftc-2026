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
     * Example stub for fixed launch angle (radians).
     */
    public static double getFixedLaunchAngleRadians() {
        return Math.toRadians(30.0); // 30 degrees
    }

    /**
     * Example stub for launcher wheel radius (meters).
     */
    public static double getLauncherWheelRadiusMeters() {
        // Example: 2 inch wheel
        return 0.0254 * 2.0;
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
