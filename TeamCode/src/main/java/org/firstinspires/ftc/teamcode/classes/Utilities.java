package org.firstinspires.ftc.teamcode.classes;

public final class Utilities {
    private Utilities() {
    }

    /**
     * Normalize mecanum motor powers so that no element exceeds magnitude 1.0.
     * Returns a new array with order: {frontLeft, frontRight, backLeft, backRight}.
     */
    public static double[] normalizeMecanum(double fl, double fr, double bl, double br) {
        double max = Math.max(Math.abs(fl), Math.abs(fr));
        max = Math.max(max, Math.abs(bl));
        max = Math.max(max, Math.abs(br));
        if (max <= 1.0)
            return new double[] { fl, fr, bl, br };
        return new double[] {
                fl / max,
                fr / max,
                bl / max,
                br / max
        };
    }
}
