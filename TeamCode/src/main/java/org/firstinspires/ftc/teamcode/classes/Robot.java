package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Robot interface declares high-level robot operations.
 * Implementations should be lightweight facades that orchestrate subsystems.
 */
public interface Robot {
    void init(HardwareMap hardwareMap);

    void init(HardwareMap hardwareMap, Vision vision);

    void drive(double axial, double lateral, double yaw);

    void driveWithGamepad(double axial, double lateral, double yaw);

    void driveAutonomous(double axial, double lateral, double yaw);

    org.firstinspires.ftc.teamcode.classes.robot.MovementResult moveToAprilTag(Vision.TargetData targetData);

    void stopMovement();

    /**
     * Return a read-only status view of the robot (motor powers, last inputs, last
     * vision target).
     */
    org.firstinspires.ftc.teamcode.classes.robot.RobotStatus getStatus();

    /**
     * Cycle the drive motor direction preset by the specified delta.
     * Positive values advance, negative values go backwards.
     */
    void cycleDriveDirectionPreset(int delta);

    /**
     * Return the currently selected drive direction preset index (0-based).
     */
    int getDriveDirectionPreset();

    /**
     * Return a human-readable description of the current drive direction preset
     * and per-motor directions (e.g. "DEFAULT: FL=REV FR=FWD BL=REV BR=FWD").
     */
    String getDriveDirectionString();

    /**
     * Factory helper so callers can obtain the default implementation
     * without importing or referencing the concrete class.
     */
    static Robot createDefault() {
        return new org.firstinspires.ftc.teamcode.classes.robot.DefaultRobot();
    }
}