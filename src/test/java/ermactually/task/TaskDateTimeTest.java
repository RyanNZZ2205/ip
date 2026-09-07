package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing, formatting, and ordering behavior provided by {@link TaskDateTime}.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_dateOnly_preservesDateWithoutInventingTime() {
        TaskDateTime date = TaskDateTime.parse("2026-08-25");

        assertEquals("2026-08-25", date.toStorageString());
        assertEquals("Aug 25 2026", date.toDisplayString());
    }

    @Test
    public void parse_userTime_formatsForStorageAndDisplay() {
        TaskDateTime dateTime = TaskDateTime.parse("2026-08-25 1900");

        assertEquals("2026-08-25T19:00", dateTime.toStorageString());
        assertEquals("Aug 25 2026 7:00 PM", dateTime.toDisplayString());
    }

    @Test
    public void parse_storedIsoTime_preservesDateTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2026-08-25T19:00");

        assertEquals("2026-08-25T19:00", dateTime.toStorageString());
    }

    @Test
    public void isBefore_earlierDate_returnsTrue() {
        TaskDateTime earlier = TaskDateTime.parse("2026-08-24 2000");
        TaskDateTime later = TaskDateTime.parse("2026-08-25 0900");

        assertTrue(earlier.isBefore(later));
    }

    @Test
    public void isBefore_earlierTimeOnSameDate_returnsTrue() {
        TaskDateTime earlier = TaskDateTime.parse("2026-08-25 0900");
        TaskDateTime later = TaskDateTime.parse("2026-08-25 1000");

        assertTrue(earlier.isBefore(later));
    }

    @Test
    public void isBefore_missingTimeOnSameDate_returnsFalse() {
        TaskDateTime dateOnly = TaskDateTime.parse("2026-08-25");
        TaskDateTime dateTime = TaskDateTime.parse("2026-08-25 1000");

        assertFalse(dateOnly.isBefore(dateTime));
        assertFalse(dateTime.isBefore(dateOnly));
    }
}
