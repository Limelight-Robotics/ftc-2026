package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Motor6thTest", group = "Test")
public class Motor6thTest extends LinearOpMode {
    
    private static final String MOTOR_NAME = "test";
    private static final String MOTOR_2_NAME = "test2";
    
    private static final double MIN_MOTOR_POWER = 0.2;
    private static final double MAX_MOTOR_POWER = 1.0;
    private static final long MAIN_SLEEP = 250;

    private final ElapsedTime runtime = new ElapsedTime();
    private DcMotor motor = null;
    private DcMotor motor2 = null; 

    @Override
    public void runOpMode() throws InterruptedException {
        if (!initializeMotor()) {
            return; 
        }

        // Initialize a counter variable (not 'final' so we can change it)
        int count = 0;

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        waitForStart();
        runtime.reset();
        
        if (opModeIsActive()) {
            
            // This loop will run 10 times
            while (opModeIsActive()) {
                
                // Step 1: Forward
                motor.setPower(MIN_MOTOR_POWER);
                motor2.setPower(-MIN_MOTOR_POWER);
                sleep(20);
                motor.getCurrentPosition();
                motor2.getCurrentPosition();

                motor.setTargetPosition(motor.getCurrentPosition() );
                motor2.setTargetPosition(motor2.getCurrentPosition() );
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setPower(1.0);
                motor2.setPower(1.0);
                sleep(1000);
            
                motor.setPower(MAX_MOTOR_POWER);
                motor2.setPower(-MAX_MOTOR_POWER);
                sleep(200);
                
                telemetry.addData("Loop Count", count);
                telemetry.addData("Message", "done");
                telemetry.update();
            }
            
            // Stop
            setBothPower(0.0);

            telemetry.addData("Message", "Test Completed!");
            telemetry.update();
            
            sleep(2000); 
        }
    }

    private boolean initializeMotor() {
        try {
            motor = hardwareMap.get(DcMotor.class, MOTOR_NAME);
            motor2 = hardwareMap.get(DcMotor.class, MOTOR_2_NAME);
            motor.setPower(0.0);
            motor2.setPower(0.0);
            return true;
        } catch (Exception e) {
            telemetry.addData("Error", "Check motor config names 'test' and 'test2'");
            telemetry.update();
            return false;
        }
    }

    private void setBothPower(double power) {
        if (motor != null) motor.setPower(power);
        if (motor2 != null) motor2.setPower(power);
        updateTelemetry(power);
    }

    private void updateTelemetry(double power) {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motor Power", "%.2f", power);
        telemetry.update();
    }
}
