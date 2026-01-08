
package org.firstinspires.ftc.teamcode.classes;

import org.firstinspires.ftc.teamcode.classes.DefaultRobot;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.classes.robot.DriveSubsystem;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatusSnapshot;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.classes.robot.DriveSubsystem;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatus;
import org.firstinspires.ftc.teamcode.classes.robot.RobotStatusSnapshot;


/**
 * Robot interface for teleop control. For autonomous, use MecanumDrive directly
 * with RoadRunner trajectories.
 */
public interface Robot {
    /** Initialize robot hardware from hardware map. */
    void init(HardwareMap hardwareMap);

    /** Drive the robot. */

    void drive(double forward, double strafe, double rotate);
    void driveWithGamepad(double forward, double strafe, double rotate);

    /** Stop all movement immediately. */
    void stopMovement();

    /** Get current robot status snapshot. */
    RobotStatus getStatus();

    /** Set intake motor power (-1.0 to 1.0). */
    void setIntakePower(double power);
    
    /** Update localizer and return current pose. */
    Pose2d updateLocalizer();
    
    /** Get current robot pose from localizer. */
    Pose2d getPose();
    
    /** Get current robot velocity from localizer. */
    PoseVelocity2d getVelocity();
    
    /** Reset pose to a new value. */
    void setPose(Pose2d pose);

    /** Factory method for default implementation. */
    static Robot createDefault() {
        return new DefaultRobot();
    }
}

