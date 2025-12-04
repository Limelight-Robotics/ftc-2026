package org.firstinspires.ftc.teamcode.teleop;

/**
 * Enum for intake control states.
 */
public enum IntakeMode {
    REGULAR,
    REVERSE,
    OFF;

    /** Cycle to the next intake mode in the sequence. */
    public IntakeMode next() {
        switch (this) {
            case REGULAR: return REVERSE;
            case REVERSE: return OFF;
            case OFF:
            default: return REGULAR;
        }
    }

    /** Get motor power for this mode. */
    public double getPower() {
        switch (this) {
            case REGULAR: return 1.0;
            case REVERSE: return -1.0;
            case OFF:
            default: return 0.0;
        }
    }
}
