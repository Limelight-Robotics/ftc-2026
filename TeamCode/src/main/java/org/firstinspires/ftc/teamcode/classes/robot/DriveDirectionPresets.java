package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Encapsulates drive motor direction presets using a 4-bit bitmask.
 * Each bit controls whether a motor direction is inverted from baseline.
 * Bit 0: FL, Bit 1: FR, Bit 2: BL, Bit 3: BR
 */
public class DriveDirectionPresets {
    private static final DcMotor.Direction FL_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction FR_BASE = DcMotor.Direction.FORWARD;
    private static final DcMotor.Direction BL_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction BR_BASE = DcMotor.Direction.FORWARD;
    private static final int BITMASK = 0xF;
    private static final int DEFAULT_PRESET = 3;

    private int preset = DEFAULT_PRESET & BITMASK;

    public int getPreset() {
        return preset & BITMASK;
    }

    public void setPreset(int idx) {
        this.preset = idx & BITMASK;
    }

    public void cyclePreset() {
        this.preset = (this.preset + 1) & BITMASK;
    }

    /**
     * Apply preset to motors. Null motors are safely ignored.
     */
    public void applyTo(DcMotor fl, DcMotor fr, DcMotor bl, DcMotor br) {
        boolean flFlip = (preset & 0x1) != 0;
        boolean frFlip = (preset & 0x2) != 0;
        boolean blFlip = (preset & 0x4) != 0;
        boolean brFlip = (preset & 0x8) != 0;

        if (fl != null) fl.setDirection(flFlip ? invert(FL_BASE) : FL_BASE);
        if (fr != null) fr.setDirection(frFlip ? invert(FR_BASE) : FR_BASE);
        if (bl != null) bl.setDirection(blFlip ? invert(BL_BASE) : BL_BASE);
        if (br != null) br.setDirection(brFlip ? invert(BR_BASE) : BR_BASE);
    }

    /** Returns human-readable string of current preset directions. */
    public String getPresetString() {
        String flDir = ((preset & 0x1) != 0 ? invert(FL_BASE) : FL_BASE).toString();
        String frDir = ((preset & 0x2) != 0 ? invert(FR_BASE) : FR_BASE).toString();
        String blDir = ((preset & 0x4) != 0 ? invert(BL_BASE) : BL_BASE).toString();
        String brDir = ((preset & 0x8) != 0 ? invert(BR_BASE) : BR_BASE).toString();
        return String.format("Preset %d: FL=%s FR=%s BL=%s BR=%s", preset, flDir, frDir, blDir, brDir);
    }

    private DcMotor.Direction invert(DcMotor.Direction d) {
        return d == DcMotor.Direction.FORWARD 
            ? DcMotor.Direction.REVERSE 
            : DcMotor.Direction.FORWARD;
    }
}
