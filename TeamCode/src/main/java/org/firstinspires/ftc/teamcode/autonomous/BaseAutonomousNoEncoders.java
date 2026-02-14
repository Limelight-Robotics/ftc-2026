package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.classes.DefaultRobot;

/**
 * Base time-based autonomous: drives backward, fires the shooter, then strafes.
 * Subclasses specify the strafe direction via getStrafePower().
 *
 * Tune the timing and power constants on the actual robot via FTC Dashboard.
 */
@Config
public abstract class BaseAutonomousNoEncoders extends LinearOpMode {
  private final DefaultRobot robot = new DefaultRobot();
  private final ElapsedTime timer = new ElapsedTime();
  private DcMotorEx shooterMotor;

  // Drive power (0.0 to 1.0). Lower = more controllable but slower.
  public static double DRIVE_POWER = 0.5;

  // Shooter config
  public static double SHOOTER_TICKS_PER_REV = 28.0;
  public static double SHOOTER_TARGET_RPM = 3000.0;

  // Time in seconds for each step. Tune via FTC Dashboard.
  public static double DRIVE_BACKWARD_TIME_SEC = 1.5;
  public static double SHOOTER_SPINUP_PAUSE_SEC = 2.0;
  public static double LOADER_FIRE_PAUSE_SEC = 2.0;
  public static double STRAFE_TIME_SEC = 2.0;

  /** Returns the strafe power: negative for left (blue), positive for right (red). */
  protected abstract double getStrafePower();

  @Override
  public void runOpMode() {
    robot.init(hardwareMap);
    shooterMotor = initShooterMotor();

    telemetry.addData("Status", "Initialized - Ready to run");
    telemetry.addData("Shooter", shooterMotor != null ? "OK" : "NOT FOUND");
    telemetry.update();

    waitForStart();

    // Step 1: Drive backward
    telemetry.addData("Step", "1 - Driving backward");
    telemetry.update();
    driveForTime(-DRIVE_POWER, 0, 0, DRIVE_BACKWARD_TIME_SEC);

    // Step 2: Start the shooter motor
    telemetry.addData("Step", "2 - Spinning up shooter");
    telemetry.update();
    startShooter();

    // Step 3: Pause to let the shooter spin up
    pauseForTime(SHOOTER_SPINUP_PAUSE_SEC);

    // Step 4: Raise the loader to fire the ball
    telemetry.addData("Step", "3 - Raising loader to fire");
    telemetry.update();
    robot.raiseLoader();

    // Step 5: Pause while the ball fires
    pauseForTime(LOADER_FIRE_PAUSE_SEC);

    // Step 6: Stop shooter and lower loader
    stopShooter();
    robot.lowerLoader();

    // Step 7: Strafe
    telemetry.addData("Step", "4 - Strafing");
    telemetry.update();
    driveForTime(0, getStrafePower(), 0, STRAFE_TIME_SEC);

    // Stop and report
    robot.stopMovement();
    stopShooter();
    telemetry.addData("Status", "Autonomous complete");
    telemetry.update();
  }

  private DcMotorEx initShooterMotor() {
    try {
      DcMotorEx m = hardwareMap.get(DcMotorEx.class, "shooter");
      m.setPower(0.0);
      return m;
    } catch (Exception e) {
      return null;
    }
  }

  private void startShooter() {
    if (shooterMotor == null)
      return;
    double ticksPerSec = SHOOTER_TARGET_RPM * SHOOTER_TICKS_PER_REV / 60.0;
    shooterMotor.setVelocity(-ticksPerSec);
  }

  private void stopShooter() {
    if (shooterMotor == null)
      return;
    shooterMotor.setVelocity(0);
  }

  /**
   * Drives at the given power for the specified duration, then stops.
   * Exits early if the OpMode is stopped.
   */
  private void driveForTime(double forward, double strafe, double rotate, double seconds) {
    timer.reset();
    robot.drive(forward, strafe, rotate);

    while (opModeIsActive() && timer.seconds() < seconds) {
      telemetry.addData("Time", "%.1f / %.1f sec", timer.seconds(), seconds);
      telemetry.update();
      sleep(50);
    }

    robot.stopMovement();
  }

  /**
   * Pauses for the specified duration. Exits early if the OpMode is stopped.
   */
  private void pauseForTime(double seconds) {
    timer.reset();
    while (opModeIsActive() && timer.seconds() < seconds) {
      telemetry.addData("Pause", "%.1f / %.1f sec", timer.seconds(), seconds);
      telemetry.update();
      sleep(50);
    }
  }
}
