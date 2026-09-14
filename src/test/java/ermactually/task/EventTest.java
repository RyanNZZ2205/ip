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
    private static final String INVALID_ORDER_MESSAGE =
            "how can the event end before it starts?";

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
        assertEventError("Please add in a description of the event!",
                null, "2026-09-14", "2026-09-15");
        assertEventError("Please add in a description of the event!",
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
        String startError =
                "actually the format of start is in yyyy-MM-dd or yyyy-MM-dd HHmm!";
        String endError =
                "actually the format of end is in yyyy-MM-dd or yyyy-MM-dd HHmm!";

        assertEventError(startError, "meeting", "2026-02-30", "2026-09-15");
        assertEventError(startError, "meeting", "2026-09-14 2500", "2026-09-15");
        assertEventError(endError, "meeting", "2026-09-14", "not-a-date");
        assertEventError(endError, "meeting", "2026-09-14", "2026-09-15T99:00");
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertEventError(INVALID_ORDER_MESSAGE, "trip", "2026-09-15", "2026-09-14");
        assertEventError(INVALID_ORDER_MESSAGE, "meeting",
                "2026-09-14 1000", "2026-09-14 0959");
    }

    @Test
    public void constructor_endEqualsStart_exceptionThrown() {
        assertEventError(INVALID_ORDER_MESSAGE, "meeting", "2026-09-15", "2026-09-15");
        assertEventError(INVALID_ORDER_MESSAGE, "meeting",
                "2026-09-15 0900", "2026-09-15 0900");
    }

    @Test
    public void constructor_sameDateWithOnlyOneTime_exceptionThrown() {
        String errorMessage =
                "Please provide times for both event endpoints, or for neither endpoint.";

        assertEventError(errorMessage, "meeting", "2026-09-15 0900", "2026-09-15");
        assertEventError(errorMessage, "meeting", "2026-09-15", "2026-09-15 0900");
    }

    /** Verifies an event validation error and its exact user-facing message. */
    private void assertEventError(String expectedMessage, String description,
            String from, String to) {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Event(description, from, to));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
