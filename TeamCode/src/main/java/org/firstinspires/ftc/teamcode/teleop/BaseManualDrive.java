package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.classes.DefaultRobot;
import org.firstinspires.ftc.teamcode.classes.LauncherHelper;
import org.firstinspires.ftc.teamcode.classes.Vision;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;

/**
 * Base class for manual drive OpModes. Provides common drive and intake logic.
 * Subclasses specify the speed multiplier via getSpeedMultiplier().
 */
public abstract class BaseManualDrive extends LinearOpMode
{
    private final ElapsedTime  runtime = new ElapsedTime();
    private final DefaultRobot robot   = new DefaultRobot();

    // Intake
    private DcMotor             intakeMotor;
    private static final String INTAKE_MOTOR_NAME = "intake";
    private static final double MAX_INTAKE_POWER  = 1.0;

    // Shooter (spins up and launches balls)
    private DcMotorEx           shooterMotor;
    private static final String SHOOTER_MOTOR_NAME    = "shooter";
    private static final double SHOOTER_TICKS_PER_REV = 28.0;
    private static final double SHOOTER_TARGET_RPM    = 3000.0;

    // Turret (aims the shooter)
    private DcMotor             turretMotor;
    private static final String TURRET_MOTOR_NAME = "turret";
    private static final double MAX_TURRET_POWER  = 1.0;

    // Vision
    private Vision vision;

    /** Returns speed multiplier (0.0 to 1.0) for drive inputs. */
    protected abstract double getSpeedMultiplier();

    @Override public void runOpMode()
    {
        robot.init(hardwareMap);
        intakeMotor  = getMotorOrNull(INTAKE_MOTOR_NAME);
        shooterMotor = getMotorExOrNull(SHOOTER_MOTOR_NAME);
        turretMotor  = getMotorOrNull(TURRET_MOTOR_NAME);
        vision       = robot.getVision();
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive())
        {
            processDriveInput();
            processIntakeInput();
            processShooterInput();
            processTurretInput();
            processLoaderInput();
            robot.updateLocalizer();
            vision.update();
            updateTelemetry();
        }
        stopMotors();
    }

    private DcMotor getMotorOrNull(String name)
    {
        try
        {
            DcMotor m = hardwareMap.get(DcMotor.class, name);
            m.setPower(0.0);
            return m;
        }
        catch (Exception e)
        {
            telemetry.addData("Error", "Failed to init " + name + ": " + e.getMessage());
            telemetry.update();
            return null;
        }
    }

    private DcMotorEx getMotorExOrNull(String name)
    {
        try
        {
            DcMotorEx m = hardwareMap.get(DcMotorEx.class, name);
            m.setPower(0.0);
            return m;
        }
        catch (Exception e)
        {
            telemetry.addData("Error", "Failed to init " + name + ": " + e.getMessage());
            telemetry.update();
            return null;
        }
    }

    /**
     * Loader servo control: D-pad up raises the loader, D-pad down lowers it.
     */
    private void processLoaderInput()
    {
        if (gamepad1.dpad_up)
        {
            robot.raiseLoader();
        }
        else if (gamepad1.dpad_down)
        {
            robot.lowerLoader();
        }
    }

    private void processDriveInput()
    {
        double mult = getSpeedMultiplier();
        robot.driveWithGamepad(-gamepad1.left_stick_y * mult, gamepad1.left_stick_x * mult,
            gamepad1.right_stick_x * mult);
    }

    private void processIntakeInput()
    {
        setMotorPowerFromGamepad(
            intakeMotor, gamepad1.left_bumper, gamepad1.right_bumper, MAX_INTAKE_POWER);
    }

    /**
     * Shooter control: Hold X button to spin up shooter to fixed target RPM.
     * When at speed (98% of target), automatically raises the loader servo.
     * When X is released, stops the motor and lowers the loader.
     */
    private void processShooterInput()
    {
        if (shooterMotor == null)
            return;

        if (gamepad1.x)
        {
            double ticksPerSec = SHOOTER_TARGET_RPM * SHOOTER_TICKS_PER_REV / 60.0;
            shooterMotor.setVelocity(ticksPerSec);

            double  actualTicksPerSec = shooterMotor.getVelocity();
            double  actualRPM         = actualTicksPerSec * 60.0 / SHOOTER_TICKS_PER_REV;
            boolean atSpeed           = LauncherHelper.isAtTargetRPM(actualRPM, SHOOTER_TARGET_RPM);

            if (atSpeed)
            {
                robot.raiseLoader();
            }

            telemetry.addData("Shooter", "Target: %.0f RPM | Actual: %.0f RPM | %s",
                SHOOTER_TARGET_RPM, actualRPM, atSpeed ? "FIRING" : "SPINNING UP");
        }
        else
        {
            shooterMotor.setVelocity(0);
            robot.lowerLoader();
        }
    }

    /**
     * Turret control: D-pad left/right rotates the turret for aiming.
     */
    private void processTurretInput()
    {
        setMotorPowerFromGamepad(
            turretMotor, gamepad1.dpad_left, gamepad1.dpad_right, MAX_TURRET_POWER);
    }

    /**
     * Sets the given motor's power based on positive/negative gamepad inputs.
     * If both inputs are false, sets power to 0.
     */
    private void setMotorPowerFromGamepad(
        DcMotor motor, boolean negative, boolean positive, double maxPower)
    {
        if (motor == null)
            return;
        double power = 0.0;
        if (positive)
        {
            power = maxPower;
        }
        else if (negative)
        {
            power = -maxPower;
        }
        motor.setPower(power);
    }

    private void stopMotors()
    {
        if (intakeMotor != null)
            intakeMotor.setPower(0.0);
        if (shooterMotor != null)
            shooterMotor.setPower(0.0);
        if (turretMotor != null)
            turretMotor.setPower(0.0);
    }

    private void addLocalizerTelemetry()
    {
        try
        {
            // Use only the Robot interface
            Pose2d pose = robot.getPose();
            telemetry.addData("Robot Pose", "x: %.2f, y: %.2f, heading: %.2f", pose.position.x,
                pose.position.y, pose.heading.toDouble());
        }
        catch (Exception ignored)
        {
        }
    }

    private void updateTelemetry()
    {
        RobotStatus status = robot.getStatus();
        Pose2d      pose   = robot.getPose();
        addLocalizerTelemetry();
        telemetry.addData("Status", "Run Time: " + runtime);
        telemetry.addData("Pose", "X: %.2f, Y: %.2f, H: %.1f°", pose.position.x, pose.position.y,
            Math.toDegrees(pose.heading.toDouble()));
        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
            status.getFrontLeftPower(), status.getFrontRightPower(), status.getBackLeftPower(),
            status.getBackRightPower());
        telemetry.addData("Inputs", "Fwd: %.2f, Str: %.2f, Rot: %.2f", status.getLastForward(),
            status.getLastStrafe(), status.getLastRotate());
        telemetry.addData("Intake Power", "%.2f", status.getIntakePower());
        telemetry.addData("Loader", "Init: %s | Pos: %.2f | DpadUp: %s | DpadDown: %s",
            robot.isLoaderInitialized(), robot.getLoaderPosition(), gamepad1.dpad_up,
            gamepad1.dpad_down);
        telemetry.addData("Vision", vision.getStatusString());
        telemetry.update();
    }
}
