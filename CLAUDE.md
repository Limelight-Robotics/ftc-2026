# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FTC Robot Controller SDK (v11.1) — a multi-module Android application for FIRST Tech Challenge robotics. This is the DECODE 2025-2026 season codebase. Upstream repo: https://github.com/FIRST-Tech-Challenge/FtcRobotController

**All development happens in `TeamCode/` only.** Everything else is upstream SDK code and should not be modified.

## Build Commands

```bash
./gradlew build                      # Build entire project
./gradlew TeamCode:assembleDebug     # Build TeamCode debug APK
./gradlew TeamCode:installDebug      # Build and install on connected device
./gradlew clean                      # Clean build artifacts
```

No test infrastructure is currently configured. If tests are added, place them in `TeamCode/src/test/java/` and add JUnit dependencies to `TeamCode/build.gradle`.

## Architecture

### Two-Module Structure

- **FtcRobotController/** — SDK library module. Contains core infrastructure and 66+ sample OpModes. **Do not modify this module** — it receives upstream SDK updates.
- **TeamCode/** — Application module where all custom robot code goes. Depends on FtcRobotController. This is the only module that should be edited.

```
TeamCode (Application) → FtcRobotController (Library) → FTC SDK Maven artifacts
```

### Build Configuration

Shared build config is split across root-level files:
- `build.common.gradle` — compileSdk 30, minSdk 24, targetSdk 28, Java 8, NDK 21.3.6528147
- `build.dependencies.gradle` — FTC SDK library dependencies (RobotCore, Hardware, Vision, etc.)
- Version info is extracted from `FtcRobotController/src/main/AndroidManifest.xml` (single source of truth)

### OpMode Development

All team OpModes go in `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/`.

**OpMode types:**
- `LinearOpMode` — sequential execution (most common for team code)
- Iterative `OpMode` — init/loop/stop callbacks

**Annotations:** Use `@TeleOp(name="...", group="...")` or `@Autonomous(name="...", group="...")`. Remove `@Disabled` to make an OpMode visible on the Driver Station.

**Samples location:** `FtcRobotController/src/main/java/org/firstinspires/ftc/robotcontroller/external/samples/` — copy samples into TeamCode, don't edit them in place.

### Hardware Naming Conventions

Standard device names configured via Driver Station:
- Motors: `left_drive`, `right_drive`, `left_arm`, `arm`
- Servos: `left_hand`, `right_hand`, `claw`
- Sensors: `sensor_color`, `sensor_range`, `sensor_touch`, `imu`

## Controller Bindings (PS5 / DualSense)

We use PS5 controllers. When referencing gamepad buttons in code comments, telemetry, or documentation, use PS5 naming:

| PS5 Button | FTC SDK Field         |
|------------|-----------------------|
| Cross (X)  | `gamepad1.cross`      |
| Circle (O) | `gamepad1.circle`     |
| Square     | `gamepad1.square`     |
| Triangle   | `gamepad1.triangle`   |
| L1         | `gamepad1.left_bumper` |
| R1         | `gamepad1.right_bumper`|
| L2         | `gamepad1.left_trigger`|
| R2         | `gamepad1.right_trigger`|
| L3         | `gamepad1.left_stick_button`|
| R3         | `gamepad1.right_stick_button`|
| D-pad      | `gamepad1.dpad_up/down/left/right`|
| Left Stick | `gamepad1.left_stick_x/y`|
| Right Stick| `gamepad1.right_stick_x/y`|
| Share      | `gamepad1.share`      |
| Options    | `gamepad1.options`    |
| Touchpad   | `gamepad1.touchpad`   |

**Important:** Use `cross`/`circle`/`square`/`triangle` (PS5 names), not `a`/`b`/`x`/`y` (Xbox names) in code.

## Constraints

- **Java 8 only** — required for OnBotJava compatibility
- **ABI targets:** armeabi-v7a, arm64-v8a only
- Requires Android Studio Ladybug (2024.2) or later
- Gradle wrapper version: 8.9

## Resources

- FTC SDK docs: https://ftc-docs.firstinspires.org/
- Javadoc: https://javadoc.io/doc/org.firstinspires.ftc
- Community forum: https://ftc-community.firstinspires.org/