package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Robot {
    public DcMotor frontLeftDrive = null;
    public DcMotor backLeftDrive = null;
    public DcMotor frontRightDrive = null;
    public DcMotor backRightDrive = null;

    private double lastFrontLeftPower = 0;
    private double lastFrontRightPower = 0;
    private double lastBackLeftPower = 0;
    private double lastBackRightPower = 0;

    // track last requested axial/lateral/yaw for telemetry
    private double lastAxial = 0;
    private double lastLateral = 0;
    private double lastYaw = 0;

    // TeleOp enable/disable flag and toggle debounce state
    private boolean teleopEnabled = true;
    private boolean lastToggleButtonState = false;

    public void init(HardwareMap hardwareMap) {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "fL");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fR");
        backRightDrive = hardwareMap.get(DcMotor.class, "bR");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Update the teleop enabled state using a toggle button input.
     * Call this once per control loop and pass the current boolean state of the toggle button
     * (e.g. gamepad1.a). The toggle occurs on the rising edge.
     */
    public void updateTeleopEnabled(boolean toggleButtonPressed) {
        if (toggleButtonPressed && !lastToggleButtonState) {
            teleopEnabled = !teleopEnabled;
            // when disabling, make sure motors are zeroed
            if (!teleopEnabled) setDrivePower(0,0,0,0);
        }
        lastToggleButtonState = toggleButtonPressed;
    }

    public void setTeleopEnabled(boolean enabled) {
        teleopEnabled = enabled;
        if (!teleopEnabled) setDrivePower(0,0,0,0);
    }

    public boolean isTeleopEnabled() {
        return teleopEnabled;
    }

    public void toggleTeleopEnabled() {
        setTeleopEnabled(!teleopEnabled);
    }

    public void driveWithGamepad(double axial, double lateral, double yaw) {
        // store requested inputs for telemetry
        lastAxial = axial;
        lastLateral = lateral;
        lastYaw = yaw;

        if (!teleopEnabled) {
            // When TeleOp is disabled, ensure motors are stopped and last powers reflect that.
            setDrivePower(0, 0, 0, 0);
            lastFrontLeftPower = 0;
            lastFrontRightPower = 0;
            lastBackLeftPower = 0;
            lastBackRightPower = 0;
            return;
        }

        double frontLeftPower  = axial + lateral + yaw;
        double frontRightPower = axial - lateral - yaw;
        double backLeftPower   = axial - lateral + yaw;
        double backRightPower  = axial + lateral - yaw;
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));
        if (max > 1.0) {
            frontLeftPower  /= max;
            frontRightPower /= max;
            backLeftPower   /= max;
            backRightPower  /= max;
        }
        setDrivePower(frontLeftPower, frontRightPower, backLeftPower, backRightPower);
        lastFrontLeftPower = frontLeftPower;
        lastFrontRightPower = frontRightPower;
        lastBackLeftPower = backLeftPower;
        lastBackRightPower = backRightPower;
    }

    public void setDrivePower(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        if (!teleopEnabled) {
            // safety: force zero when TeleOp disabled
            if (frontLeftDrive != null) frontLeftDrive.setPower(0);
            if (frontRightDrive != null) frontRightDrive.setPower(0);
            if (backLeftDrive != null) backLeftDrive.setPower(0);
            if (backRightDrive != null) backRightDrive.setPower(0);
            return;
        }
        if (frontLeftDrive != null) frontLeftDrive.setPower(frontLeftPower);
        if (frontRightDrive != null) frontRightDrive.setPower(frontRightPower);
        if (backLeftDrive != null) backLeftDrive.setPower(backLeftPower);
        if (backRightDrive != null) backRightDrive.setPower(backRightPower);
    }

    public double getFrontLeftPower() { return lastFrontLeftPower; }
    public double getFrontRightPower() { return lastFrontRightPower; }
    public double getBackLeftPower() { return lastBackLeftPower; }
    public double getBackRightPower() { return lastBackRightPower; }

    // accessors for requested axial/lateral/yaw so opmodes can display them
    public double getLastAxial() { return lastAxial; }
    public double getLastLateral() { return lastLateral; }
    public double getLastYaw() { return lastYaw; }
}
