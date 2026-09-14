package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/** Tests validation and representation behavior provided by {@link Event}. */
public class EventTest {
    private static final String INVALID_ORDER_MESSAGE =
            "The event end must be after its start.";

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        assertInvalidOrder("2026-09-16", "2026-09-15");
        assertInvalidOrder("2026-09-15 1700", "2026-09-15 0900");
    }

    @Test
    public void constructor_endEqualsStart_exceptionThrown() {
        assertInvalidOrder("2026-09-15", "2026-09-15");
        assertInvalidOrder("2026-09-15 0900", "2026-09-15 0900");
    }

    @Test
    public void constructor_sameDateWithOnlyOneTime_exceptionThrown() {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () ->
                        new Event("meeting", "2026-09-15 0900", "2026-09-15"));

        assertEquals("Please provide times for both event endpoints, or for neither endpoint.",
                exception.getMessage());
    }

    @Test
    public void constructor_nonexistentDate_exceptionThrown() {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () ->
                        new Event("meeting", "2026-02-30", "2026-03-01"));

        assertEquals("Please enter the event start in yyyy-MM-dd or yyyy-MM-dd HHmm format.",
                exception.getMessage());
    }

    /** Verifies that an invalid endpoint order produces the standard error. */
    private void assertInvalidOrder(String from, String to) {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Event("meeting", from, to));

        assertEquals(INVALID_ORDER_MESSAGE, exception.getMessage());
    }
}
