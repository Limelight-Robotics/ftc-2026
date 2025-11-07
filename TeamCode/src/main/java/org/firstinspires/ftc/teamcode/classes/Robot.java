package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Robot interface declares high-level robot operations.
 * Implementations should be lightweight facades that orchestrate subsystems.
 */
public interface Robot {
    void init(HardwareMap hardwareMap);

    void init(HardwareMap hardwareMap, Vision vision);

    void drive(double forward, double strafe, double rotate);

    void driveWithGamepad(double forward, double strafe, double rotate);

    void driveAutonomous(double forward, double strafe, double rotate);

    org.firstinspires.ftc.teamcode.classes.robot.MovementResult moveToAprilTag(Vision.TargetData targetData);

    void stopMovement();

    org.firstinspires.ftc.teamcode.classes.robot.RobotStatus getStatus();

    void cycleDriveDirectionPreset(int delta);

    int getDriveDirectionPreset();

    String getDriveDirectionString();

    static Robot createDefault() {
        return new org.firstinspires.ftc.teamcode.classes.robot.DefaultRobot();
    }

    void setIntakePower(double power);
}