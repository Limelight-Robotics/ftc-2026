package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Encapsulates drive motor direction presets using a 4-bit bitmask.
 * Each bit controls whether a motor direction is inverted from baseline.
 * Bit 0: FL, Bit 1: FR, Bit 2: BL, Bit 3: BR
 */
public class DriveDirectionPresets {
    // Base directions for the logical wheel positions
    private static final DcMotor.Direction FL_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction FR_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction BL_BASE = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction BR_BASE = DcMotor.Direction.FORWARD;

    // 24 permutations of assigning base directions to physical motors (4!)
    // Each row maps: [dirForFL, dirForFR, dirForBL, dirForBR]
    private static final DcMotor.Direction[][] PRESETS = buildPermutations();

    private int preset = 0;

    public int getPreset() {
        return preset & BITMASK;
    }

    public void setPreset(int idx) {
        if (idx < 0) idx = 0;
        this.preset = idx % PRESETS.length;
    }

    public void cyclePreset() {
        this.preset = (this.preset + 1) % PRESETS.length;
    }

    /**
     * Apply preset to motors. Null motors are safely ignored.
     */
    public void applyTo(DcMotor fl, DcMotor fr, DcMotor bl, DcMotor br) {
        DcMotor.Direction[] cfg = PRESETS[preset];
        if (fl != null) fl.setDirection(cfg[0]);
        if (fr != null) fr.setDirection(cfg[1]);
        if (bl != null) bl.setDirection(cfg[2]);
        if (br != null) br.setDirection(cfg[3]);
    }

    /** Returns human-readable string of current preset directions. */
    public String getPresetString() {
        DcMotor.Direction[] cfg = PRESETS[preset];
        return String.format("Preset %d: FL=%s FR=%s BL=%s BR=%s", preset,
                cfg[0], cfg[1], cfg[2], cfg[3]);
    }

    private DcMotor.Direction invert(DcMotor.Direction d) {
        return d == DcMotor.Direction.FORWARD 
            ? DcMotor.Direction.REVERSE 
            : DcMotor.Direction.FORWARD;
    }

    private static DcMotor.Direction[][] buildPermutations() {
        DcMotor.Direction[] base = new DcMotor.Direction[] { FL_BASE, FR_BASE, BL_BASE, BR_BASE };
        int[][] perms = new int[][]{
                {0,1,2,3}, {0,1,3,2}, {0,2,1,3}, {0,2,3,1}, {0,3,1,2}, {0,3,2,1},
                {1,0,2,3}, {1,0,3,2}, {1,2,0,3}, {1,2,3,0}, {1,3,0,2}, {1,3,2,0},
                {2,0,1,3}, {2,0,3,1}, {2,1,0,3}, {2,1,3,0}, {2,3,0,1}, {2,3,1,0},
                {3,0,1,2}, {3,0,2,1}, {3,1,0,2}, {3,1,2,0}, {3,2,0,1}, {3,2,1,0}
        };
        DcMotor.Direction[][] presets = new DcMotor.Direction[perms.length][4];
        for (int i = 0; i < perms.length; i++) {
            presets[i] = new DcMotor.Direction[]{
                    base[perms[i][0]],
                    base[perms[i][1]],
                    base[perms[i][2]],
                    base[perms[i][3]]
            };
        }
        return presets;
    }
}
