package ermactually.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task date with an optional time.
 */
final class TaskDateTime {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter TIME_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final LocalDate date;
    private final LocalTime time;

    /** Creates a task date with an optional time. */
    private TaskDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Parses a date and optional time from user input or saved data.
     *
     * @param value Date in {@code yyyy-MM-dd}, {@code yyyy-MM-dd HHmm}, or stored ISO format.
     * @return Parsed task date and optional time.
     */
    static TaskDateTime parse(String value) {
        assert value != null && !value.isBlank() : "A task date must not be blank";
        String[] dateAndTime = value.trim().split("[T\\s]+", 2);
        LocalDate date = LocalDate.parse(dateAndTime[0]);
        LocalTime time = dateAndTime.length == 2 ? parseTime(dateAndTime[1]) : null;
        return new TaskDateTime(date, time);
    }

    /** Returns whether this value is on the requested date. */
    boolean isOn(LocalDate date) {
        return this.date.equals(date);
    }

    /**
     * Returns whether this value is before another task date and optional time.
     * Missing times are treated as unspecified rather than midnight.
     */
    boolean isBefore(TaskDateTime other) {
        if (!date.equals(other.date)) {
            return date.isBefore(other.date);
        }
        return time != null && other.time != null && time.isBefore(other.time);
    }

    /** Returns the date component. */
    LocalDate getDate() {
        return date;
    }

    /** Returns the stable ISO representation used by storage. */
    String toStorageString() {
        return time == null ? date.toString() : date + "T" + time;
    }

    /** Returns the user-friendly date and optional time. */
    String toDisplayString() {
        String formattedValue = date.format(DATE_DISPLAY_FORMAT);
        return time == null ? formattedValue : formattedValue + " " + time.format(TIME_DISPLAY_FORMAT);
    }

    /** Parses a user-entered or ISO-formatted time. */
    private static LocalTime parseTime(String time) {
        return time.contains(":") ? LocalTime.parse(time) : LocalTime.parse(time, TIME_INPUT_FORMAT);
    }
}
