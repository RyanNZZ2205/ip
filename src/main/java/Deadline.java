import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Represents a task that has a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    protected LocalDate byDate;
    protected LocalTime byTime;

    /**
     * Creates a deadline with a description and deadline value.
     *
     * @param description Task description.
     * @param by Deadline in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @throws ErmActuallyException If either required value is empty or the date is invalid.
     */
    public Deadline(String description, String by) throws ErmActuallyException {
        super(validateDescription(description), TaskType.DEADLINE);
        String[] dateAndTime = splitDateAndOptionalTime(by);
        this.byDate = parseDate(dateAndTime[0]);
        this.byTime = dateAndTime.length == 2 ? parseTime(dateAndTime[1]) : null;
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
     * Splits a deadline into its required date and optional time components.
     *
     * @param by Deadline text entered by the user or read from storage.
     * @return One element for a date-only deadline, or two elements when a time is present.
     * @throws ErmActuallyException If the deadline is {@code null} or blank.
     */
    private static String[] splitDateAndOptionalTime(String by) throws ErmActuallyException {
        if (by == null || by.trim().isEmpty()) {
            throw new ErmActuallyException("The deadline cannot be empty.");
        }
        return by.trim().split("[T\\s]+", 2);
    }

    /**
     * Parses the date component of a deadline.
     *
     * @param date Deadline date in {@code yyyy-MM-dd} format.
     * @return The parsed date.
     * @throws ErmActuallyException If the date is invalid.
     */
    private static LocalDate parseDate(String date) throws ErmActuallyException {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw invalidDeadlineFormat();
        }
    }

    /**
     * Parses the optional time component from user input or ISO-formatted saved data.
     *
     * @param time Deadline time in {@code HHmm} or stored {@code HH:mm} format.
     * @return The parsed time.
     * @throws ErmActuallyException If the time is invalid.
     */
    private static LocalTime parseTime(String time) throws ErmActuallyException {
        try {
            return time.contains(":") ? LocalTime.parse(time) : LocalTime.parse(time, TIME_INPUT_FORMAT);
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
    public boolean occursOn(LocalDate date) {
        return byDate.equals(date);
    }

    /**
     * Returns a stable ISO representation while preserving whether a time was supplied.
     *
     * @return A date, optionally followed by an ISO time separated by {@code T}.
     */
    public String toStorageString() {
        return byTime == null ? byDate.toString() : byDate + "T" + byTime;
    }

    /**
     * Returns the deadline using the user-friendly {@code MMM dd yyyy} date format.
     *
     * @return The task status, description, and formatted deadline date.
     */
    @Override
    public String toString() {
        String formattedDeadline = byDate.format(DISPLAY_FORMAT);
        if (byTime != null) {
            formattedDeadline += " " + byTime.format(TIME_DISPLAY_FORMAT);
        }
        return super.toString() + " (by: " + formattedDeadline + ")";
    }
}
