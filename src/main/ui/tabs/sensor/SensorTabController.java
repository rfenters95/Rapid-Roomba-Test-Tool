package main.ui.tabs.sensor;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import main.core.Injectable;
import main.core.RoombaJSSCSingleton;
import main.core.sensor.bool.AbstractBooleanSensor;
import main.core.sensor.bool.BumpCenter;
import main.core.sensor.bool.BumpLeft;
import main.core.sensor.bool.BumpRight;
import main.core.sensor.signal.AbstractSignalSensor;
import main.core.sensor.signal.Wall;
import main.ui.root.RootController;

public class SensorTabController implements Initializable, Injectable {

  private RootController rootController;

  @FXML
  private AnchorPane sensorTab;

  @FXML
  private JFXComboBox<AbstractBooleanSensor> booleanSensorComboBox;

  @FXML
  private JFXComboBox<AbstractSignalSensor> signalSensorComboBox;

  private boolean booleanToggleEnabled;
  private boolean signalToggleEnabled;

  /*
  * TODO Fix application thread flooding problem
  * See https://stackoverflow.com/questions/31408363/javafx-changelistener-not-always-working/31414801#31414801
  * for more info.
  *
  * In the mean time increase sleep time on sensor readers.
  * As of 08-29-2017, sleep time is equal to 1 second or 1000ms. This should be
  * fine for stationary sensor reading. However, before if-then tests are
  * implemented in expansion sleep times of .1 <= x < 1 should be possible.
  *
  * Look into swapping out the TextArea for a ListView, as they are
  * a virtual control that only renders text shown
  * instead of all containing text.
  *
  * See https://stackoverflow.com/questions/33078241/javafx-application-freeze-when-i-append-log4j-to-textarea
  * and http://www.rshingleton.com/javafx-log4j-textarea-log-appender/
  * for more info.
  * */

  private Service<Void> booleanToggleService = new Service<Void>() {

    @Override
    protected Task<Void> createTask() {

      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {

          AbstractBooleanSensor sensor = booleanSensorComboBox.getSelectionModel()
              .getSelectedItem();
          while (!isCancelled()) {
            boolean value = sensor.read();
            String message = RoombaJSSCSingleton.logDate() + "\t<---> " + value + "\n";
            Platform.runLater(() -> {
              rootController.console.appendText(message);
            });
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              if (isCancelled()) {
                break;
              }
            }
          }

          return null;

        }

      };

    }

  };

  private Service<Void> signalToggleService = new Service<Void>() {

    @Override
    protected Task<Void> createTask() {

      return new Task<Void>() {
        @Override
        protected Void call() throws Exception {

          AbstractSignalSensor sensor = signalSensorComboBox.getSelectionModel().getSelectedItem();
          while (!isCancelled()) {
            int value = sensor.read();
            String message = RoombaJSSCSingleton.logDate() + "\t<---> " + value + "\n";
            Platform.runLater(() -> {
              rootController.console.appendText(message);
            });
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              if (isCancelled()) {
                break;
              }
            }
          }

          return null;

        }

      };

    }

  };

  @FXML
  void readBooleanSensor(ActionEvent event) {
    // Don't run if other thread is running
    if (!signalToggleEnabled) {
      if (booleanToggleEnabled) {
        JFXButton button = (JFXButton) event.getSource();
        button.setText("Read");
        booleanToggleService.cancel();
      } else {
        JFXButton button = (JFXButton) event.getSource();
        button.setText("Stop");
        booleanToggleService.reset();
        booleanToggleService.start();
      }
      booleanToggleEnabled = !booleanToggleEnabled;
    }
  }

  @FXML
  void readSignalSensor(ActionEvent event) {
    // Don't run if other thread is running
    if (!booleanToggleEnabled) {
      if (signalToggleEnabled) {
        JFXButton button = (JFXButton) event.getSource();
        button.setText("Read");
        signalToggleService.cancel();
      } else {
        JFXButton button = (JFXButton) event.getSource();
        button.setText("Stop");
        signalToggleService.reset();
        signalToggleService.start();
      }
      signalToggleEnabled = !signalToggleEnabled;
    }
  }

  @Override
  public void inject(RootController rootController) {
    this.rootController = rootController;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    /* Task messages may need later

    booleanToggleService.messageProperty().addListener((observable, oldValue, newValue) -> {
        rootController.console.appendText(newValue);
    });

    signalToggleService.messageProperty().addListener(((observable, oldValue, newValue) -> {
        rootController.console.appendText(newValue);
    }));

    */

    /* *********************************************
    *
    * Add boolean sensors
    *
    ********************************************** */

    booleanSensorComboBox.setVisibleRowCount(3);
    booleanSensorComboBox.getItems().add(new BumpCenter());
    booleanSensorComboBox.getItems().add(new BumpLeft());
    booleanSensorComboBox.getItems().add(new BumpRight());

    /* *********************************************
    *
    * Add signal sensors
    *
    ********************************************** */

    signalSensorComboBox.setVisibleRowCount(3);
    signalSensorComboBox.getItems().add(new Wall());

  }

}
