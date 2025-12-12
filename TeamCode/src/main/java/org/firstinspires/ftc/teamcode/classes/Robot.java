package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.classes.robot.DriveSubsystem;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatusSnapshot;

/**
 * Robot interface for teleop control. For autonomous, use MecanumDrive directly
 * with RoadRunner trajectories.
 */
public interface Robot {
    void init(HardwareMap hardwareMap);
    void drive(double forward, double strafe, double rotate);
    void driveWithGamepad(double forward, double strafe, double rotate);
    void stopMovement();
    RobotStatus getStatus();
    void setIntakePower(double power);
    void cycleDriveDirectionPreset(int delta);
    int getDriveDirectionPreset();
    String getDriveDirectionString();

    /** Factory method for default implementation. */
    static Robot createDefault() {
        return new DefaultRobot();
    }
}

/**
 * Default Robot implementation delegating to DriveSubsystem.
 */
class DefaultRobot implements Robot {
    private final DriveSubsystem drive = new DriveSubsystem();
    private DcMotor intakeMotor = null;
    private double lastForward, lastStrafe, lastRotate;
    private double intakePower = 0.0;

    @Override
    public void init(HardwareMap hardwareMap) {
        drive.init(hardwareMap);
        initIntakeMotor(hardwareMap);
    }

    private void initIntakeMotor(HardwareMap hardwareMap) {
        try {
            intakeMotor = hardwareMap.get(DcMotor.class, "intake");
            intakeMotor.setPower(0.0);
        } catch (Exception ignored) {
            intakeMotor = null;
        }
    }

    @Override
    public void drive(double forward, double strafe, double rotate) {
        drive.drive(forward, strafe, rotate);
    }

    @Override
    public void driveWithGamepad(double forward, double strafe, double rotate) {
        lastForward = forward;
        lastStrafe = strafe;
        lastRotate = rotate;
        drive.drive(forward, strafe, rotate);
        drive.updateTelemetryPowers();
    }

    @Override
    public void stopMovement() {
        drive.stop();
    }

    @Override
    public RobotStatus getStatus() {
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

    @Override
    public void setIntakePower(double power) {
        intakePower = Utilities.clamp(power);
        if (intakeMotor != null) {
            intakeMotor.setPower(-intakePower);
        }
    }

    @Override
    public void cycleDriveDirectionPreset(int delta) {
        drive.cycleDriveDirectionPreset(delta);
    }

    @Override
    public int getDriveDirectionPreset() {
        return drive.getDirectionPreset();
    }

    @Override
    public String getDriveDirectionString() {
        return drive.getDirectionString();
    }
}
