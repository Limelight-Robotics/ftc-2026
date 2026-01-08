package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Encapsulates drive motor direction presets using a 4-bit bitmask.
 * Each bit controls whether a motor direction is inverted from baseline.
 * Bit 0: FL, Bit 1: FR, Bit 2: BL, Bit 3: BR
 */
public class DriveDirectionPresets {
    // Base directions for the logical wheel positions
    private static final DcMotor.Direction FL_BASE = DcMotor.Direction.FORWARD;
    private static final DcMotor.Direction FR_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction BL_BASE = DcMotor.Direction.FORWARD;
    private static final DcMotor.Direction BR_BASE = DcMotor.Direction.FORWARD;

    /**
     * Apply base directions to motors.
     */
    public void applyTo(DcMotor fl, DcMotor fr, DcMotor bl, DcMotor br) {
        fl.setDirection(FL_BASE);
        fr.setDirection(FR_BASE);
        bl.setDirection(BL_BASE);
        br.setDirection(BR_BASE);
    }
}
