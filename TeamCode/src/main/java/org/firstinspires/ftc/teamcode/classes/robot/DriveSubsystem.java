package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.classes.Utilities;

/**
 * DriveSubsystem encapsulates mecanum motor hardware and basic drive operations.
 */
public class DriveSubsystem {
    private static final String MOTOR_FL = "leftFront";
    private static final String MOTOR_BL = "leftBack";
    private static final String MOTOR_FR = "rightFront";
    private static final String MOTOR_BR = "rightBack";

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;

    private final DriveDirectionPresets directionPresets = new DriveDirectionPresets();
    private double lastFLPower, lastFRPower, lastBLPower, lastBRPower;

    /** Initialize motors from hardware map. */
    public void init(HardwareMap hardwareMap) {
        if (hardwareMap == null) {
            throw new IllegalArgumentException("hardwareMap cannot be null");
        }
        frontLeft = hardwareMap.get(DcMotor.class, MOTOR_FL);
        backLeft = hardwareMap.get(DcMotor.class, MOTOR_BL);
        frontRight = hardwareMap.get(DcMotor.class, MOTOR_FR);
        backRight = hardwareMap.get(DcMotor.class, MOTOR_BR);

        validateMotors();
        directionPresets.applyTo(frontLeft, frontRight, backLeft, backRight);
    }

    private void validateMotors() {
        if (frontLeft == null || backLeft == null || frontRight == null || backRight == null) {
            throw new IllegalStateException("Missing drive motor(s) in hardware map");
        }
    }

    /** Drive with mecanum kinematics. */
    public void drive(double forward, double strafe, double rotate) {
        double fl = forward + strafe + rotate;
        double fr = forward - strafe - rotate;
        double bl = forward - strafe + rotate;
        double br = forward + strafe - rotate;
        double[] normalized = Utilities.normalizeMecanum(fl, fr, bl, br);
        setMotorPowers(normalized);
    }

    /** Stop all drive motors. */
    public void stop() {
        setMotorPowers(new double[] { 0.0, 0.0, 0.0, 0.0 });
    }

    private void setMotorPowers(double[] powers) {
        if (powers == null || powers.length < 4) return;
        frontLeft.setPower(powers[0]);
        frontRight.setPower(powers[1]);
        backLeft.setPower(powers[2]);
        backRight.setPower(powers[3]);
        updateTelemetryPowers();
    }

    /** Update cached power values for telemetry. */
    public void updateTelemetryPowers() {
        lastFLPower = frontLeft != null ? frontLeft.getPower() : 0.0;
        lastFRPower = frontRight != null ? frontRight.getPower() : 0.0;
        lastBLPower = backLeft != null ? backLeft.getPower() : 0.0;
        lastBRPower = backRight != null ? backRight.getPower() : 0.0;
    }

    public void cycleDriveDirectionPreset(int shift) {
        int cur = directionPresets.getPreset();
        int next = (cur + shift) & 0xF;
        directionPresets.setPreset(next);
        directionPresets.applyTo(frontLeft, frontRight, backLeft, backRight);
    }

    public int getDirectionPreset() {
        return directionPresets.getPreset();
    }

    public String getDirectionString() {
        return directionPresets.getPresetString();
    }

    public double getFrontLeftPower() { return lastFLPower; }
    public double getFrontRightPower() { return lastFRPower; }
    public double getBackLeftPower() { return lastBLPower; }
    public double getBackRightPower() { return lastBRPower; }
}
