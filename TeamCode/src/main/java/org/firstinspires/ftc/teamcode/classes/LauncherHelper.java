package org.firstinspires.ftc.teamcode.classes;

/**
 * Lookup-table based launcher RPM calculator.
 * Replace placeholder values with empirically measured {distance_meters, rpm}
 * pairs.
 */
public final class LauncherHelper {
  private LauncherHelper() {}

  /**
   * Empirical lookup table: {distance_meters, rpm}.
   * Fill these in from testing — set launcher to known RPM, measure landing
   * distance.
   */
  private static final double[][] RPM_TABLE = {
      {1.0, 2000}, {1.5, 2800}, {2.0, 3500}, {2.5, 4200}, {3.0, 5000}};

  /**
   * Linearly interpolate RPM from the lookup table. Clamps at boundaries.
   */
  public static double lookupRPM(double distanceMeters) {
    if (distanceMeters <= RPM_TABLE[0][0]) {
      return RPM_TABLE[0][1];
    }
    for (int i = 0; i < RPM_TABLE.length - 1; i++) {
      if (distanceMeters <= RPM_TABLE[i + 1][0]) {
        double d0 = RPM_TABLE[i][0], rpm0 = RPM_TABLE[i][1];
        double d1 = RPM_TABLE[i + 1][0], rpm1 = RPM_TABLE[i + 1][1];
        double t = (distanceMeters - d0) / (d1 - d0);
        return rpm0 + t * (rpm1 - rpm0);
      }
    }
    return RPM_TABLE[RPM_TABLE.length - 1][1];
  }

  /**
   * Get required RPM using vision distance. Returns 0 if no target visible.
   */
  public static double getRequiredRPM(Vision vision) {
    if (vision == null || !vision.hasTarget()) {
      return 0;
    }
    return lookupRPM(vision.getDistanceToGoalMeters());
  }

  /**
   * Check if the launcher is within 98% of the target RPM (ready to fire).
   */
  public static boolean isAtTargetRPM(double currentRPM, double targetRPM) {
    if (targetRPM <= 0)
      return false;
    return currentRPM >= targetRPM * 0.98;
  }
}
