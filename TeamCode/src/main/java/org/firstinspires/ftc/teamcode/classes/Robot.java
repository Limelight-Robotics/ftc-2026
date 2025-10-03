package org.firstinspires.ftc.teamcode.classes;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Robot class to handle all hardware operations and movement logic
 */
public class Robot {

    // Constants for movement
    private static final double TARGET_DISTANCE_THRESHOLD = 0.1;
    private static final double Y_POSITION_TOLERANCE = 0.05;
    private static final double FORWARD_POWER = 0.5;
    private static final double LATERAL_GAIN = 0.4;
    private static final double MAX_LATERAL_POWER = 0.5;

    // Drive motors
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;

    // Telemetry tracking for drive powers
    private double lastFrontLeftPower = 0;
    private double lastFrontRightPower = 0;
    private double lastBackLeftPower = 0;
    private double lastBackRightPower = 0;

    // Track last requested axial/lateral/yaw for telemetry
    private double lastAxial = 0;
    private double lastLateral = 0;
    private double lastYaw = 0;

    /**
     * Initialize robot hardware
     */
    public void init(HardwareMap hardwareMap) {
        // Initialize drive motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "fL");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fR");
        backRightDrive = hardwareMap.get(DcMotor.class, "bR");

        // Validate motor configuration
        validateMotors();

        // Set motor directions
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Validate that all motors are properly configured
     */
    private void validateMotors() {
        assert frontLeftDrive != null : "frontLeftDrive is null - check robot config name 'fL'";
        assert backLeftDrive != null : "backLeftDrive is null - check robot config name 'bL'";
        assert frontRightDrive != null : "frontRightDrive is null - check robot config name 'fR'";
        assert backRightDrive != null : "backRightDrive is null - check robot config name 'bR'";
    }

    /**
     * Core mecanum drive method
     */
    public void drive(double axial, double lateral, double yaw) {
        // Calculate individual motor powers for mecanum drive
        double frontLeftPower  = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower   = axial - lateral + yaw;
        double backRightPower  = axial + lateral - yaw;

        // Normalize powers to ensure no motor exceeds 100%
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        if (maxPower > 1.0) {
            frontLeftPower  /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower   /= maxPower;
            backRightPower  /= maxPower;
        }

        setMotorPowers(frontLeftPower, frontRightPower, backLeftPower, backRightPower);
    }

    /**
     * Drive method for gamepad control with telemetry tracking
     */
    public void driveWithGamepad(double axial, double lateral, double yaw) {
        // Store requested inputs for telemetry
        lastAxial = axial;
        lastLateral = lateral;
        lastYaw = yaw;

        // Use the core drive method
        drive(axial, lateral, yaw);

        // Update last power values for telemetry
        updateTelemetryPowers();
    }

    /**
     * Drive method for autonomous control
     */
    public void driveAutonomous(double axial, double lateral, double yaw) {
        drive(axial, lateral, yaw);
    }

    /**
     * Move towards an April Tag target automatically
     */
    public MovementResult moveToAprilTag(Vision.TargetData targetData) {
        if (!targetData.isAcquired) {
            stopMovement();
            return new MovementResult(false, "No target acquired", 0.0, 0.0);
        }

        double axialPower = calculateAxialPower(targetData.xPosition);
        double lateralPower = calculateLateralPower(targetData.yPosition);

        drive(axialPower, lateralPower, 0.0);

        boolean atTarget = isAtTarget(targetData.xPosition, targetData.yPosition);
        @SuppressLint("DefaultLocale") String status = atTarget ?
            String.format("Target reached! X: %.2f < %.2f, Y: %.2f centered",
                targetData.xPosition, TARGET_DISTANCE_THRESHOLD, targetData.yPosition) :
            String.format("Moving - X: %.2f, Y: %.2f", targetData.xPosition, targetData.yPosition);

        return new MovementResult(atTarget, status, axialPower, lateralPower);
    }

    /**
     * Calculate forward/backward power based on X position
     */
    private double calculateAxialPower(double xPosition) {
        return xPosition >= TARGET_DISTANCE_THRESHOLD ? FORWARD_POWER : 0.0;
    }

    /**
     * Calculate lateral power for centering on target
     */
    private double calculateLateralPower(double yPosition) {
        if (Math.abs(yPosition) <= Y_POSITION_TOLERANCE) {
            return 0.0;
        }

        double power = -yPosition * LATERAL_GAIN;
        return Math.max(-MAX_LATERAL_POWER, Math.min(MAX_LATERAL_POWER, power));
    }

    /**
     * Check if robot is at the target position
     */
    private boolean isAtTarget(double xPosition, double yPosition) {
        return xPosition < TARGET_DISTANCE_THRESHOLD &&
               Math.abs(yPosition) <= Y_POSITION_TOLERANCE;
    }

    /**
     * Stop all movement
     */
    public void stopMovement() {
        drive(0.0, 0.0, 0.0);
    }

    /**
     * Set individual motor powers
     */
    private void setMotorPowers(double frontLeftPower, double frontRightPower,
                               double backLeftPower, double backRightPower) {
        validateMotors();

        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }

    /**
     * Update telemetry power values
     */
    private void updateTelemetryPowers() {
        lastFrontLeftPower = frontLeftDrive.getPower();
        lastFrontRightPower = frontRightDrive.getPower();
        lastBackLeftPower = backLeftDrive.getPower();
        lastBackRightPower = backRightDrive.getPower();
    }

    /**
     * Display robot telemetry
     */
    public void displayTelemetry(Telemetry telemetry) {
        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                lastFrontLeftPower, lastFrontRightPower, lastBackLeftPower, lastBackRightPower);
        telemetry.addData("Drive Inputs", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f",
                lastAxial, lastLateral, lastYaw);
    }

    // Getters for telemetry
    public double getFrontLeftPower() { return lastFrontLeftPower; }
    public double getFrontRightPower() { return lastFrontRightPower; }
    public double getBackLeftPower() { return lastBackLeftPower; }
    public double getBackRightPower() { return lastBackRightPower; }
    public double getLastAxial() { return lastAxial; }
    public double getLastLateral() { return lastLateral; }
    public double getLastYaw() { return lastYaw; }

    /**
     * Data class to hold movement result information
     */
    public static class MovementResult {
        public final boolean atTarget;
        public final String status;
        public final double axialPower;
        public final double lateralPower;

        public MovementResult(boolean atTarget, String status, double axialPower, double lateralPower) {
            this.atTarget = atTarget;
            this.status = status;
            this.axialPower = axialPower;
            this.lateralPower = lateralPower;
        }
    }
}
