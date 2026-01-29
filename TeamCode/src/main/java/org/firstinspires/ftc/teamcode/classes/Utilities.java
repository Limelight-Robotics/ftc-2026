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

    /**
     * Calculate required launch velocity using projectile motion physics.
     * Formula: v = sqrt(g * d² / (2 * cos²(θ) * (d * tan(θ) - h)))
     *
     * @param distance Horizontal distance to target in meters
     * @param height Vertical offset to target in meters (positive = target above launcher)
     * @param angle Launch angle in radians
     * @return Required launch velocity in m/s, or 0 if shot is impossible
     */
    public static double calculateLaunchVelocity(double distance, double height, double angle) {
        final double GRAVITY = 9.81; // m/s²

        double cosAngle = Math.cos(angle);
        double tanAngle = Math.tan(angle);

        // Denominator: 2 * cos²(θ) * (d * tan(θ) - h)
        double denominator = 2 * cosAngle * cosAngle * (distance * tanAngle - height);

        // If denominator <= 0, the shot is impossible (target too high for angle/distance)
        if (denominator <= 0) {
            return 0;
        }

        // v = sqrt(g * d² / denominator)
        double velocitySquared = GRAVITY * distance * distance / denominator;
        return Math.sqrt(velocitySquared);
    }
}
