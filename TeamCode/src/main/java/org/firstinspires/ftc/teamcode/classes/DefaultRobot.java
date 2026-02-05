package org.firstinspires.ftc.teamcode.classes;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.classes.robot.DriveSubsystem;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatusSnapshot;

public class DefaultRobot
{
    private DriveSubsystem          drive       = null;
    private DcMotor                 intakeMotor = null;
    private ThreeDeadWheelLocalizer localizer   = null;
    private Vision                  vision      = null;

    public ThreeDeadWheelLocalizer getLocalizer() { return localizer; }
    private PoseVelocity2d         lastVelocity = new com.acmerobotics.roadrunner.PoseVelocity2d(
        new com.acmerobotics.roadrunner.Vector2d(0, 0), 0);
    private double lastForward, lastStrafe, lastRotate;
    private double intakePower = 0.0;

    public void init(HardwareMap hardwareMap)
    {
        drive = new DriveSubsystem(hardwareMap);
        initIntakeMotor(hardwareMap);
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
