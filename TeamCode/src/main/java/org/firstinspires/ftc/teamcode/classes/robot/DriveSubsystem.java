package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.classes.Utilities;

/**
 * DriveSubsystem encapsulates mecanum motor hardware and basic drive
 * operations.
 */
public class DriveSubsystem {
    private DcMotor frontLeftDrive;
    private DcMotor backLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backRightDrive;

    // Hardware names
    private static final String MOTOR_FRONT_LEFT = "fL";
    private static final String MOTOR_BACK_LEFT = "bL";
    private static final String MOTOR_FRONT_RIGHT = "fR";
    private static final String MOTOR_BACK_RIGHT = "bR";

    private final DriveDirectionPresets directionPresets = new DriveDirectionPresets();

    private double lastFrontLeftPower = 0.0;
    private double lastFrontRightPower = 0.0;
    private double lastBackLeftPower = 0.0;
    private double lastBackRightPower = 0.0;

    // Direction presets are managed by DriveDirectionPresets
    public void init(HardwareMap hardwareMap) {
        if (hardwareMap == null)
            throw new IllegalArgumentException("hardwareMap cannot be null");
        frontLeftDrive = hardwareMap.get(DcMotor.class, MOTOR_FRONT_LEFT);
        backLeftDrive = hardwareMap.get(DcMotor.class, MOTOR_BACK_LEFT);
        frontRightDrive = hardwareMap.get(DcMotor.class, MOTOR_FRONT_RIGHT);
        backRightDrive = hardwareMap.get(DcMotor.class, MOTOR_BACK_RIGHT);
        if (frontLeftDrive == null || backLeftDrive == null || frontRightDrive == null || backRightDrive == null) {
            throw new IllegalStateException("Missing drive motor(s) in hardware map");
        }

        // Apply the configured direction preset (per-motor)
        directionPresets.applyTo(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);
    }

    /**
     * Cycle the preset by an integer delta (positive forward, negative backward).
     */
    public void cycleDriveDirectionPreset(int shift) {
        int cur = directionPresets.getPreset();
        int next = (cur + shift) & 0xF;
        directionPresets.setPreset(next);
        directionPresets.applyTo(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);
    }

    public int getDirectionPreset() {
        return directionPresets.getPreset();
    }

    public String getDirectionString() {
        return directionPresets.getPresetString();
    }

    public void drive(double axial, double lateral, double yaw) {
        double fl = axial + lateral + yaw;
        double fr = axial - lateral - yaw;
        double bl = axial - lateral + yaw;
        double br = axial + lateral - yaw;
        double[] normalized = Utilities.normalizeMecanum(fl, fr, bl, br);
        setMotorPowers(normalized);
    }

    public void stop() {
        setMotorPowers(new double[] { 0.0, 0.0, 0.0, 0.0 });
    }

    private void setMotorPowers(double[] powers) {
        if (powers == null || powers.length < 4)
            return;
        double fl = powers[0], fr = powers[1], bl = powers[2], br = powers[3];
        if (frontLeftDrive != null)
            frontLeftDrive.setPower(fl);
        if (frontRightDrive != null)
            frontRightDrive.setPower(fr);
        if (backLeftDrive != null)
            backLeftDrive.setPower(bl);
        if (backRightDrive != null)
            backRightDrive.setPower(br);
        updateTelemetryPowers();
    }

    public void updateTelemetryPowers() {
        lastFrontLeftPower = frontLeftDrive != null ? frontLeftDrive.getPower() : 0.0;
        lastFrontRightPower = frontRightDrive != null ? frontRightDrive.getPower() : 0.0;
        lastBackLeftPower = backLeftDrive != null ? backLeftDrive.getPower() : 0.0;
        lastBackRightPower = backRightDrive != null ? backRightDrive.getPower() : 0.0;
    }

    public double getFrontLeftPower() {
        return lastFrontLeftPower;
    }

    public double getFrontRightPower() {
        return lastFrontRightPower;
    }

    public double getBackLeftPower() {
        return lastBackLeftPower;
    }

    public double getBackRightPower() {
        return lastBackRightPower;
    }
}
