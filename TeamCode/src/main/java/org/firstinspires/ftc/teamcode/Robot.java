package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
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

    // limelight specific variables
    private Limelight3A limelight = null;
    private int limelightPipelineIndex = 0;

    public void init(HardwareMap hardwareMap) {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "fL");
        backLeftDrive = hardwareMap.get(DcMotor.class, "bL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "fR");
        backRightDrive = hardwareMap.get(DcMotor.class, "bR");

        // Assert that the motors were found in the configuration. Assertions must be enabled at runtime
        assert frontLeftDrive != null : "frontLeftDrive is null - check robot config name 'fL'";
        assert backLeftDrive != null : "backLeftDrive is null - check robot config name 'bL'";
        assert frontRightDrive != null : "frontRightDrive is null - check robot config name 'fR'";
        assert backRightDrive != null : "backRightDrive is null - check robot config name 'bR'";

        // Now safe to dereference
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    public void initLimelight(HardwareMap hardwareMap, String name, int pipelineIndex) {
        this.limelight = hardwareMap.get(Limelight3A.class, name);
        this.limelightPipelineIndex = pipelineIndex;
        this.limelight.pipelineSwitch(limelightPipelineIndex);
    }

    public int getLimelightPipelineIndex() {
        return limelightPipelineIndex;
    }

    public void setLimelightPipelineIndex(int pipelineIndex) {
        this.limelightPipelineIndex = pipelineIndex;
    }

    public void startLimelight() {
        if (limelight != null) {
            limelight.start();
        }
    }

    public void stopLimelight() {
        if (limelight != null) {
            limelight.stop();
        }
    }

    public LLResult getLimelightResult() {
        if (limelight != null) {
            LLResult result = limelight.getLatestResult();
            if (result != null && result.isValid()) {
                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void driveWithGamepad(double axial, double lateral, double yaw) {
        // store requested inputs for telemetry
        lastAxial = axial;
        lastLateral = lateral;
        lastYaw = yaw;

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
        // Use assertions instead of null checks; assertions must be enabled at runtime to take effect.
        assert frontLeftDrive != null : "frontLeftDrive is null when setting power";
        assert frontRightDrive != null : "frontRightDrive is null when setting power";
        assert backLeftDrive != null : "backLeftDrive is null when setting power";
        assert backRightDrive != null : "backRightDrive is null when setting power";

        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
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
