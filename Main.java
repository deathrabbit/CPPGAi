package CPPGAi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Main
 *
 * Main class for Genetic Algorithm (integers) program
 *
 * @author Scott Matsumura
 * @version 1.0
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CPPGAi-GUI.fxml"));
            Parent root = loader.load();
            Controller controller = loader.getController();
            controller.setStage(primaryStage);
            primaryStage.setTitle(DefaultConstants.TITLE);
            primaryStage.setScene(new Scene(root, LayoutConstants.WINDOW_SIZE[0], LayoutConstants.WINDOW_SIZE[1]));
            primaryStage.setOnCloseRequest(event -> {
                if (controller.isOneRunning()) {
                    Alert alertStop = new Alert(Alert.AlertType.CONFIRMATION);
                    alertStop.setTitle(null);
                    alertStop.setHeaderText(null);
                    alertStop.setContentText(LayoutConstants.TASK_ALERT);

                    ButtonType buttonReturn = new ButtonType(LayoutConstants.TASK_ALERT_BUTTON_RETURN);
                    ButtonType buttonClose = new ButtonType(LayoutConstants.TASK_ALERT_BUTTON_CLOSE);

                    alertStop.getButtonTypes().setAll(buttonReturn, buttonClose);
                    Optional<ButtonType> result = alertStop.showAndWait();
                    if (result.isPresent()) {
                        if (result.get() == buttonClose) {
                            primaryStage.close();
                            controller.stopRunning();
                        } else {
                            event.consume();
                        }
                    } else {
                        event.consume();
                    }
                } else if (!controller.isSaved()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(null);
                    alert.setHeaderText(null);
                    alert.setContentText(LayoutConstants.SAVE_ALERT);

                    ButtonType buttonSave = new ButtonType(LayoutConstants.SAVE_ALERT_BUTTON_SAVEAS);
                    ButtonType buttonClose = new ButtonType(LayoutConstants.SAVE_ALERT_BUTTON_CLOSE, ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonSave, buttonClose);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent()) {
                        if (result.get() == buttonSave) {
                            controller.onSaveAsFile();
                        }
                    }
                    primaryStage.close();
                    controller.stopRunning();
                } else {
                    primaryStage.close();
                    controller.stopRunning();
                }
            });
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
