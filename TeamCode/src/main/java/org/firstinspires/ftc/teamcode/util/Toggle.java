package org.firstinspires.ftc.teamcode.util;

import java.util.function.Consumer;

/**
 * Small reusable rising-edge toggle helper.
 * Usage: call update(currentButtonState) each loop. When the button
 * transitions from false->true the internal state flips and the
 * registered callback is invoked with the new state.
 */
public class Toggle {
    private boolean state = false;
    private boolean lastInput = false;
    private Consumer<Boolean> onChange;

    // Assigned power used when this toggle represents a motor power toggle.
    // Updated by the new update(boolean, double) overload.
    private double assignedPower = 1.0;

    public Toggle() {
    }

    public Toggle(boolean initialState) {
        this.state = initialState;
    }

    public void setOnChange(Consumer<Boolean> callback) {
        this.onChange = callback;
    }

    /**
     * Return the last-assigned power for this toggle. Useful when multiple
     * buttons map to different powers and the onChange callback needs to
     * apply the currently selected power.
     */
    public double getAssignedPower() {
        return assignedPower;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean newState) {
        if (this.state != newState) {
            this.state = newState;
            if (onChange != null)
                onChange.accept(this.state);
        }
    }

    /** Call every loop with the current button pressed state. */
    public void update(boolean inputPressed) {
        update(inputPressed, assignedPower);
    }

    /**
     * Overload that allows specifying the power associated with the button
     * press. Call this from your loop for each button that should control
     * this toggle (e.g. A -> 1.0, B -> -1.0). The most-recently pressed button
     * becomes the assigned power while the toggle is on.
     */
    public void update(boolean inputPressed, double power) {
        if (inputPressed && !lastInput) {
            if (!state) {
                // Currently off -> turn on and record the power that caused it.
                state = true;
                assignedPower = power;
            } else {
                // Currently on. If the same power button was pressed, toggle off.
                // If a different button was pressed, switch the assigned power
                // while remaining on.
                if (Double.compare(assignedPower, power) == 0) {
                    state = false;
                } else {
                    assignedPower = power;
                }
            }
            if (onChange != null)
                onChange.accept(state);
        }
        lastInput = inputPressed;
    }
}
