package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Encapsulates drive motor direction presets (per-motor flips) so
 * DriveSubsystem can toggle or apply them cleanly.
 *
 * Preset Bitmask Layout:
 * - The preset is a 4-bit mask where each bit controls whether a motor
 * direction is inverted from its baseline.
 * - Bit 0 (0x1): Front Left motor flip
 * - Bit 1 (0x2): Front Right motor flip
 * - Bit 2 (0x4): Back Left motor flip
 * - Bit 3 (0x8): Back Right motor flip
 * - When a bit is 1, that motor's baseline direction is inverted.
 * - For example, preset=3 (binary 0011) flips both Front Left and Front Right.
 */
public class DriveDirectionPresets {
    // 4-bit preset bitmask (bits: 0=FL, 1=FR, 2=BL, 3=BR)
    // Each bit indicates whether to invert that motor from baseline.
    private int preset = 3 & 0xF;

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
     *
     * For each motor, extracts the corresponding bit from the preset bitmask:
     * - If the bit is 1, the motor's baseline direction is inverted.
     * - If the bit is 0, the baseline direction is used as-is.
     */
    public void applyTo(DcMotor frontLeft, DcMotor frontRight, DcMotor backLeft, DcMotor backRight) {
        // Extract each motor's flip bit from the 4-bit preset
        boolean flBit = (preset & 0x1) != 0; // Bit 0: FL flip
        boolean frBit = (preset & 0x2) != 0; // Bit 1: FR flip
        boolean blBit = (preset & 0x4) != 0; // Bit 2: BL flip
        boolean brBit = (preset & 0x8) != 0; // Bit 3: BR flip

        if (frontLeft != null)
            frontLeft.setDirection(flBit ? invert(FL_BASE) : FL_BASE);
        if (frontRight != null)
            frontRight.setDirection(frBit ? invert(FR_BASE) : FR_BASE);
        if (backLeft != null)
            backLeft.setDirection(blBit ? invert(BL_BASE) : BL_BASE);
        if (backRight != null)
            backRight.setDirection(brBit ? invert(BR_BASE) : BR_BASE);
    }

    /**
     * Build a human-readable string representation of the current preset,
     * showing the preset name and the resulting direction for each motor.
     */
    public String getPresetString() {
        // Extract flip bits and determine actual direction for each motor
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
