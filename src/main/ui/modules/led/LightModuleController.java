package main.ui.modules.led;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import main.core.Injectable;
import main.core.RoombaJSSCSingleton;
import main.core.led.listeners.PowerColorListener;
import main.core.led.listeners.PowerIntensityListener;
import main.ui.root.RootController;

/*
* Manages interaction between the user and the Roomba.
* LightModule allows users to set parameters for the Roomba
* LED command and send that command to the Roomba.
* */
public class LightModuleController implements Initializable, Injectable {

  private RootController rootController;

  /* *********************************************
  *
  * FXML members
  *
  ********************************************** */

  @FXML
  private VBox lightModule;

  @FXML
  private JFXCheckBox debrisCheckBox;

  @FXML
  private JFXCheckBox spotCheckBox;

  @FXML
  private JFXCheckBox dockCheckBox;

  @FXML
  private JFXCheckBox checkRobotCheckBox;

  @FXML
  private JFXTextField powerColorTextField;

  @FXML
  private JFXTextField powerIntensityTextField;

  @FXML
  private JFXButton toggle;

  private boolean hasStarted = false;

  @FXML
  void toggle(ActionEvent event) {

    // Don't allow play to change if error is obtained!

    if (!hasStarted) {
      int powerColor = Integer.parseInt(powerColorTextField.getText());
      int powerIntensity = Integer.parseInt(powerIntensityTextField.getText());
      boolean check1 = (powerColor >= 0 && powerColor <= 100);
      boolean check2 = (powerIntensity >= 0 && powerIntensity <= 100);

      if (check1 && check2) {
        boolean debris = debrisCheckBox.isSelected();
        boolean spot = spotCheckBox.isSelected();
        boolean dock = dockCheckBox.isSelected();
        boolean checkRobot = checkRobotCheckBox.isSelected();
        RoombaJSSCSingleton.getRoombaJSSC()
            .leds(debris, spot, dock, checkRobot, powerColor, powerIntensity);
      } else {
        if (!check1) {
          Alert alert = new Alert(AlertType.ERROR);
          DialogPane dialogPane = alert.getDialogPane();
          dialogPane.getScene().getStylesheets().add("main/ui/root/dialog.css");
          alert.setHeaderText("Power Color (%)");
          alert.setContentText("Invalid Input! Range [0, 100]");
          alert.show();
        } else {
          Alert alert = new Alert(AlertType.ERROR);
          DialogPane dialogPane = alert.getDialogPane();
          dialogPane.getScene().getStylesheets().add("main/ui/root/dialog.css");
          alert.setHeaderText("Power Intensity (%)");
          alert.setContentText("Invalid Input! Range [0, 100]");
          alert.show();
        }
      }

      JFXButton button = (JFXButton) event.getSource();
      ImageView imageView = new ImageView("main/res/stop.png");
      imageView.setFitWidth(25);
      imageView.setFitHeight(25);
      button.setGraphic(imageView);

    } else {
      RoombaJSSCSingleton.getRoombaJSSC().leds(false, false, false, false, 0, 0);
      JFXButton button = (JFXButton) event.getSource();
      ImageView imageView = new ImageView("main/res/play.png");
      imageView.setFitWidth(25);
      imageView.setFitHeight(25);
      button.setGraphic(imageView);
    }

    hasStarted = !hasStarted;

  }

  @Override
  public void inject(RootController rootController) {
    this.rootController = rootController;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    lightModule.setStyle("-fx-background-color: #47494c; -fx-background-radius: 10;");
    lightModule.setOnKeyPressed(event -> {
      switch (event.getCode()) {
        case SPACE:
          toggle.fire();
          break;
        default:
          break;
      }
    });

    final ContextMenu powerColorContextMenu = new ContextMenu();
    MenuItem powerColorMenuItemGreen = new MenuItem("Green");
    powerColorMenuItemGreen.setOnAction(e -> powerColorTextField.setText("0"));
    MenuItem powerColorMenuItemOrange = new MenuItem("Orange");
    powerColorMenuItemOrange.setOnAction(e -> powerColorTextField.setText("50"));
    MenuItem powerColorMenuItemYellow = new MenuItem("Yellow");
    powerColorMenuItemYellow.setOnAction(e -> powerColorTextField.setText("75"));
    MenuItem powerColorMenuItemRed = new MenuItem("Red");
    powerColorMenuItemRed.setOnAction(e -> powerColorTextField.setText("100"));
    powerColorContextMenu.getItems().add(powerColorMenuItemGreen);
    powerColorContextMenu.getItems().add(powerColorMenuItemOrange);
    powerColorContextMenu.getItems().add(powerColorMenuItemYellow);
    powerColorContextMenu.getItems().add(powerColorMenuItemRed);
    powerColorTextField.setContextMenu(powerColorContextMenu);

    final ContextMenu powerIntensityContextMenu = new ContextMenu();
    MenuItem powerIntensityMenuItemOff = new MenuItem("Off");
    powerIntensityMenuItemOff.setOnAction(e -> powerIntensityTextField.setText("0"));
    MenuItem powerIntensityMenuItemHalf = new MenuItem("Half");
    powerIntensityMenuItemHalf.setOnAction(e -> powerIntensityTextField.setText("50"));
    MenuItem powerIntensityMenuItemFull = new MenuItem("Full");
    powerIntensityMenuItemFull.setOnAction(e -> powerIntensityTextField.setText("100"));
    powerIntensityContextMenu.getItems().add(powerIntensityMenuItemOff);
    powerIntensityContextMenu.getItems().add(powerIntensityMenuItemHalf);
    powerIntensityContextMenu.getItems().add(powerIntensityMenuItemFull);
    powerIntensityTextField.setContextMenu(powerIntensityContextMenu);

    powerColorTextField.setText("0");
    powerIntensityTextField.setText("0");
    powerColorTextField.textProperty().addListener(new PowerColorListener(powerColorTextField));
    powerIntensityTextField.textProperty()
        .addListener(new PowerIntensityListener(powerIntensityTextField));

  }

}
