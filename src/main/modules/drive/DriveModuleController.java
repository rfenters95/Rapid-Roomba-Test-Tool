package main.modules.drive;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import main.alerts.NotConnectedAlert;
import main.core.RoombaState;
import main.core.drive.modes.AbstractDriveMode;
import main.core.drive.modes.Drive;
import main.core.drive.modes.DriveDirect;
import main.modules.ModuleController;

/*
* Manages interaction between the user and the Roomba.
* DriveModule allows users to set parameters for the various
* Roomba Drive commands and send those commands to the Roomba.
* */
public class DriveModuleController extends ModuleController implements Initializable {

  /* *********************************************
  *
  * FXML members
  *
  ********************************************** */

  @FXML
  private VBox driveModule;

  @FXML
  private JFXComboBox<AbstractDriveMode> driveModeComboBox;

  @FXML
  private JFXTextField textField1;

  @FXML
  private JFXTextField textField2;

  /* *********************************************
  *
  * Instance members
  *
  ********************************************** */

  private AbstractDriveMode mode;

  /* *********************************************
  *
  * Actions
  *
  ********************************************** */

  @Override
  public void play(ActionEvent event) {
    // Do nothing, if Roomba is not connected.
    if (!RoombaState.isConnected()) {
      NotConnectedAlert connectionAlert = new NotConnectedAlert();
      connectionAlert.show();
      return;
    }

    // Invalid parameters detected. Alert user and exit method.
    if (!mode.hasValidParameters()) {
      mode.alert();
      return;
    }

    if (!isPlaying) {
      mode.move();
      AbstractDriveMode.disableFields();
      rootController.setImage(playButton, "main/res/stop.png");
    } else {
      mode.stop();
      AbstractDriveMode.enableFields();
      rootController.setImage(playButton, "main/res/play.png");
    }
    isPlaying = !isPlaying;
  }

  /* *********************************************
  *
  * Initialize DriveModuleController
  *
  ********************************************** */

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    /* *********************************************
    *
    * Add stop module action to Space key
    *
    ********************************************** */

    driveModule.setOnKeyPressed(new SpaceKeyAction());

    /* *********************************************
    *
    * Initialize AbstractDriveMode TextFields
    *
    ********************************************** */

    AbstractDriveMode.setTextFields(textField1, textField2);

    /* *********************************************
    *
    * Populate driveMode ComboBox
    *
    ********************************************** */

    AbstractDriveMode driveMode = new Drive();
    AbstractDriveMode driveDirectMode = new DriveDirect();
    driveModeComboBox.getItems().setAll(driveMode, driveDirectMode);

    /* *********************************************
    *
    * Swap TextField listener to match selected item
    *
    ********************************************** */

    driveModeComboBox.setOnAction(new ComboBoxOnActionEvent());

    /* *********************************************
    *
    * Initialize driveMode ComboBox to first item
    *
    ********************************************** */

    driveModeComboBox.getSelectionModel().selectFirst();
    driveModeComboBox.getSelectionModel().getSelectedItem().initialize();
    mode = driveModeComboBox.getSelectionModel().getSelectedItem();
  }

  /*
  * ComboBoxOnActionEvent changes the drive modules
  * parameters to reflect DriveMode selection.
  * */
  private class ComboBoxOnActionEvent implements EventHandler<ActionEvent> {

    @Override
    public void handle(ActionEvent event) {
      mode = driveModeComboBox.getSelectionModel().getSelectedItem();
      mode.swapListener();
    }
  }

}
