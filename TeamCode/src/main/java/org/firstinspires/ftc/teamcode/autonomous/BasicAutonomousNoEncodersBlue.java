package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Basic Autonomous - Blue (No Encoders)", group = "1 - Team Code")
public class BasicAutonomousNoEncodersBlue extends BaseAutonomousNoEncoders {
  @Override
  protected double getStrafePower() {
    return -DRIVE_POWER;
  }
}