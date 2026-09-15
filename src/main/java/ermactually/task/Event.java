package ermactually.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import ermactually.ErmActuallyException;

/**
 * Represents a task that occurs over a specified period.
 */
public class Event extends Task {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_INPUT_FORMAT = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    protected LocalDate fromDate;
    protected LocalTime fromTime;
    protected LocalDate toDate;
    protected LocalTime toTime;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description Task description.
     * @param from Event start in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @param to Event end in {@code yyyy-MM-dd} or {@code yyyy-MM-dd HHmm} format.
     * @throws ErmActuallyException If a required value is empty, a date-time is invalid, or the end
     *         is not after the start.
     */
    public Event(String description, String from, String to) throws ErmActuallyException {
        super(validateDescription(description), TaskType.EVENT);
        DateAndOptionalTime parsedFrom = parseDateAndOptionalTime(from, "start");
        DateAndOptionalTime parsedTo = parseDateAndOptionalTime(to, "end");
        this.fromDate = parsedFrom.date;
        this.fromTime = parsedFrom.time;
        this.toDate = parsedTo.date;
        this.toTime = parsedTo.time;
        if (toDate.equals(fromDate) && (fromTime == null) != (toTime == null)) {
            throw new ErmActuallyException(
                    "Please provide times for both event endpoints, or for neither endpoint.");
        }
        if (toDate.isBefore(fromDate)
                || toDate.equals(fromDate) && (fromTime == null || !toTime.isAfter(fromTime))) {
            throw new ErmActuallyException("how can the event end before it starts?");
        }
        assert !toDate.isBefore(fromDate) : "An event's end date must not precede its start date";
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
            throw new ErmActuallyException("Please add in a description of the event!");
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
    private static DateAndOptionalTime parseDateAndOptionalTime(String value, String valueName)
            throws ErmActuallyException {
        if (value == null || value.trim().isEmpty()) {
            throw new ErmActuallyException("The event " + valueName + " cannot be empty.");
        }
        String[] dateAndTime = value.trim().split("[T\\s]+", 2);
        try {
            LocalDate date = LocalDate.parse(dateAndTime[0]);
            LocalTime time = dateAndTime.length == 2 ? parseTime(dateAndTime[1]) : null;
            return new DateAndOptionalTime(date, time);
        } catch (DateTimeParseException e) {
            throw invalidEventFormat(valueName);
        }
    }

    /**
     * Parses a time entered as {@code HHmm} or restored from ISO storage as {@code HH:mm}.
     *
     * @param time Time text to parse.
     * @return The parsed time.
     * @throws DateTimeParseException If the time is invalid.
     */
    private static LocalTime parseTime(String time) {
        return time.contains(":") ? LocalTime.parse(time) : LocalTime.parse(time, TIME_INPUT_FORMAT);
    }

    /**
     * Creates a validation error identifying an invalid event endpoint.
     *
     * @param valueName Endpoint name, such as {@code start} or {@code end}.
     * @return An exception explaining both accepted event formats.
     */
    private static ErmActuallyException invalidEventFormat(String valueName) {
        return new ErmActuallyException("actually the format of " + valueName
                + " is in yyyy-MM-dd or yyyy-MM-dd HHmm!");
    }

    /**
     * Checks whether any part of this event occurs on the requested date.
     *
     * @param date Date to compare with the event's inclusive start and end dates.
     * @return {@code true} when the date is within the event's date range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }

    /** Returns the event start date used for chronological ordering. */
    @Override
    public Optional<LocalDate> getRelevantDate() {
        return Optional.of(fromDate);
    }

    /** Returns the event start time when one was supplied. */
    @Override
    public Optional<LocalTime> getRelevantTime() {
        return Optional.ofNullable(fromTime);
    }

    /**
     * Formats the event start for storage without inventing a missing time.
     *
     * @return The ISO start date, optionally followed by its time.
     */
    public String getFromStorageString() {
        return formatForStorage(fromDate, fromTime);
    }

    /**
     * Formats the event end for storage without inventing a missing time.
     *
     * @return The ISO end date, optionally followed by its time.
     */
    public String getToStorageString() {
        return formatForStorage(toDate, toTime);
    }

    /** Returns whether another event has the same description and endpoints. */
    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return fromDate.equals(otherEvent.fromDate)
                && Objects.equals(fromTime, otherEvent.fromTime)
                && toDate.equals(otherEvent.toDate)
                && Objects.equals(toTime, otherEvent.toTime);
    }

    /** Returns a hash based on the immutable event details. */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fromDate, fromTime, toDate, toTime);
    }

    /**
     * Creates the stable ISO representation used by the save file.
     *
     * @param date Required date component.
     * @param time Optional time component; {@code null} means no time was supplied.
     * @return The date alone or a date and time separated by {@code T}.
     */
    private static String formatForStorage(LocalDate date, LocalTime time) {
        return time == null ? date.toString() : date + "T" + time;
    }

    /**
     * Creates a friendly date display and includes a time only when one was supplied.
     *
     * @param date Required date component.
     * @param time Optional time component.
     * @return The formatted date with an optional formatted time.
     */
    private static String formatForDisplay(LocalDate date, LocalTime time) {
        String formattedValue = date.format(DATE_DISPLAY_FORMAT);
        return time == null ? formattedValue : formattedValue + " " + time.format(TIME_DISPLAY_FORMAT);
    }

    /**
     * Returns the event with its start and end in a user-friendly format.
     *
     * @return The task status, description, start, and end.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + formatForDisplay(fromDate, fromTime)
                + " to: " + formatForDisplay(toDate, toTime) + ")";
    }

    /** Holds the two components produced while parsing an event endpoint. */
    private static class DateAndOptionalTime {
        private final LocalDate date;
        private final LocalTime time;

        /**
         * Creates a parsed endpoint value.
         *
         * @param date Required endpoint date.
         * @param time Optional endpoint time.
         */
        DateAndOptionalTime(LocalDate date, LocalTime time) {
            this.date = date;
            this.time = time;
        }
    }
}
