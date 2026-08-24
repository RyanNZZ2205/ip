/**
 * Represents a task that has a deadline.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline with a description and deadline value.
     *
     * @param description Task description.
     * @param by Deadline value.
     * @throws ErmActuallyException If either required value is empty.
     */
    public Deadline(String description, String by) throws ErmActuallyException {
        super(validateDescription(description), TaskType.DEADLINE);
        this.by = validateDeadline(by);
    }

    private static String validateDescription(String description) throws ErmActuallyException {
        if (description == null || description.trim().isEmpty()) {
            throw new ErmActuallyException("The description of a deadline cannot be empty.");
        }
        return description.trim();
    }

    private static String validateDeadline(String by) throws ErmActuallyException {
        if (by == null || by.trim().isEmpty()) {
            throw new ErmActuallyException("The deadline cannot be empty.");
        }
        return by.trim();
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
