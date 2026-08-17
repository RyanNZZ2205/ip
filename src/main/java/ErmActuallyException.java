/**
 * Represents an error caused by invalid input to the Erm Actually chatbot.
 */
public class ErmActuallyException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of the invalid input
     */
    public ErmActuallyException(String message) {
        super(message);
    }
}
