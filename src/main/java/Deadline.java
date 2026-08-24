import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that has a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    protected LocalDate by;

    /**
     * Creates a deadline with a description and deadline value.
     *
     * @param description Task description.
     * @param by Deadline date in {@code yyyy-MM-dd} format.
     * @throws ErmActuallyException If either required value is empty or the date is invalid.
     */
    public Deadline(String description, String by) throws ErmActuallyException {
        super(validateDescription(description), TaskType.DEADLINE);
        this.by = validateDeadline(by);
    }

    /**
     * Checks that a deadline description contains text and removes surrounding whitespace.
     *
     * @param description Description supplied for the deadline task.
     * @return The description without leading or trailing whitespace.
     * @throws ErmActuallyException If the description is {@code null} or blank.
     */
    private static String validateDescription(String description) throws ErmActuallyException {
        if (description == null || description.trim().isEmpty()) {
            throw new ErmActuallyException("The description of a deadline cannot be empty.");
        }
        return description.trim();
    }

    /**
     * Converts an ISO-format date string into a {@link LocalDate} for type-safe storage.
     *
     * @param by Deadline date supplied in {@code yyyy-MM-dd} format.
     * @return The parsed deadline date.
     * @throws ErmActuallyException If the date is {@code null}, blank, or not a valid ISO date.
     */
    private static LocalDate validateDeadline(String by) throws ErmActuallyException {
        if (by == null || by.trim().isEmpty()) {
            throw new ErmActuallyException("The deadline cannot be empty.");
        }
        try {
            return LocalDate.parse(by.trim());
        } catch (DateTimeParseException e) {
            throw new ErmActuallyException("Please enter the deadline in yyyy-MM-dd format.");
        }
    }

    /**
     * Returns the deadline using the user-friendly {@code MMM dd yyyy} date format.
     *
     * @return The task status, description, and formatted deadline date.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
