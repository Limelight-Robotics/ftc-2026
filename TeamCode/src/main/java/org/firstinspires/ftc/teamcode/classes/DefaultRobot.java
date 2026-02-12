package org.firstinspires.ftc.teamcode.classes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.classes.robot.DriveSubsystem;

@Config
public class DefaultRobot
{
    private DriveSubsystem drive       = null;
    private DcMotor        intakeMotor = null;
    private Servo          loaderServo = null;
    private Vision         vision      = null;

    // Loader servo positions
    public static double LOADER_UP_POSITION   = 1.0;
    public static double LOADER_DOWN_POSITION = 0.0;

    private double lastForward, lastStrafe, lastRotate;
    private double intakePower = 0.0;

    public void init(HardwareMap hardwareMap)
    {
        drive = new DriveSubsystem(hardwareMap);
        initIntakeMotor(hardwareMap);
        initLoaderServo(hardwareMap);
        vision = new Vision(hardwareMap);
    }

    private void initIntakeMotor(HardwareMap hardwareMap)
    {
        try
        {
            intakeMotor = hardwareMap.get(DcMotor.class, "intake");
            intakeMotor.setPower(0.0);
        }
        catch (Exception ignored)
        {
            intakeMotor = null;
        }
    }

    private void initLoaderServo(HardwareMap hardwareMap)
    {
        try
        {
            loaderServo = hardwareMap.get(Servo.class, "loader");
        }
        catch (Exception e)
        {
            loaderServo = null;
            e.printStackTrace();
        }
    }

    public boolean isLoaderInitialized() { return loaderServo != null; }

    public double getLoaderPosition()
    {
        if (loaderServo != null)
        {
            return loaderServo.getPosition();
        }
        return -1.0;
    }

    public void drive(double forward, double strafe, double rotate)
    {
        drive.drive(forward, strafe, rotate);
    }

    public void driveWithGamepad(double forward, double strafe, double rotate)
    {
        lastForward = forward;
        lastStrafe  = strafe;
        lastRotate  = rotate;
        drive.drive(forward, strafe, rotate);
        drive.updateTelemetryPowers();
    }

    public void stopMovement() { drive.stop(); }

    public double getFrontLeftPower() { return drive.getFrontLeftPower(); }
    public double getFrontRightPower() { return drive.getFrontRightPower(); }
    public double getBackLeftPower() { return drive.getBackLeftPower(); }
    public double getBackRightPower() { return drive.getBackRightPower(); }
    public double getLastForward() { return lastForward; }
    public double getLastStrafe() { return lastStrafe; }
    public double getLastRotate() { return lastRotate; }
    public double getIntakePower() { return intakePower; }

    public void setIntakePower(double power)
    {
        intakePower = Utilities.clamp(power);
        if (intakeMotor != null)
        {
            intakeMotor.setPower(-intakePower);
        }
    }

    public void setLoaderPosition(double position)
    {
        if (loaderServo != null)
        {
            loaderServo.setPosition(position);
        }
    }

    public void raiseLoader() { setLoaderPosition(LOADER_UP_POSITION); }

    public void lowerLoader() { setLoaderPosition(LOADER_DOWN_POSITION); }

    public Vision getVision() { return vision; }

    public void resetEncoders() { drive.resetEncoders(); }
    public void setTargetPositions(int fl, int fr, int bl, int br) { drive.setTargetPositions(fl, fr, bl, br); }
    public void setAllMotorPower(double power) { drive.setAllMotorPower(power); }
    public boolean areMotorsBusy() { return drive.areMotorsBusy(); }
    public void setRunUsingEncoders() { drive.setRunUsingEncoders(); }
}
