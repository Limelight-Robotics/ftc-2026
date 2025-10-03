# TeamCode Module
This module contains the source code for the robot! We don't want to edit anything in the `./FtcRobotController/` folder, as that is the base code provided by FIRST. Instead, we will be editing code in this module.

## Classes
- [Robot](classes/Robot.java): The main Robot controller.
- [Vision](classes/Vision.java): The main Vision controller.

## TeleOps
- [Manual Drive](teleop/ManualDrive.java): The main manual TeleOp used for driving the robot.
- [Slow Manual Drive](teleop/SlowManualDrive.java): The main manual TeleOp, but 2x slower movement.

## Autonomous
- [Limelight Move to April Tag](autonomous/LimelightMoveToAprilTag.java): Autonomous OpMode for movement utilizing the Limelight3A camera.