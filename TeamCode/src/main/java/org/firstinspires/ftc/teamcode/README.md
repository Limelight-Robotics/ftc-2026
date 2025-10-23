# TeamCode Module

What this module is
- Contains your OpModes, robot interfaces, and implementations. Don't edit `FtcRobotController/` — that's FIRST-provided code.

Key concepts
- `Robot` (interface) — high-level robot lifecycle and movement API. Use `Robot.createDefault()` to get the default implementation.
- `Vision` (interface) — camera/processing API. Use `Vision.createDefault()` for the Limelight-backed implementation.
- Implementations live in subpackages to keep OpModes decoupled:
	- `org.firstinspires.ftc.teamcode.classes.robot` — DefaultRobot, DriveSubsystem, MovementResult
	- `org.firstinspires.ftc.teamcode.classes.vision` — LimelightVision

Where to find things
- OpModes (examples):
	- `teleop/ManualDrive.java`
	- `teleop/SlowManualDrive.java`
	- `autonomous/LimelightMoveToAprilTag.java`
- Interfaces:
	- `src/main/java/org/firstinspires.ftc.teamcode/classes/Robot.java`
	- `src/main/java/org/firstinspires.ftc.teamcode/classes/Vision.java`

Hardware names (defaults)
- Drive motors: `fL`, `bL`, `fR`, `bR` (front-left, back-left, front-right, back-right)
- Limelight hardware name: `limelight`
	(If your robot uses different names, update the implementation or the Robot Controller hardware configuration.)

Build
- From repo root:
	- ./build-and-deploy

Notes
- OpModes should depend only on the `Robot` and `Vision` interfaces by using the `createDefault()` factories.
- Run a full build after pulling refactors to catch any remaining import or compile issues.
