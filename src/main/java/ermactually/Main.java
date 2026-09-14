package ermactually;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Provides the FXML-based graphical user interface for ErmActually.
 */
public class Main extends Application {

    private final ErmActually ermActually =
            new ErmActually("data/ErmActually.txt");

    /**
     * Creates and displays the primary application window.
     *
     * @param stage Primary stage supplied by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setErmActually(ermActually);
            stage.show();
        } catch (IOException | RuntimeException e) {
            showStartupError();
        }
    }

    /** Shows a concise error when required graphical interface resources cannot be loaded. */
    private static void showStartupError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erm Actually could not start");
        alert.setHeaderText("The graphical interface could not be loaded.");
        alert.setContentText("Please reinstall the application or restore its missing resource files.");
        alert.showAndWait();
    }
}
