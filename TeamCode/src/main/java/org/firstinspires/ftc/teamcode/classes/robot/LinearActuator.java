package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * LinearActuator encapsulates a linear motor (DC motor for
 * extension/retraction).
 * Supports target position control and power-based movement.
 */
public class LinearActuator {
    private DcMotor motor;
    private double lastPower = 0.0;
    private int targetPosition = 0;
    private boolean isMovingToTarget = false;
    private final String hardwareName;

    /**
     * Create a linear actuator with the specified hardware name.
     * 
     * @param hardwareName
     *            The name of the motor in the hardware map
     */
    public LinearActuator(String hardwareName) {
        if (hardwareName == null || hardwareName.isEmpty())
            throw new IllegalArgumentException("hardwareName cannot be null or empty");
        this.hardwareName = hardwareName;
    }

    /**
     * Check if the actuator is initialized.
     * 
     * @throws IllegalStateException
     *             if not initialized
     * @return if initialized
     */
    public boolean isInitialized() {
        if (motor == null) {
            throw new IllegalStateException("Motor not initialized. Call init() first");
        }
        return true;
    }

    /**
     * Initialize the linear actuator from the hardware map.
     * 
     * @param hardwareMap
     *            The hardware map containing the motor
     */
    public void init(HardwareMap hardwareMap) {
        if (hardwareMap == null)
            throw new IllegalArgumentException("hardwareMap cannot be null");

        motor = hardwareMap.get(DcMotor.class, hardwareName);
        if (motor == null)
            throw new IllegalStateException("Motor '" + hardwareName + "' not found in hardware map");
    }

    /**
     * Set the power of the linear actuator.
     * 
     * @param power
     *            Power value between -1.0 and 1.0
     */
    public void setPower(double power) {
        isInitialized();
        double clampedPower = Math.max(-1.0, Math.min(1.0, power));
        motor.setPower(clampedPower);
        lastPower = clampedPower;
    }

    /**
     * Stop the linear actuator (set power to 0).
     */
    public void stop() {
        setPower(0.0);
    }

    /**
     * Get the last set power value.
     * 
     * @return The power value
     */
    public double getPower() {
        return lastPower;
    }

    /**
     * Get the hardware name of this actuator.
     * 
     * @return The hardware name
     */
    public String getHardwareName() {
        return hardwareName;
    }

    /**
     * Set a target position for the motor.
     * 
     * @param position
     *            The target encoder position
     */
    public void setTargetPosition(int position) {
        isInitialized();
        targetPosition = position;
        motor.setTargetPosition(position);
    }

    /**
     * Move to the target position using RUN_TO_POSITION mode.
     * 
     * @param power
     *            The power to use for movement (0.0 to 1.0)
     */
    public void moveToTarget(double power) {
        isInitialized();
        double clampedPower = Math.max(0.0, Math.min(1.0, power));
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(clampedPower);
        lastPower = clampedPower;
        isMovingToTarget = true;
    }

    /**
     * Check if the motor is currently busy moving to target.
     * 
     * @return True if motor is moving to target position
     */
    public boolean isBusy() {
        isInitialized();
        return motor.isBusy();
    }

    /**
     * Get the current motor position.
     * 
     * @return The current encoder position
     */
    public int getCurrentPosition() {
        isInitialized();
        return motor.getCurrentPosition();
    }

    /**
     * Get the target position.
     * 
     * @return The target encoder position
     */
    public int getTargetPosition() {
        return targetPosition;
    }

    /**
     * Check if the actuator is moving to a target position.
     * 
     * @return True if currently in target position mode
     */
    public boolean isMovingToTarget() {
        return isMovingToTarget;
    }

    /**
     * Reset the motor encoder position to zero.
     */
    public void resetEncoder() {
        isInitialized();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
}
