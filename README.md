# Limelight Robotics 2026 Season
The source code for the robot in the 2025-2026 season.

## Table of Contents
- [Configuration](#control-hub-configurations)
- [Controller Map](#controller-map)
- [Scripts](#scripts)
- [ADB WiFi Debugging](#adb-wifi-debugging)

# Control Hub Configurations
## Control Hub
### Motors
GoBilda 5202/3/4 series
- 0 - rightFront
- 1 - leftBack
- 2 - rightBack
- 3 - leftFront

### Servos
- 0 - loader

## Expansion Hub
### Motors
GoBilda 5202/3/4 series
- 0 - shooter
- 1 - intake
- 2 - turret

# Controller Map
All controls use **Gamepad 1** (PS5 DualSense).

## Main Drive (ManualDrive / SlowManualDrive)

```
            ┌─────────────────────────────────────────────┐
            │               PS5 DualSense                 │
            │                                             │
            │    ┌───┐                         ┌───┐     │
            │    │L1 │  Turret CW     Turret  │R1 │     │
            │    │   │                  CCW   │   │     │
            │    └───┘                         └───┘     │
            │    ┌───┐                         ┌───┐     │
            │    │L2 │                Shooter  │R2 │     │
            │    │   │                Spin-up  │   │     │
            │    └───┘                         └───┘     │
            │                                             │
            │     ┌───┐                    ╔═══╗         │
            │     │ ↑ │ Raise          ┌───╢ △ ║         │
            │   ┌─┼───┼─┐ Loader      │   ╚═══╝         │
            │   │ ←   → │         ┌───┐     ┌───┐       │
            │   └─┼───┼─┘         │ ▢ ├─────┤ ○ │       │
            │     │ ↓ │ Lower     └───┘     └───┘       │
            │     └───┘  Loader    Intake    ┌───┐       │
            │                      Forward   │ ✕ │       │
            │   ╔═══════╗                    └───┘       │
            │   ║ L Joy ║                   Intake       │
            │   ║       ║     ╔═══════╗     Reverse      │
            │   ║  F/B  ║     ║ R Joy ║                  │
            │   ║Strafe ║     ║       ║                  │
            │   ╚═══════╝     ║Rotate ║                  │
            │                 ╚═══════╝                  │
            └─────────────────────────────────────────────┘
```

| Control | Action |
|---------|--------|
| Left Stick Y | Forward / Backward |
| Left Stick X | Strafe Left / Right |
| Right Stick X | Rotate |
| Cross (✕) | Intake Reverse |
| Square (▢) | Intake Forward |
| R2 Trigger | Shooter Spin-up (auto-raises loader at speed) |
| R1 Bumper | Rotate Turret CCW |
| L1 Bumper | Rotate Turret CW |
| D-pad Up | Raise Loader |
| D-pad Down | Lower Loader |

## Diagnostic OpModes

### Debug Motors
| Control | Action |
|---------|--------|
| Triangle (△) | Front Left motor |
| Circle (○) | Front Right motor |
| Square (▢) | Back Left motor |
| Cross (✕) | Back Right motor |
| L1 (hold) | Reverse direction |

### Manual Shooter Control
| Control | Action |
|---------|--------|
| R2 Trigger | Shooter forward (0–50%) |
| L2 Trigger | Shooter reverse (0–50%) |

### Launcher RPM Tuner
| Control | Action |
|---------|--------|
| D-pad Up | +500 RPM |
| D-pad Down | -500 RPM |
| D-pad Right | +100 RPM |
| D-pad Left | -100 RPM |
| Cross (✕) | Toggle motor on/off |
| Circle (○) | Emergency stop |

# Scripts
### `./build-and-deploy.sh`
Build and/or deploy the robot code.

* `--clean` - Clean build output, then build and/or deploy
* `--build-only` - Only build, don't deploy

### `./format.sh`
Format all Java files in `TeamCode/`.

# ADB WiFi Debugging
If ADB over WiFi stops working or can't connect to the Control Hub, reset the connection:

```bash
adb kill-server
adb start-server
adb connect 192.168.43.1:5555
./gradlew installDebug
```