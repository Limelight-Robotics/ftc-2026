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

}
