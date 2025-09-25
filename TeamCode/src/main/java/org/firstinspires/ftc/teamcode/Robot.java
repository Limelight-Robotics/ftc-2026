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

    public void driveWithGamepad(double axial, double lateral, double yaw) {
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
        frontLeftDrive.setPower(frontLeftPower);
        frontRightDrive.setPower(frontRightPower);
        backLeftDrive.setPower(backLeftPower);
        backRightDrive.setPower(backRightPower);
    }

    public double getFrontLeftPower() { return lastFrontLeftPower; }
    public double getFrontRightPower() { return lastFrontRightPower; }
    public double getBackLeftPower() { return lastBackLeftPower; }
    public double getBackRightPower() { return lastBackRightPower; }
}
