package org.firstinspires.ftc.teamcode.classes;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.classes.robot.DriveSubsystem;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatusSnapshot;

public class DefaultRobot
{
    private DriveSubsystem          drive       = null;
    private DcMotor                 intakeMotor = null;
    private Servo                   loaderServo = null;
    private ThreeDeadWheelLocalizer localizer   = null;
    private Vision                  vision      = null;

    // Loader servo positions
    public static final double LOADER_UP_POSITION   = 1.0;
    public static final double LOADER_DOWN_POSITION = 0.0;

    public ThreeDeadWheelLocalizer getLocalizer() { return localizer; }
    private PoseVelocity2d         lastVelocity = new com.acmerobotics.roadrunner.PoseVelocity2d(
        new com.acmerobotics.roadrunner.Vector2d(0, 0), 0);
    private double lastForward, lastStrafe, lastRotate;
    private double intakePower = 0.0;

    public void init(HardwareMap hardwareMap)
    {
        drive = new DriveSubsystem(hardwareMap);
        initIntakeMotor(hardwareMap);
        initLoaderServo(hardwareMap);
        initLocalizer(hardwareMap);
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
            // Log will show in logcat if init fails
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

    private void initLocalizer(HardwareMap hardwareMap)
    {
        try
        {
            // Use inPerTick from MecanumDrive params for consistency
            localizer = new ThreeDeadWheelLocalizer(
                hardwareMap, MecanumDrive.PARAMS.inPerTick, new Pose2d(0, 0, 0));
        }
        catch (Exception ignored)
        {
            localizer = null;
        }
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

    public RobotStatus getStatus()
    {
        return RobotStatusSnapshot.builder()
            .frontLeftPower(drive.getFrontLeftPower())
            .frontRightPower(drive.getFrontRightPower())
            .backLeftPower(drive.getBackLeftPower())
            .backRightPower(drive.getBackRightPower())
            .lastForward(lastForward)
            .lastStrafe(lastStrafe)
            .lastRotate(lastRotate)
            .intakePower(intakePower)
            .build();
    }

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

    public Pose2d updateLocalizer()
    {
        if (localizer != null)
        {
            lastVelocity = localizer.update();
            return localizer.getPose();
        }
        return new Pose2d(0, 0, 0);
    }

    public Pose2d getPose()
    {
        if (localizer != null)
        {
            return localizer.getPose();
        }
        return new Pose2d(0, 0, 0);
    }

    public PoseVelocity2d getVelocity() { return lastVelocity; }

    public void setPose(Pose2d pose)
    {
        if (localizer != null)
        {
            localizer.setPose(pose);
        }
    }

    public Vision getVision() { return vision; }
}
