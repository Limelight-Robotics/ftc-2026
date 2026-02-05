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

    // Turret / Launchers
    private DcMotorEx           turretMotor;
    private static final String TURRET_MOTOR_NAME      = "turret";
    private static final double MAX_TURRET_POWER       = 1.0;
    private static final double LAUNCHER_TICKS_PER_REV = 28.0;

    // Cable Drive
    private DcMotor             cableDriveMotor;
    private static final String CABLE_DRIVE_MOTOR_NAME = "cableDrive";
    private static final double MAX_CABLE_DRIVE_POWER  = 1.0;

    // Vision
    private Vision vision;

    /** Returns speed multiplier (0.0 to 1.0) for drive inputs. */
    protected abstract double getSpeedMultiplier();

    @Override public void runOpMode()
    {
        robot.init(hardwareMap);
        intakeMotor     = getMotorOrNull(INTAKE_MOTOR_NAME);
        turretMotor     = getMotorExOrNull(TURRET_MOTOR_NAME);
        cableDriveMotor = getMotorOrNull(CABLE_DRIVE_MOTOR_NAME);
        vision          = robot.getVision();
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        runtime.reset();

        while (opModeIsActive())
        {
            processDriveInput();
            processIntakeInput();
            processTurretInput();
            processCableDriveInput();
            processLoaderInput();
            processLauncherInput();
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

    /**
     * Launcher control: Hold right stick button to spin up launcher to
     * vision-calculated RPM. When button is released, processTurretInput()
     * resumes normal turret control (including stopping the motor if triggers
     * aren't held).
     */
    private void processLauncherInput()
    {
        if (turretMotor == null || !gamepad1.right_stick_button)
            return;

        double targetRPM = LauncherHelper.getRequiredRPM(vision);

        if (targetRPM <= 0)
        {
            turretMotor.setVelocity(0);
            telemetry.addData("Launcher", "NO TARGET");
            return;
        }

        double ticksPerSec = targetRPM * LAUNCHER_TICKS_PER_REV / 60.0;
        turretMotor.setVelocity(ticksPerSec);

        double  actualTicksPerSec = turretMotor.getVelocity();
        double  actualRPM         = actualTicksPerSec * 60.0 / LAUNCHER_TICKS_PER_REV;
        boolean ready             = LauncherHelper.isAtTargetRPM(actualRPM, targetRPM);

        telemetry.addData("Launcher", "Target: %.0f RPM | Actual: %.0f RPM | %s", targetRPM,
            actualRPM, ready ? "READY" : "SPINNING UP");
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

    private void processTurretInput()
    {
        // Skip turret aim control while launcher is spinning up
        if (gamepad1.right_stick_button)
            return;
        // The triggers are as floats, so we need boolean conversion.
        setMotorPowerFromGamepad(turretMotor, gamepad1.left_trigger > 0.0,
            gamepad1.right_trigger > 0.0, MAX_TURRET_POWER);
    }

    private void processCableDriveInput()
    {
        setMotorPowerFromGamepad(
            cableDriveMotor, gamepad1.dpad_left, gamepad1.dpad_right, MAX_CABLE_DRIVE_POWER);
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
        if (turretMotor != null)
            turretMotor.setPower(0.0);
        if (cableDriveMotor != null)
            cableDriveMotor.setPower(0.0);
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
        telemetry.addData("Status", "Run Time: " + runtime.toString());
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
