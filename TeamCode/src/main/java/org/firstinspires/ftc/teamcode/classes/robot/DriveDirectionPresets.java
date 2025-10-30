package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Encapsulates drive motor direction presets (per-motor flips) so
 * DriveSubsystem
 * can toggle or apply them cleanly.
 */
public class DriveDirectionPresets {
    // current preset bitmask (bits: 0=FL,1=FR,2=BL,3=BR)
    private int preset = 3;

    // Baseline directions used when a bit is 0
    private static final DcMotor.Direction FL_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction FR_BASE = DcMotor.Direction.FORWARD;
    private static final DcMotor.Direction BL_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction BR_BASE = DcMotor.Direction.FORWARD;

    public DriveDirectionPresets() {
    }

    public int getPreset() {
        return preset & 0xF;
    }

    public void setPreset(int idx) {
        this.preset = idx & 0xF;
    }

    public void cyclePreset() {
        this.preset = (this.preset + 1) & 0xF;
    }

    /**
     * Apply the currently selected preset to the provided motors. If any motor
     * is null this method will not set direction for that motor.
     */
    public void applyTo(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight) {
        // TODO: What does this even mean? %%Zander Lewis <zander@zanderlewis.dev>%%
        boolean flBit = (preset & 0x1) != 0;
        boolean frBit = (preset & 0x2) != 0;
        boolean blBit = (preset & 0x4) != 0;
        boolean brBit = (preset & 0x8) != 0;

        if (frontLeft != null)
            frontLeft.setDirection(flBit ? invert(FL_BASE) : FL_BASE);
        if (frontRight != null)
            frontRight.setDirection(frBit ? invert(FR_BASE) : FR_BASE);
        if (backLeft != null)
            backLeft.setDirection(blBit ? invert(BL_BASE) : BL_BASE);
        if (backRight != null)
            backRight.setDirection(brBit ? invert(BR_BASE) : BR_BASE);
    }

    public String getPresetString() {
        // TODO: What does this even mean? %%Zander Lewis <zander@zanderlewis.dev>%%
        String flDir = ((preset & 0x1) != 0 ? invert(FL_BASE) : FL_BASE).toString();
        String frDir = ((preset & 0x2) != 0 ? invert(FR_BASE) : FR_BASE).toString();
        String blDir = ((preset & 0x4) != 0 ? invert(BL_BASE) : BL_BASE).toString();
        String brDir = ((preset & 0x8) != 0 ? invert(BR_BASE) : BR_BASE).toString();
        String name = presetName(preset);
        return String.format("%s: FL=%s FR=%s BL=%s BR=%s", name, flDir, frDir, blDir, brDir);
    }

    private DcMotor.Direction invert(DcMotor.Direction d) {
        return (d == DcMotor.Direction.FORWARD) ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD;
    }

    private String presetName(int idx) {
        switch (idx & 0xF) {
            case 0:
                return "DEFAULT";
            case 1:
                return "FL_FLIP";
            case 2:
                return "FR_FLIP";
            case 3:
                return "FL_FR_FLIP";
            case 4:
                return "BL_FLIP";
            case 5:
                return "FL_BL_FLIP";
            case 6:
                return "FR_BL_FLIP";
            case 7:
                return "FL_FR_BL_FLIP";
            case 8:
                return "BR_FLIP";
            case 9:
                return "FL_BR_FLIP";
            case 10:
                return "FR_BR_FLIP";
            case 11:
                return "FL_FR_BR_FLIP";
            case 12:
                return "BL_BR_FLIP";
            case 13:
                return "FL_BL_BR_FLIP";
            case 14:
                return "FR_BL_BR_FLIP";
            case 15:
                return "ALL_FLIP";
            default:
                return String.format("0x%X", idx);
        }
    }
}
