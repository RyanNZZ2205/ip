package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests validation and representation behavior provided by {@link Event}.
 */
public class EventTest {
    private static final String INVALID_ORDER_MESSAGE =
            "how can the event end before it starts?";

    @Test
    public void constructor_blankDescription_exceptionThrown() {
        assertConstructorThrows(
                "", "2026-08-25", "2026-08-26",
                "Please add in a description of the event!");
    }

    @Test
    public void constructor_invalidStart_exceptionThrown() {
        assertConstructorThrows(
                "meeting", "Tuesday", "2026-08-26",
                "actually the format of start is in yyyy-MM-dd or yyyy-MM-dd HHmm!");
    }

    @Test
    public void constructor_invalidEnd_exceptionThrown() {
        assertConstructorThrows(
                "meeting", "2026-08-25", "Tuesday",
                "actually the format of end is in yyyy-MM-dd or yyyy-MM-dd HHmm!");
    }

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
        assertConstructorThrows(
                "meeting", "2026-09-15 0900", "2026-09-15",
                "Please provide times for both event endpoints, or for neither endpoint.");
    }

    @Test
    public void constructor_nonexistentDate_exceptionThrown() {
        assertConstructorThrows(
                "meeting", "2026-02-30", "2026-03-01",
                "actually the format of start is in yyyy-MM-dd or yyyy-MM-dd HHmm!");
    }

    /**
     * Verifies the exception and message produced for invalid event input.
     *
     * @param description Event description to test.
     * @param from Event start to test.
     * @param to Event end to test.
     * @param expectedMessage Expected validation message.
     */
    private void assertConstructorThrows(String description, String from, String to,
            String expectedMessage) {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Event(description, from, to));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /** Verifies that an invalid endpoint order produces the standard error. */
    private void assertInvalidOrder(String from, String to) {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Event("meeting", from, to));

        assertEquals(INVALID_ORDER_MESSAGE, exception.getMessage());
    }
}
