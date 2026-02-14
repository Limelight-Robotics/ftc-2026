package org.firstinspires.ftc.teamcode.classes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.classes.robot.DriveSubsystem;

@Config
public class DefaultRobot {
    private DriveSubsystem drive = null;
    private DcMotor intakeMotor = null;
    private DcMotor loaderMotor = null;
    private Vision vision = null;
    private String loaderInitError = null;


    // Loader motor encoder positions (ticks) — tune these for your mechanism
    public static int LOADER_UP_POSITION = 5;
    public static int LOADER_DOWN_POSITION = 0;
    public static double LOADER_MOTOR_POWER = .5;

    private double lastForward, lastStrafe, lastRotate;
    private double intakePower = 0.0;

    public void init(HardwareMap hardwareMap) {
        drive = new DriveSubsystem(hardwareMap);
        initIntakeMotor(hardwareMap);
        initLoaderMotor(hardwareMap);
        vision = new Vision(hardwareMap);
    }

    private void initIntakeMotor(HardwareMap hardwareMap) {
        try {
            intakeMotor = hardwareMap.get(DcMotor.class, "intake");
            intakeMotor.setPower(0.0);
        } catch (Exception ignored) {
            intakeMotor = null;
        }
    }

    private void initLoaderMotor(HardwareMap hardwareMap) {
        try {
            loaderMotor = hardwareMap.get(DcMotor.class, "loader");
            loaderMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            loaderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            loaderMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } catch (Exception e) {
            loaderMotor = null;
            loaderInitError = e.getMessage();
        }
    }

      public boolean isLoaderInitialized() {
            return loaderMotor != null;
      }
      public String getLoaderInitError() {
            return loaderInitError;
      }

    public int getLoaderPosition() {
        if (loaderMotor != null) {
            return loaderMotor.getCurrentPosition();
        }
        return -1;
    }

    public void drive(double forward, double strafe, double rotate) {
        drive.drive(forward, strafe, rotate);
    }

    public void driveWithGamepad(double forward, double strafe, double rotate) {
        lastForward = forward;
        lastStrafe = strafe;
        lastRotate = rotate;
        drive.drive(forward, strafe, rotate);
        drive.updateTelemetryPowers();
    }

    public void stopMovement() {
        drive.stop();
    }

    public double getFrontLeftPower() {
        return drive.getFrontLeftPower();
    }
    public double getFrontRightPower() {
        return drive.getFrontRightPower();
    }
    public double getBackLeftPower() {
        return drive.getBackLeftPower();
    }
    public double getBackRightPower() {
        return drive.getBackRightPower();
    }
    public double getLastForward() {
        return lastForward;
    }
    public double getLastStrafe() {
        return lastStrafe;
    }
    public double getLastRotate() {
        return lastRotate;
    }
    public double getIntakePower() {
        return intakePower;
    }

    public void setIntakePower(double power) {
        intakePower = Utilities.clamp(power);
        if (intakeMotor != null) {
            intakeMotor.setPower(-intakePower);
        }
    }

    public void setLoaderPosition(int targetTicks) {
        if (loaderMotor != null) {
            loaderMotor.setTargetPosition(targetTicks);
            loaderMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            loaderMotor.setPower(LOADER_MOTOR_POWER);
        }
    }

    public void raiseLoader() {
        setLoaderPosition(LOADER_UP_POSITION);
    }

    public void lowerLoader() {
        setLoaderPosition(LOADER_DOWN_POSITION);
    }

    public void stopLoader() {
        if (loaderMotor != null) {
            loaderMotor.setPower(0);
            loaderMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public Vision getVision() {
        return vision;
    }

    public void resetEncoders() {
        drive.resetEncoders();
    }
    public void setTargetPositions(int fl, int fr, int bl, int br) {
        drive.setTargetPositions(fl, fr, bl, br);
    }
    public void setAllMotorPower(double power) {
        drive.setAllMotorPower(power);
    }
    public boolean areMotorsBusy() {
        return drive.areMotorsBusy();
    }
    public void setRunUsingEncoders() {
        drive.setRunUsingEncoders();
    }
}
