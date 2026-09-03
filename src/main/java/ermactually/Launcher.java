package ermactually;

import javafx.application.Application;

/**
 * Launches the JavaFX application while avoiding classpath issues.
 */
public class Launcher {
    /**
     * Starts the ErmActually JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
