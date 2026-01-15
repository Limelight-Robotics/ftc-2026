package org.firstinspires.ftc.teamcode.classes;

/**
 * Utility methods for common robot operations.
 */
public final class Utilities {
    private Utilities() {
    }

    /**
     * Normalize mecanum motor powers so no element exceeds magnitude 1.0.
     * 
     * @return array with order: {frontLeft, frontRight, backLeft, backRight}
     */
    public static double[] normalizeMecanum(double fl, double fr, double bl, double br) {
        double max = Math.max(Math.abs(fl), Math.abs(fr));
        max = Math.max(max, Math.abs(bl));
        max = Math.max(max, Math.abs(br));
        if (max <= 1.0) {
            return new double[] { fl, fr, bl, br };
        }
        return new double[] { fl / max, fr / max, bl / max, br / max };
    }

    /**
     * Clamp a value between -1.0 and 1.0.
     * 
     * @return clamped value
     */
    public static double clamp(double value) {
        if (value > 1.0) return 1.0;
        if (value < -1.0) return -1.0;
        return value;
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
    public static double calculateLaunchVelocity(double distance, double height, double launchAngle) {
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
     * Convert linear velocity (m/s) to launcher wheel RPM.
     * @param velocity Linear velocity in m/s
     * @param wheelRadius Radius of launcher wheel in meters
     * @return Required RPM for the wheel
     */
    public static double velocityToRPM(double velocity, double wheelRadius) {
        // v = omega * r, omega in rad/s
        double omega = velocity / wheelRadius;
        double rpm = omega * 60 / (2 * Math.PI);
        return rpm;
    }
}
