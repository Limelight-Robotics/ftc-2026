# Limelight Robotics 2026 Season
The source code for the robot in the 2025-2026 season.

## Table of Contents
- [Configuration](#control-hub-configurations)
- [Scripts](#scripts)

# Control Hub Configurations
## Control Hub
### Motors
GoBilda 5202/3/4 series
- 0 - rightFront
- 1 - leftBack
- 2 - rightBack
- 3 - leftFront

### Servos
- 0 - uptake

## Expansion Hub
### Motors
GoBilda 5202/3/4 series
- 0 - shooter
- 1 - intake
- 2 - turret

## Scripts
### `./build-and-deploy.sh`
Build and/or deploy the robot code.

* `--clean` - Clean build output, then build and/or deploy
* `--build-only` - Only build, don't deploy

### `./format.sh`
Format all Java files in `TeamCode/`.
