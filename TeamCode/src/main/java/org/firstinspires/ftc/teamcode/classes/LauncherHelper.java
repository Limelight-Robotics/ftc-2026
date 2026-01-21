package org.firstinspires.ftc.teamcode.classes;

/**
 * Helper for launcher logic: distance, velocity, RPM calculation.
 *
 * Two approaches for determining launch velocity:
 *
 * 1. PHYSICS-BASED (current): Uses projectile motion formula with a tunable
 *    fudge factor. Good starting point, works across all distances, but assumes
 *    ideal conditions.
 *
 * 2. LOOKUP TABLE (alternative): If physics model is consistently inaccurate,
 *    replace with empirically measured values:
 *
 *    private static final double[][] RPM_LOOKUP = {
 *        // {distance_meters, measured_rpm}
 *        {1.0, 2000},
 *        {1.5, 2800},
 *        {2.0, 3500},
 *        {2.5, 4200},
 *        {3.0, 5000}
 *    };
 *
 *    public static double lookupRPM(double distance) {
 *        // Find two closest entries and linearly interpolate
 *        for (int i = 0; i < RPM_LOOKUP.length - 1; i++) {
 *            if (distance <= RPM_LOOKUP[i + 1][0]) {
 *                double d0 = RPM_LOOKUP[i][0], rpm0 = RPM_LOOKUP[i][1];
 *                double d1 = RPM_LOOKUP[i + 1][0], rpm1 = RPM_LOOKUP[i + 1][1];
 *                double t = (distance - d0) / (d1 - d0);
 *                return rpm0 + t * (rpm1 - rpm0);
 *            }
 *        }
 *        return RPM_LOOKUP[RPM_LOOKUP.length - 1][1]; // clamp to max
 *    }
 *
 *    To build the lookup table:
 *    1. Set launcher to known RPM, measure where ball lands
 *    2. Repeat at 4-5 distances across your expected range
 *    3. Record distance → RPM pairs
 *    4. Adjust for height difference if goal height varies
 */
public final class LauncherHelper {

    private LauncherHelper() {}

    public static final double LAUNCHER_WHEEL_RADIUS = 0.0254 * 1.0; // 1 inch in meters

    /**
     * Fudge factor to account for real-world inefficiencies:
     * - Wheel slip on ball contact
     * - Air resistance
     * - Ball compression energy loss
     *
     * Tune this value empirically:
     * - If shots fall short, INCREASE this value
     * - If shots overshoot, DECREASE this value
     */
    private static final double VELOCITY_FUDGE_FACTOR = 1.0;

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
        return LAUNCHER_WHEEL_RADIUS;
    }

    /**
     * Calculate the required launcher wheel velocity to hit a target at a given distance
     * and height, using a fixed launch angle.
     *
     * @param distance Horizontal distance to the goal (in meters)
     * @param height Vertical difference between launcher and goal (in meters)
     * @param launchAngle Launch angle in radians
     * @return Required initial velocity (m/s)
     */
    private static double calculateLaunchVelocity(double distance, double height, double launchAngle) {
        // g = gravity (m/s^2)
        final double g = 9.820302;
        double cosA = Math.cos(launchAngle);
        double sinA = Math.sin(launchAngle);
        double numerator = g * Math.pow(distance, 2);
        double denominator = 2 * Math.pow(cosA, 2) * (distance * Math.tan(launchAngle) - height);
        if (denominator <= 0) return 0; // impossible shot
        double v = Math.sqrt(numerator / denominator);
        return v;
    }

    /**
     * Calculate required RPM for launcher wheel to hit goal using robot's vision.
     * Uses physics-based projectile motion with fudge factor compensation.
     */
    public static double calculateRequiredRPM(org.firstinspires.ftc.teamcode.classes.DefaultRobot robot) {
        double distance = getDistanceToGoalMeters(robot);
        double height = getHeightToGoalMeters(robot);
        double angle = getFixedLaunchAngleRadians();
        double velocity = calculateLaunchVelocity(distance, height, angle);
        velocity *= VELOCITY_FUDGE_FACTOR; // Apply real-world correction
        double radius = getLauncherWheelRadiusMeters();
        return Utilities.velocityToRPM(velocity, radius);
    }
}
