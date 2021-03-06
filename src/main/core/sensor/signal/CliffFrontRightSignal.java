package main.core.sensor.signal;

import main.core.RoombaState;

/*
* Requests wall signal sensor information from the Roomba.
* */
public class CliffFrontRightSignal extends AbstractSignalSensor {

  @Override
  public int read() {
    RoombaState.getRoomba().updateSensors();
    return RoombaState.getRoomba().cliffSignalFrontRight();
  }

}
