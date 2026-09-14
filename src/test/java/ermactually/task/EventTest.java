package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/** Tests validation, dates, storage, and display behavior of events. */
public class EventTest {
    @Test
    public void constructor_dateOnlyValues_exposesDateWithoutTime() throws ErmActuallyException {
        Event event = new Event("  holiday  ", "2026-09-14", "2026-09-15");

        assertEquals("holiday", event.getDescription());
        assertEquals(LocalDate.of(2026, 9, 14), event.getRelevantDate().orElseThrow());
        assertTrue(event.getRelevantTime().isEmpty());
        assertEquals("2026-09-14", event.getFromStorageString());
        assertEquals("2026-09-15", event.getToStorageString());
        assertEquals("[E][ ] holiday (from: Sep 14 2026 to: Sep 15 2026)", event.toString());
    }

    @Test
    public void constructor_userAndStoredTimes_preservesTimes() throws ErmActuallyException {
        Event userEvent = new Event("meeting", "2026-09-14 0905", "2026-09-14 1730");
        Event storedEvent = new Event("meeting", "2026-09-14T09:05", "2026-09-14T17:30");

        assertEquals(LocalTime.of(9, 5), userEvent.getRelevantTime().orElseThrow());
        assertEquals("2026-09-14T09:05", userEvent.getFromStorageString());
        assertEquals("2026-09-14T17:30", userEvent.getToStorageString());
        assertEquals(userEvent.toString(), storedEvent.toString());
        assertEquals("[E][ ] meeting (from: Sep 14 2026 9:05 AM"
                + " to: Sep 14 2026 5:30 PM)", userEvent.toString());
    }

    @Test
    public void occursOn_beforeWithinAndAfterRange_returnsExpectedValues()
            throws ErmActuallyException {
        Event event = new Event("conference", "2026-09-14", "2026-09-16");

        assertFalse(event.occursOn(LocalDate.of(2026, 9, 13)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 14)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 15)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 16)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 17)));
    }

    @Test
    public void constructor_missingRequiredValues_exceptionThrown() {
        assertEventError("The description of an event cannot be empty.",
                null, "2026-09-14", "2026-09-15");
        assertEventError("The description of an event cannot be empty.",
                "  ", "2026-09-14", "2026-09-15");
        assertEventError("The event start cannot be empty.",
                "meeting", null, "2026-09-15");
        assertEventError("The event start cannot be empty.",
                "meeting", " ", "2026-09-15");
        assertEventError("The event end cannot be empty.",
                "meeting", "2026-09-14", null);
        assertEventError("The event end cannot be empty.",
                "meeting", "2026-09-14", " ");
    }

    @Test
    public void constructor_invalidDatesAndTimes_exceptionIdentifiesEndpoint() {
        String startError = "Please enter the event start in yyyy-MM-dd or yyyy-MM-dd HHmm format.";
        String endError = "Please enter the event end in yyyy-MM-dd or yyyy-MM-dd HHmm format.";

        assertEventError(startError, "meeting", "2026-02-30", "2026-09-15");
        assertEventError(startError, "meeting", "2026-09-14 2500", "2026-09-15");
        assertEventError(endError, "meeting", "2026-09-14", "not-a-date");
        assertEventError(endError, "meeting", "2026-09-14", "2026-09-15T99:00");
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        String expectedMessage = "The event end cannot be before its start.";

        assertEventError(expectedMessage, "trip", "2026-09-15", "2026-09-14");
        assertEventError(expectedMessage, "meeting",
                "2026-09-14 1000", "2026-09-14 0959");
    }

    @Test
    public void constructor_sameEndpointOrPartiallyTimedEndpoint_isAccepted()
            throws ErmActuallyException {
        Event instant = new Event("instant", "2026-09-14 1000", "2026-09-14 1000");
        Event onlyStartTimed = new Event("flexible", "2026-09-14 1000", "2026-09-14");
        Event onlyEndTimed = new Event("flexible", "2026-09-14", "2026-09-14 0900");

        assertEquals("2026-09-14T10:00", instant.getFromStorageString());
        assertEquals("2026-09-14", onlyStartTimed.getToStorageString());
        assertEquals("2026-09-14T09:00", onlyEndTimed.getToStorageString());
    }

    /** Verifies an event validation error and its exact user-facing message. */
    private void assertEventError(String expectedMessage, String description,
            String from, String to) {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Event(description, from, to));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
