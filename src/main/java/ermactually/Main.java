package ermactually;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for ErmActually using FXML.
 */
public class Main extends Application {

    private ErmActually ermActually =
            new ErmActually("data/ErmActually.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setErmActually(ermActually);  // inject the ErmActually instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}