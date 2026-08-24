/**
 * Represents a task without a date or time.
 */
public class Todo extends Task {
    /**
     * Creates a todo with a non-empty description.
     *
     * @param description Task description.
     * @throws ErmActuallyException If the description is empty.
     */
    public Todo(String description) throws ErmActuallyException {
        super(validateDescription(description), TaskType.TODO);
    }

    private static String validateDescription(String description) throws ErmActuallyException {
        if (description == null || description.trim().isEmpty()) {
            throw new ErmActuallyException("The description of a todo cannot be empty.");
        }
        return description.trim();
    }
}
