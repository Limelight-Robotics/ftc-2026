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

**Teleop**: Extend `BaseManualDrive` and override `getSpeedMultiplier()` for speed variants. Joystick mapping: left stick for forward/strafe, right stick X for rotation.

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

## Dependencies

- FTC SDK 11.0.0
- RoadRunner 1.0.1 (ftc adapter 0.1.25)
- FTC Dashboard 0.5.1
- Java 21, Android SDK 30 (min 24, target 28)
