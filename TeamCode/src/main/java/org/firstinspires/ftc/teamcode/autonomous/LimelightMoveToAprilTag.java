package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.classes.Robot;
import org.firstinspires.ftc.teamcode.classes.Vision;

@Autonomous(name = "Limelight3A Move to April Tag", group = "April Tag")
public class LimelightMoveToAprilTag extends OpMode {

    // Robot and vision systems
    private final Robot robot = Robot.createDefault();
    private final Vision vision = Vision.createDefault();

    @Override
    public void init() {
        robot.init(hardwareMap, vision);
    }

    @Override
    public void start() {
        vision.start();
    }

    @Override
    public void loop() {
        Vision.TargetData targetData = vision.processFrame();
        if (targetData != null && targetData.isAcquired) {
            robot.moveToAprilTag(targetData);
        } else {
            robot.stopMovement();
        }

        // Render telemetry from the OpMode using RobotStatus and VisionStatus
        org.firstinspires.ftc.teamcode.classes.robot.RobotStatus status = robot.getStatus();
        telemetry.addData("Drive Powers", "FL: %.2f, FR: %.2f, BL: %.2f, BR: %.2f",
                status.getFrontLeftPower(), status.getFrontRightPower(), status.getBackLeftPower(),
                status.getBackRightPower());
        telemetry.addData("Drive Inputs", "Forward: %.2f, Strafe: %.2f, Rotate: %.2f", status.getLastForward(),
                status.getLastStrafe(), status.getLastRotate());
        if (targetData != null) {
            telemetry.addData("Target Acquired", "%s", targetData.isAcquired ? "YES" : "NO");
            if (targetData.isAcquired)
                telemetry.addData("Target (X,Y,Z)", "%.2f, %.2f, %.2f", targetData.xPosition,
                        targetData.yPosition, targetData.zPosition);
        }

        // Also surface vision status if needed
        org.firstinspires.ftc.teamcode.classes.vision.VisionStatus vstatus = vision.getStatus();
        if (vstatus != null) {
            telemetry.addData("Vision Pipeline", "%s", vstatus.getCurrentPipeline().name());
            telemetry.addData("NoTargetFrames", "%d", vstatus.getConsecutiveNoTargetFrames());
        }
        telemetry.update();
    }

    @Override
    public void stop() {
        vision.stop();
        robot.stopMovement();
    }
}
