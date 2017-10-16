package main.core;

import com.maschel.roomba.RoombaJSSC;
import com.maschel.roomba.RoombaJSSCSerial;
import com.sun.javafx.binding.StringFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/*
* Ensures only one instance of RoombaJSSC is created.
* */
public class RoombaJSSCSingleton {

  private static final RoombaJSSC roombaJSSC = new RoombaJSSCSerial();
  private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
      StringFormatter.format("%12s", "hh:mm:ss:SSS").getValue());
  private static final SimpleDateFormat fileSafeDateFormat = new SimpleDateFormat(
      StringFormatter.format("%s", "hh.mm.ss").getValue());

  private static BooleanProperty isConnected = new SimpleBooleanProperty(false);

  static BooleanProperty moduleLocked;

  public static boolean isModuleLocked() {
    return moduleLocked.get();
  }

  public static void setModuleLocked(boolean moduleLocked) {
    RoombaJSSCSingleton.moduleLocked.set(moduleLocked);
  }

  public static BooleanProperty moduleLockedProperty() {
    return moduleLocked;
  }

  private RoombaJSSCSingleton() {
  }

  /*
  * Connects & Starts Roomba
  * @param port Port that Roomba is using
  * @param startMode Mode that user wants to start in
  * */
  public static void connect(String port, RoombaStartMode startMode) {
    roombaJSSC.connect(port);
    roombaJSSC.start();
    switch (startMode) {
      case FULL:
        roombaJSSC.fullMode();
        break;
      case SAFE:
        roombaJSSC.safeMode();
        break;
      default:
        break;
    }
    isConnected.set(true);
  }

  public static void powerOff() {
    roombaJSSC.stop();
    roombaJSSC.disconnect();
    isConnected.set(false);
  }

  public static boolean isConnected() {
    return isConnected.get();
  }

  public static BooleanProperty isConnectedProperty() {
    return isConnected;
  }

  public static RoombaJSSC getRoombaJSSC() {
    return roombaJSSC;
  }

  public static String logDate() {
    return simpleDateFormat.format(new Date());
  }

  public static String logDateForFileName() {
    return fileSafeDateFormat.format(new Date());
  }

}
