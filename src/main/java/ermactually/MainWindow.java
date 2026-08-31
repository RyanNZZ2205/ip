package ermactually;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private ErmActually ermActually;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the ErmActually instance and displays any task-loading error. */
    public void setErmActually(ErmActually ermActually) {
        this.ermActually = ermActually;
        String startupError = ermActually.getStartupError();
        if (startupError != null) {
            dialogContainer.getChildren().add(
                    DialogBox.getErmActuallyDialog(
                            startupError, dukeImage, Parser.CommandType.UNKNOWN));
        }
    }

    /**
     * Creates dialog boxes for the user's input and ErmActually's response.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = ermActually.getResponse(input);
        Parser.CommandType commandType = ermActually.getCommandType();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getErmActuallyDialog(response, dukeImage, commandType)
        );
        userInput.clear();
    }
}
