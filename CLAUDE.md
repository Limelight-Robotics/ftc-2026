# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FTC (FIRST Tech Challenge) robotics code for Limelight Robotics 2025-2026 season. The code compiles to an Android APK deployed to robot hardware via ADB.

## Build Commands

```bash
./gradlew assembleDebug      # Build debug APK
./build-and-deploy.sh        # Build and deploy to connected robot via ADB
./adb-connect.sh             # Connect to robot via ADB
```

The build script outputs APK to `TeamCode/build/outputs/apk/debug/TeamCode-debug.apk`.

## Main Code Location

All team-specific code lives in:
```
TeamCode/src/main/java/org/firstinspires/ftc/teamcode/
```

## Architecture

### Robot Abstraction Layer

- **`Robot` interface** (`classes/Robot.java`): Defines robot capabilities (drive, intake, localizer access)
- **`DefaultRobot`** (`classes/DefaultRobot.java`): Concrete implementation that initializes subsystems and gracefully handles missing hardware via try-catch

### Drive System

- **`MecanumDrive`**: RoadRunner-based motion planning with configurable parameters in `Params` inner class (track width, encoder ticks, feedforward gains)
- **`DriveSubsystem`**: Lower-level motor control with mecanum math normalization
- **`DriveDirectionPresets`**: Motor direction configuration

### Localizers (multiple implementations, fallback chain)

- `MecanumDrive` (primary): RoadRunner with IMU + motor encoders
- `ThreeDeadWheelLocalizer` / `TwoDeadWheelLocalizer`: Dead wheel odometry
- `OTOSLocalizer` / `PinpointLocalizer`: External sensor integration

### OpMode Patterns

**Teleop**: Extend `BaseManualDrive` and override `getSpeedMultiplier()` for speed variants.

Gamepad 1 Controls:
| Control | Action |
|---------|--------|
| Left stick Y | Forward/backward |
| Left stick X | Strafe left/right |
| Right stick X | Rotation |
| Left bumper | Intake reverse |
| Right bumper | Intake forward |
| Left trigger | Turret left |
| Right trigger | Turret right |
| D-pad left | Cable drive reverse |
| D-pad right | Cable drive forward |

Speed variants:
- `ManualDrive`: Full speed (1.0x)
- `SlowManualDrive`: 10% speed (0.1x)

Hardware names for teleop: `"intake"`, `"turret"`, `"cableDrive"` (all optional with null-safe handling)

**Autonomous**: Direct `MecanumDrive` initialization with start pose, RoadRunner trajectory building (`.lineToY()`, `.turn()`, etc.), blocking action execution via `Actions.runBlocking()`.

### Key Directories

- `autonomous/`: Four autonomous modes (BlueLeft, BlueRight, RedLeft, RedRight)
- `teleop/`: Manual control OpModes with speed variants
- `classes/robot/`: Drive subsystem, status tracking, movement results
- `tuning/`: RoadRunner tuning utilities (LocalizationTest, SplineTest, ManualFeedbackTuner)
- `messages/`: DTOs for drive commands and localizer data

## Conventions

- Hardware names: `"leftFront"`, `"rightFront"`, `"leftBack"`, `"rightBack"`
- Optional hardware wrapped in try-catch with null checks
- `@Config` annotation enables FTC Dashboard parameter tuning
- `Utilities.java` contains mecanum normalization and physics helpers

## Limelight 3A Vision System

### Hardware Configuration

Hardware name: `"limelight"` (configure in Driver Station robot configuration)

### Imports

```java
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
```

### Initialization

```java
Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
limelight.setPollRateHz(100);  // Poll 100 times per second
limelight.start();
```

### Pipeline Management

Switch between pipelines (0-9, configured in web interface at http://limelight.local:5801):

```java
limelight.pipelineSwitch(0);  // Fire-and-forget, executes immediately
```

### Getting Results

```java
LLResult result = limelight.getLatestResult();
if (result != null && result.isValid()) {
    double tx = result.getTx();  // Horizontal offset from crosshair (-29.8 to 29.8 degrees)
    double ty = result.getTy();  // Vertical offset from crosshair (-24.85 to 24.85 degrees)
    double ta = result.getTa();  // Target area (0% to 100% of image)
    double staleness = result.getStaleness();  // Milliseconds since last update
}
```

### AprilTag/Fiducial Detection

```java
List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
for (LLResultTypes.FiducialResult fiducial : fiducials) {
    int tagId = fiducial.getFiducialId();
    Pose3D robotPose = fiducial.getRobotPoseTargetSpace();  // Pose relative to tag
}
```

### MegaTag Robot Localization

**MegaTag 1** - Field position from AprilTags alone:

```java
if (result != null && result.isValid()) {
    Pose3D botpose = result.getBotpose();
    if (botpose != null) {
        double x = botpose.getPosition().x;  // Field X coordinate
        double y = botpose.getPosition().y;  // Field Y coordinate
    }
}
```

**MegaTag 2** - Enhanced accuracy with IMU fusion:

```java
// First, send robot heading to Limelight
double robotYaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
limelight.updateRobotOrientation(robotYaw);

if (result != null && result.isValid()) {
    Pose3D botpose_mt2 = result.getBotpose_MT2();
    // Use botpose_mt2 for more accurate localization
}
```

### Coordinate System

- Field origin (0,0,0) is center of field floor
- Standard FTC coordinate system
- 0° yaw: blue alliance on left, red alliance on right

### Additional Features

```java
limelight.captureSnapshot("snapshot_name");  // Save image for debugging
limelight.updatePythonInputs(double[] inputs);  // Send data to Python pipeline
double[] pythonOutput = result.getPythonOutput();  // Get Python pipeline output
```

### Documentation Links

- [FTC Programming Guide](https://docs.limelightvision.io/docs/docs-limelight/apis/ftc-programming)
- [Limelight 3A Quick-Start](https://docs.limelightvision.io/docs/docs-limelight/getting-started/limelight-3a)
- [Limelight Sample FTC Code](https://github.com/FIRST-Tech-Challenge/FtcRobotController/blob/master/FtcRobotController/src/main/java/org/firstinspires/ftc/robotcontroller/external/samples/SensorLimelight3A.java)
- [Estimating distance to AprilTags](https://docs.limelightvision.io/docs/docs-limelight/tutorials/tutorial-estimating-distance)
- [FTC Limelight Javadoc](https://javadoc.io/doc/org.firstinspires.ftc/Hardware/latest/com/qualcomm/hardware/limelightvision/package-summary.html)

## RoadRunner 1.0 Motion Planning

RoadRunner is an autonomous driving library for FTC that provides trajectory generation, motion profiling, and localization.

### Key Imports

```java
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SleepAction;
```

### Initialization

```java
// Define starting pose (x, y, heading in radians)
Pose2d initialPose = new Pose2d(11.8, 61.7, Math.toRadians(90));
MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);
```

### Building Trajectories

Use `TrajectoryActionBuilder` with fluent method chaining:

```java
Action trajectoryAction = drive.actionBuilder(initialPose)
    .lineToY(33)                                    // Drive to Y coordinate
    .lineToX(48)                                    // Drive to X coordinate
    .splineTo(new Vector2d(48, 12), Math.PI)        // Spline with end tangent
    .strafeTo(new Vector2d(36, 12))                 // Strafe to position
    .turn(Math.toRadians(90))                       // Turn in place
    .waitSeconds(2)                                 // Pause execution
    .build();
```

### Trajectory Methods

**Line-based movements:**
- `.lineToX(x)` / `.lineToY(y)` - Move to coordinate
- `.lineToXSplineHeading(x, heading)` - Line with spline heading interpolation
- `.lineToYSplineHeading(y, heading)` - Line with spline heading interpolation
- `.strafeTo(Vector2d)` - Strafe to absolute position

**Spline-based movements:**
- `.splineTo(Vector2d, endTangent)` - Smooth spline curve
- `.splineToConstantHeading(Vector2d, endTangent)` - Spline maintaining heading
- `.splineToLinearHeading(Pose2d, endTangent)` - Spline with linear heading
- `.splineToSplineHeading(Pose2d, endTangent)` - Spline with spline heading

**Simple movements:**
- `.forward(distance)` / `.back(distance)`
- `.strafeLeft(distance)` / `.strafeRight(distance)`
- `.turn(radians)` - Turn in place
- `.waitSeconds(seconds)` - Pause

**Trajectory chaining:**
- `.endTrajectory().fresh()` - End current trajectory and start fresh from endpoint

### Running Actions

```java
// Block and execute a single action
Actions.runBlocking(trajectoryAction);

// Sequential execution (one after another)
Actions.runBlocking(new SequentialAction(
    drive.actionBuilder(startPose).lineToY(30).build(),
    new SleepAction(1.0),
    drive.actionBuilder(newPose).lineToX(40).build()
));

// Parallel execution (simultaneous)
Actions.runBlocking(new ParallelAction(
    driveAction,
    liftAction,
    clawAction
));
```

### Custom Actions

Implement the `Action` interface for custom behaviors:

```java
public Action spinUp() {
    return new Action() {
        private boolean initialized = false;

        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            if (!initialized) {
                motor.setPower(0.8);
                initialized = true;
            }
            double vel = motor.getVelocity();
            packet.put("velocity", vel);  // Sent to FTC Dashboard
            return vel < 10000.0;  // Return true to continue, false when done
        }
    };
}
```

### Tuning Parameters (in MecanumDrive.Params)

**Distance calibration:**
- `inPerTick` - Distance per encoder tick (forward)
- `lateralInPerTick` - Distance per tick (strafing)
- `trackWidthTicks` - Track width in encoder ticks

**Feedforward gains:**
- `kS` - Static friction (minimum power to move)
- `kV` - Velocity gain (power proportional to velocity)
- `kA` - Acceleration gain (optional refinement)

### Localizer Options

- Drive encoders (default in MecanumDrive)
- `TwoDeadWheelLocalizer` - Two perpendicular dead wheels + IMU
- `ThreeDeadWheelLocalizer` - Three dead wheels (no IMU needed)
- `PinpointLocalizer` - goBILDA Pinpoint Odometry Computer
- `OTOSLocalizer` - SparkFun Optical Tracking Odometry Sensor

### Tuning Process

Run tuning OpModes in order via FTC Dashboard (`http://192.168.43.1:8080/dash`):
1. ForwardPushTest → determine `inPerTick`
2. LateralPushTest → determine `lateralInPerTick`
3. ForwardRampLogger → calculate `kS` and `kV`
4. AngularRampLogger → calculate `trackWidthTicks`
5. ManualFeedforwardTuner → fine-tune feedforward
6. ManualFeedbackTuner → optimize feedback controller
7. SplineTest → validate all tuning

### Documentation Links

- [RoadRunner 1.0 Docs](https://rr.brott.dev/docs/v1-0/)
- [API Javadocs](https://rr.brott.dev/docs/v1-0/api-docs/)
- [Quickstart Repository](https://github.com/acmerobotics/road-runner-quickstart)
- [Actions Guide](https://rr.brott.dev/docs/v1-0/actions/)
- [Tuning Guide](https://rr.brott.dev/docs/v1-0/tuning/)
- [Building Autonomous Guide](https://rr.brott.dev/docs/v1-0/guides/centerstage-auto/)
- [Learn Road Runner (0.5.x legacy)](https://learnroadrunner.com/)

## Dependencies

- FTC SDK 11.0.0
- RoadRunner 1.0.1 (ftc adapter 0.1.25)
- FTC Dashboard 0.5.1
- Java 21, Android SDK 30 (min 24, target 28)
