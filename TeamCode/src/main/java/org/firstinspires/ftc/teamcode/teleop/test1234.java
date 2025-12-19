package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "test1234hTest", group = "Test")
public class test1234 extends LinearOpMode {
    
    private static final String MOTOR_NAME = "test";
    private static final String MOTOR_2_NAME = "test2";
    
    private static final double MIN_MOTOR_POWER = 0.2;
    private static final double MAX_MOTOR_POWER = 1.0;

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor motor = null;
    private DcMotor motor2 = null; 

    @Override
    public void runOpMode() throws InterruptedException {
        if (initializeMotor()) {
            waitForStart();
            
            while (opModeIsActive()) {
                // 1. Move Slowly (Now using Encoder Velocity)
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor.setPower(MIN_MOTOR_POWER);
                motor2.setPower(-MIN_MOTOR_POWER);
                sleep(250);

                sleep(2000);
                
                motor.setTargetPosition(motor.getCurrentPosition());
                motor2.setTargetPosition(motor2.getCurrentPosition());
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setPower(1.0);
                motor2.setPower(1.0);
                sleep(2000);

                // 3. Move Fast (Now using Encoder Velocity)
                motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                motor.setPower(MAX_MOTOR_POWER);
                motor2.setPower(-MAX_MOTOR_POWER);
                sleep(250);
                
                // 4. Lock Position
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setPower(1.0);
                motor2.setPower(1.0);
                sleep(2000);
            }
        }
    }

    private boolean initializeMotor() {
        try {
            motor = hardwareMap.get(DcMotor.class, MOTOR_NAME);
            motor2 = hardwareMap.get(DcMotor.class, MOTOR_2_NAME);
            
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            
            // Set initial mode to use encoders
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
