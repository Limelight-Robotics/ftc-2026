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

    public Toggle() {
    }

    public Toggle(boolean initialState) {
        this.state = initialState;
    }

    public void setOnChange(Consumer<Boolean> callback) {
        this.onChange = callback;
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
        if (inputPressed && !lastInput) {
            state = !state;
            if (onChange != null)
                onChange.accept(state);
        }
        lastInput = inputPressed;
    }
}
