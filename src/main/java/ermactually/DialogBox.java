package ermactually;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Represents a dialog box containing a message and an image of the speaker.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box for a message sent by the user.
     *
     * @param text Message displayed in the dialog box.
     * @param img Image representing the user.
     * @return Dialog box containing the user's message.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Applies the response color associated with the processed command.
     *
     * @param commandType Type of command that produced the response.
     */
    private void changeDialogStyle(Parser.CommandType commandType) {
        switch (commandType) {
            case TODO:
            case DEADLINE:
            case EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
            case UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            default:
                break;
        }
    }

    /**
     * Returns a dialog box for a response sent by ErmActually.
     *
     * @param text Response displayed in the dialog box.
     * @param img Image representing ErmActually.
     * @return Dialog box containing ErmActually's response.
     */
    public static DialogBox getErmActuallyDialog(
            String text, Image img, Parser.CommandType commandType) {
        var db = new DialogBox(text, img);
        db.flip();
        db.changeDialogStyle(commandType);
        return db;
    }

}
