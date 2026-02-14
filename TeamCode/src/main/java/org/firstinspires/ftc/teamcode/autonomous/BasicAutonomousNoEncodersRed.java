package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Basic Autonomous - Red (No Encoders)", group = "1 - Team Code")
public class BasicAutonomousNoEncodersRed extends BaseAutonomousNoEncoders {
  @Override
  protected double getStrafePower() {
    return DRIVE_POWER;
  }
}