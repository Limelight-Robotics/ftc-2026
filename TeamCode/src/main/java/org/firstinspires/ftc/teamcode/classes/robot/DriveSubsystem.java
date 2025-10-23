package org.firstinspires.ftc.teamcode.classes.robot;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.classes.Utilities;

/**
 * DriveSubsystem encapsulates mecanum motor hardware and basic drive
 * operations.
 */
public class DriveSubsystem {
    private DcMotor frontLeftDrive;
    private DcMotor backLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backRightDrive;

    // Hardware names
    private static final String MOTOR_FRONT_LEFT = "fL";
    private static final String MOTOR_BACK_LEFT = "bL";
    private static final String MOTOR_FRONT_RIGHT = "fR";
    private static final String MOTOR_BACK_RIGHT = "bR";

    // Default motor directions
    private static final DcMotor.Direction MOTOR_DIR_FRONT_LEFT = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction MOTOR_DIR_FRONT_RIGHT = DcMotor.Direction.FORWARD;
    private static final DcMotor.Direction MOTOR_DIR_BACK_LEFT = DcMotor.Direction.REVERSE;
    private static final DcMotor.Direction MOTOR_DIR_BACK_RIGHT = DcMotor.Direction.FORWARD;

    private double lastFrontLeftPower = 0.0;
    private double lastFrontRightPower = 0.0;
    private double lastBackLeftPower = 0.0;
    private double lastBackRightPower = 0.0;

    // Direction preset bitmask (0..15). Bits: 0=FL,1=FR,2=BL,3=BR. Start with the
    // tested-correct FL_FR_FLIP (0x3).
    private int directionPreset = 0;

    public void init(HardwareMap hardwareMap) {
        if (hardwareMap == null)
            throw new IllegalArgumentException("hardwareMap cannot be null");
        frontLeftDrive = hardwareMap.get(DcMotor.class, MOTOR_FRONT_LEFT);
        backLeftDrive = hardwareMap.get(DcMotor.class, MOTOR_BACK_LEFT);
        frontRightDrive = hardwareMap.get(DcMotor.class, MOTOR_FRONT_RIGHT);
        backRightDrive = hardwareMap.get(DcMotor.class, MOTOR_BACK_RIGHT);
        if (frontLeftDrive == null || backLeftDrive == null || frontRightDrive == null || backRightDrive == null) {
            throw new IllegalStateException("Missing drive motor(s) in hardware map");
        }

        // Apply the configured direction preset (per-motor)
        applyDirectionPreset();
    }

    /** Cycle through 16 per-motor direction combinations. */
    public void cycleDirectionPreset() {
        directionPreset = (directionPreset + 1) % 16;
        applyDirectionPreset();
    }

    public int getDirectionPreset() {
        return directionPreset;
    }

    private void applyDirectionPreset() {
        boolean flBit = (directionPreset & 0x1) != 0;
        boolean frBit = (directionPreset & 0x2) != 0;
        boolean blBit = (directionPreset & 0x4) != 0;
        boolean brBit = (directionPreset & 0x8) != 0;

        DcMotor.Direction flBase = MOTOR_DIR_FRONT_LEFT;
        DcMotor.Direction frBase = MOTOR_DIR_FRONT_RIGHT;
        DcMotor.Direction blBase = MOTOR_DIR_BACK_LEFT;
        DcMotor.Direction brBase = MOTOR_DIR_BACK_RIGHT;

        frontLeftDrive.setDirection(flBit ? invertDirection(flBase) : flBase);
        frontRightDrive.setDirection(frBit ? invertDirection(frBase) : frBase);
        backLeftDrive.setDirection(blBit ? invertDirection(blBase) : blBase);
        backRightDrive.setDirection(brBit ? invertDirection(brBase) : brBase);
    }

    private DcMotor.Direction invertDirection(DcMotor.Direction d) {
        return (d == DcMotor.Direction.FORWARD) ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD;
    }

    /**
     * Human-readable preset description and per-motor directions.
     */
    public String getDirectionString() {
        boolean flBit = (directionPreset & 0x1) != 0;
        boolean frBit = (directionPreset & 0x2) != 0;
        boolean blBit = (directionPreset & 0x4) != 0;
        boolean brBit = (directionPreset & 0x8) != 0;

        DcMotor.Direction fl = flBit ? invertDirection(MOTOR_DIR_FRONT_LEFT) : MOTOR_DIR_FRONT_LEFT;
        DcMotor.Direction fr = frBit ? invertDirection(MOTOR_DIR_FRONT_RIGHT) : MOTOR_DIR_FRONT_RIGHT;
        DcMotor.Direction bl = blBit ? invertDirection(MOTOR_DIR_BACK_LEFT) : MOTOR_DIR_BACK_LEFT;
        DcMotor.Direction br = brBit ? invertDirection(MOTOR_DIR_BACK_RIGHT) : MOTOR_DIR_BACK_RIGHT;

        String name = presetName(directionPreset);
        return String.format("%s: FL=%s FR=%s BL=%s BR=%s", name, fl.toString(), fr.toString(), bl.toString(),
                br.toString());
    }

    private String presetName(int idx) {
        switch (idx) {
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

    public void drive(double axial, double lateral, double yaw) {
        double fl = axial + lateral + yaw;
        double fr = axial - lateral - yaw;
        double bl = axial - lateral + yaw;
        double br = axial + lateral - yaw;
        double[] normalized = Utilities.normalizeMecanum(fl, fr, bl, br);
        setMotorPowers(normalized);
    }

    public void stop() {
        setMotorPowers(new double[] { 0.0, 0.0, 0.0, 0.0 });
    }

    private void setMotorPowers(double[] powers) {
        if (powers == null || powers.length < 4)
            return;
        double fl = powers[0], fr = powers[1], bl = powers[2], br = powers[3];
        if (frontLeftDrive != null)
            frontLeftDrive.setPower(fl);
        if (frontRightDrive != null)
            frontRightDrive.setPower(fr);
        if (backLeftDrive != null)
            backLeftDrive.setPower(bl);
        if (backRightDrive != null)
            backRightDrive.setPower(br);
        updateTelemetryPowers();
    }

    public void updateTelemetryPowers() {
        lastFrontLeftPower = frontLeftDrive != null ? frontLeftDrive.getPower() : 0.0;
        lastFrontRightPower = frontRightDrive != null ? frontRightDrive.getPower() : 0.0;
        lastBackLeftPower = backLeftDrive != null ? backLeftDrive.getPower() : 0.0;
        lastBackRightPower = backRightDrive != null ? backRightDrive.getPower() : 0.0;
    }

    public double getFrontLeftPower() {
        return lastFrontLeftPower;
    }

    public double getFrontRightPower() {
        return lastFrontRightPower;
    }

    public double getBackLeftPower() {
        return lastBackLeftPower;
    }

    public double getBackRightPower() {
        return lastBackRightPower;
    }
}
