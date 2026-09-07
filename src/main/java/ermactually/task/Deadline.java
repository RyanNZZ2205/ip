package ermactually.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import ermactually.ErmActuallyException;

/**
 * Represents a task that has a deadline.
 */
public class Deadline extends Task {
    private final TaskDateTime deadlineDateTime;

    /**
     * Creates a deadline with a description and deadline value.
     *
     * @param description Task description.
     * @param by Deadline in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @throws ErmActuallyException If either required value is empty or the date is invalid.
     */
    public Deadline(String description, String by) throws ErmActuallyException {
        super(validateDescription(description), TaskType.DEADLINE);
        this.deadlineDateTime = parseDateAndOptionalTime(by);
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
     * Parses a deadline into its required date and optional time components.
     *
     * @param by Deadline text entered by the user or read from storage.
     * @return Parsed deadline date and optional time.
     * @throws ErmActuallyException If the deadline is empty or has an unsupported format.
     */
    private static TaskDateTime parseDateAndOptionalTime(String by) throws ErmActuallyException {
        if (by == null || by.trim().isEmpty()) {
            throw new ErmActuallyException("The deadline cannot be empty.");
        }
        try {
            return TaskDateTime.parse(by);
        } catch (DateTimeParseException e) {
            throw invalidDeadlineFormat();
        }
    }

    /**
     * Creates the shared validation error for an invalid deadline date or time.
     *
     * @return An exception explaining both accepted deadline formats.
     */
    private static ErmActuallyException invalidDeadlineFormat() {
        return new ErmActuallyException(
                "Please enter the deadline in yyyy-MM-dd or yyyy-MM-dd HHmm format.");
    }

    /**
     * Checks whether this deadline falls on the requested date.
     *
     * @param date Date to compare with this deadline.
     * @return {@code true} when this deadline is on the requested date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return deadlineDateTime.isOn(date);
    }

    /**
     * Returns a stable ISO representation while preserving whether a time was supplied.
     *
     * @return A date, optionally followed by an ISO time separated by {@code T}.
     */
    public String toStorageString() {
        return deadlineDateTime.toStorageString();
    }

    /**
     * Returns the deadline using the user-friendly {@code MMM dd yyyy} date format.
     *
     * @return The task status, description, and formatted deadline date.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + deadlineDateTime.toDisplayString() + ")";
    }
}
