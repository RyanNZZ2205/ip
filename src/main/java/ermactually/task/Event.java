package ermactually.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import ermactually.ErmActuallyException;

/**
 * Represents a task that occurs over a specified period.
 */
public class Event extends Task {
    private final TaskDateTime startDateTime;
    private final TaskDateTime endDateTime;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description Task description.
     * @param from Event start in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @param to Event end in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @throws ErmActuallyException If a required value is empty, a date-time is invalid, or the end
     *         is before the start.
     */
    public Event(String description, String from, String to) throws ErmActuallyException {
        super(validateDescription(description), TaskType.EVENT);
        this.startDateTime = parseDateAndOptionalTime(from, "start");
        this.endDateTime = parseDateAndOptionalTime(to, "end");
        if (endDateTime.isBefore(startDateTime)) {
            throw new ErmActuallyException("The event end cannot be before its start.");
        }
        assert !endDateTime.isBefore(startDateTime) : "An event's end must not precede its start";
    }

    /**
     * Checks that an event description contains text and removes surrounding whitespace.
     *
     * @param description Description supplied for the event.
     * @return The trimmed event description.
     * @throws ErmActuallyException If the description is {@code null} or blank.
     */
    private static String validateDescription(String description) throws ErmActuallyException {
        if (description == null || description.trim().isEmpty()) {
            throw new ErmActuallyException("The description of an event cannot be empty.");
        }
        return description.trim();
    }

    /**
     * Parses a required date and an optional time from user input or saved data.
     *
     * @param value Date-time text to parse.
     * @param valueName Name used to identify the invalid field in an error message.
     * @return The parsed date and optional time.
     * @throws ErmActuallyException If the value is empty or has an unsupported format.
     */
    private static TaskDateTime parseDateAndOptionalTime(String value, String valueName)
            throws ErmActuallyException {
        if (value == null || value.trim().isEmpty()) {
            throw new ErmActuallyException("The event " + valueName + " cannot be empty.");
        }
        try {
            return TaskDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw invalidEventFormat(valueName);
        }
    }

    /**
     * Creates a validation error identifying an invalid event endpoint.
     *
     * @param valueName Endpoint name, such as {@code start} or {@code end}.
     * @return An exception explaining both accepted event formats.
     */
    private static ErmActuallyException invalidEventFormat(String valueName) {
        return new ErmActuallyException("Please enter the event " + valueName
                + " in yyyy-MM-dd or yyyy-MM-dd HHmm format.");
    }

    /**
     * Checks whether any part of this event occurs on the requested date.
     *
     * @param date Date to compare with the event's inclusive start and end dates.
     * @return {@code true} when the date is within the event's date range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDateTime.getDate()) && !date.isAfter(endDateTime.getDate());
    }

    /**
     * Formats the event start for storage without inventing a missing time.
     *
     * @return The ISO start date, optionally followed by its time.
     */
    public String getFromStorageString() {
        return startDateTime.toStorageString();
    }

    /**
     * Formats the event end for storage without inventing a missing time.
     *
     * @return The ISO end date, optionally followed by its time.
     */
    public String getToStorageString() {
        return endDateTime.toStorageString();
    }

    /**
     * Returns the event with its start and end in a user-friendly format.
     *
     * @return The task status, description, start, and end.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + startDateTime.toDisplayString()
                + " to: " + endDateTime.toDisplayString() + ")";
    }
}
