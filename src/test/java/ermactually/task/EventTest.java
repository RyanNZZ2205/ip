package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests the validation behavior of {@link Event}.
 */
public class EventTest {
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
        assertConstructorThrows(
                "meeting", "2026-08-26", "2026-08-25",
                "how can the event end before it starts?");
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
}
