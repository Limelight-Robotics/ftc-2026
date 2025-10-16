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
    private static final double TARGET_DISTANCE_THRESHOLD = 0.15; // meters - stop when this close
    private static final double LATERAL_TOLERANCE = 0.05; // meters - center tolerance
    private static final double YAW_TOLERANCE = 5.0; // degrees - rotation tolerance
    
    // Proportional control gains
    private static final double AXIAL_GAIN = 1.5; // For forward/backward movement (X axis)
    private static final double LATERAL_GAIN = 1.5; // For left/right strafing (Y axis)
    private static final double YAW_GAIN = 0.03; // For rotation
    
    // Power limits
    private static final double MAX_AXIAL_POWER = 1.0;
    private static final double MAX_LATERAL_POWER = 1.0;
    private static final double MAX_YAW_POWER = 1.0;
    
    // Minimum power to overcome friction
    private static final double MIN_POWER = 0.15;

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

    // Vision system (optional, can be null)
    private Vision vision = null;

    /**
     * Initialize robot hardware
     */
    public void init(HardwareMap hardwareMap) {
        init(hardwareMap, null);
    }

    public void init(HardwareMap hardwareMap, Vision vision) {
        // Initialize drive motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "fL");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fR");
        backRightDrive = hardwareMap.get(DcMotor.class, "bR");

        // Initialize vision system
        this.vision = vision;
        if (this.vision != null) {
            this.vision.init(hardwareMap, "limelight", Vision.Pipeline.APRIL_TAG);
        }
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
     * BotPose coordinates: X = forward/back, Y = left/right, Z = up/down
     * Yaw = rotation angle to face the tag
     *
     * Modified: ignore vision and just drive forward.
     */
    public MovementResult moveToAprilTag(Vision.TargetData targetData) {
        // If there's no target data, do nothing
        if (targetData == null || !targetData.isAcquired) {
            stopMovement();
            return new MovementResult(false, "No target", 0.0, 0.0, 0.0);
        }

        // Use averaged positions from Vision.TargetData
        // In Vision.TargetData: xPosition = forward/back (meters), yPosition = left/right (meters)
        double xPos = targetData.xPosition; // forward (positive = ahead)
        double yPos = targetData.yPosition; // right (positive = right)

        // Determine yaw error. Try to use botPose if available for orientation.
        // Determine yaw error using Limelight's tx. Limelight's tx is positive when the
        // target is to the right. Use tx directly so commandedYaw = sign(tx) turns toward tag.
        double tx = 0.0;
        if (targetData.result != null) {
            try {
                tx = targetData.result.getTx();
            } catch (Exception ignored) {
                tx = 0.0;
            }
        }

        double yawError = tx; // positive = target is to the right

        // Use constant powers as requested (simple on/off control)
        final double AXIAL_CONST = 0.30;   // meters -> motor power (negated for forward)
        final double LATERAL_CONST = 0.20;
        final double YAW_CONST = 0.20;

        // Axial: move forward/back based on xPos (positive xPos = target ahead)
        double commandedAxial = 0.0;
        if (xPos > TARGET_DISTANCE_THRESHOLD) {
            commandedAxial = AXIAL_CONST; // negative axial moves forward toward tag
        } else if (xPos < -TARGET_DISTANCE_THRESHOLD) {
            commandedAxial = -AXIAL_CONST; // move backward
        }

        // Lateral: if target is offset, strafe to center. Use same sign convention as calculateLateralPower
        double commandedLateral = 0.0;
        if (Math.abs(yPos) > LATERAL_TOLERANCE) {
            commandedLateral = -Math.signum(yPos) * LATERAL_CONST; // negative to correct right-offset
        }

        // Yaw: turn toward tag. yawError is in degrees; positive means tag is to the right
        double commandedYaw = 0.0;
        if (Math.abs(yawError) > YAW_TOLERANCE) {
            commandedYaw = Math.signum(yawError) * YAW_CONST;
        }

        // Drive with the constant commands
        drive(commandedAxial, commandedLateral, commandedYaw);

        // Update telemetry tracking
        lastAxial = commandedAxial;
        lastLateral = commandedLateral;
        lastYaw = commandedYaw;
        updateTelemetryPowers();

    boolean atTarget = isAtTarget(xPos, yPos, yawError);
        String status = atTarget ? "At target" : "Moving to tag";

        return new MovementResult(atTarget, status, commandedAxial, commandedLateral, commandedYaw);
    }

    /**
     * Calculate forward/backward power based on X position (distance)
     * Positive X means target is ahead, negative means behind
     */
    private double calculateAxialPower(double xPosition) {
        // If we're close enough, stop
        if (Math.abs(xPosition) <= TARGET_DISTANCE_THRESHOLD) {
            return 0.0;
        }

        // Proportional control: power proportional to distance
        double power = xPosition * AXIAL_GAIN;
        
        // Clamp to max power
        power = Math.max(-MAX_AXIAL_POWER, Math.min(MAX_AXIAL_POWER, power));
        
        // Apply minimum power to overcome friction
        if (Math.abs(power) > 0 && Math.abs(power) < MIN_POWER) {
            power = Math.signum(power) * MIN_POWER;
        }
        
        return power;
    }

    /**
     * Calculate lateral (strafe) power for centering on target
     * Positive Y means target is to the right, negative means left
     */
    private double calculateLateralPower(double yPosition) {
        // If centered enough, stop strafing
        if (Math.abs(yPosition) <= LATERAL_TOLERANCE) {
            return 0.0;
        }

        // Proportional control with negative sign (strafe opposite direction to center)
        double power = -yPosition * LATERAL_GAIN;
        
        // Clamp to max power
        power = Math.max(-MAX_LATERAL_POWER, Math.min(MAX_LATERAL_POWER, power));
        
        // Apply minimum power to overcome friction
        if (Math.abs(power) > 0 && Math.abs(power) < MIN_POWER) {
            power = Math.signum(power) * MIN_POWER;
        }
        
        return power;
    }

    /**
     * Calculate yaw (rotation) power to face the target
     * Yaw is in degrees - positive means target is to the left, negative means right
     */
    private double calculateYawPower(double yaw) {
        // If facing the target, stop rotating
        if (Math.abs(yaw) <= YAW_TOLERANCE) {
            return 0.0;
        }

        // Proportional control: power proportional to angle error
        // Positive yaw = target is left, so turn left (positive power)
        double power = yaw * YAW_GAIN;
        
        // Clamp to max power
        power = Math.max(-MAX_YAW_POWER, Math.min(MAX_YAW_POWER, power));
        
        // Apply minimum power to overcome friction
        if (Math.abs(power) > 0 && Math.abs(power) < MIN_POWER) {
            power = Math.signum(power) * MIN_POWER;
        }
        
        return power;
    }

    /**
     * Check if robot is at the target position and orientation
     */
    private boolean isAtTarget(double xPosition, double yPosition, double yaw) {
        return Math.abs(xPosition) <= TARGET_DISTANCE_THRESHOLD &&
               Math.abs(yPosition) <= LATERAL_TOLERANCE &&
               Math.abs(yaw) <= YAW_TOLERANCE;
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
        if (telemetry == null) return;

        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                lastFrontLeftPower, lastFrontRightPower, lastBackLeftPower, lastBackRightPower);
        telemetry.addData("Drive Inputs", "Axial: %.2f, Lateral: %.2f, Yaw: %.2f",
                lastAxial, lastLateral, lastYaw);

        if (vision == null) {
            telemetry.addData("Vision", "None");
        } else {
            Object latest = null;
            try {
                latest = vision.getLatestResult();
            } catch (Exception ignored) {
                latest = null;
            }
            telemetry.addData("Vision", latest == null ? "None" : latest.toString());
        }

        telemetry.update();
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
        public final double yawPower;

        public MovementResult(boolean atTarget, String status, double axialPower, 
                            double lateralPower, double yawPower) {
            this.atTarget = atTarget;
            this.status = status;
            this.axialPower = axialPower;
            this.lateralPower = lateralPower;
            this.yawPower = yawPower;
        }
    }
}
