package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests the behavior of {@link Deadline}.
 */
public class DeadlineTest {
    private static final String BLANK_DESCRIPTION_MESSAGE =
            "please add in a description for the deadline!";
    private static final String BLANK_DEADLINE_MESSAGE =
            "please add in a deadline! its actually using /by";
    private static final String INVALID_FORMAT_MESSAGE =
            "the deadline's format is actually in yyyy-MM-dd or yyyy-MM-dd HHmm!";

    @Test
    public void occursOn_sameDate_returnsTrue() throws ErmActuallyException {
        // Arrange
        Deadline deadline = new Deadline(
                "submit report",
                "2026-08-25");

        // Act
        boolean result = deadline.occursOn(
                LocalDate.of(2026, 8, 25));

        // Assert
        assertTrue(result);
    }

    @Test
    public void occursOn_dateBeforeDeadline_returnsFalse() throws ErmActuallyException {
        // Arrange
        Deadline deadline = new Deadline(
                "submit report",
                "2026-08-25");

        // Act
        boolean result = deadline.occursOn(
                LocalDate.of(2026, 8, 24));

        // Assert
        assertFalse(result);
    }

    @Test
    public void occursOn_dateAfterDeadline_returnsFalse() throws ErmActuallyException {
        // Arrange
        Deadline deadline = new Deadline(
                "submit report",
                "2026-08-25");

        // Act
        boolean result = deadline.occursOn(
                LocalDate.of(2026, 8, 26));

        // Assert
        assertFalse(result);
    }

    @Test
    public void toStorageString_dateOnly_returnsIsoDate()
            throws ErmActuallyException {
        // Arrange
        Deadline deadline = new Deadline(
                "submit report",
                "2026-08-25");

        // Act
        String result = deadline.toStorageString();

        // Assert
        assertEquals("2026-08-25", result);
    }

    @Test
    public void toStorageString_dateWithTime_returnsIsoDateTime()
            throws ErmActuallyException {
        // Arrange
        Deadline deadline = new Deadline(
                "submit report",
                "2026-08-25 1900");

        // Act
        String result = deadline.toStorageString();

        // Assert
        assertEquals("2026-08-25T19:00", result);
    }

    @Test
    public void toStorageString_storedIsoDateTime_preservesIsoDateTime()
            throws ErmActuallyException {
        Deadline deadline = new Deadline(
                "submit report",
                "2026-08-25T19:00");

        assertEquals("2026-08-25T19:00", deadline.toStorageString());
    }

    @Test
    public void relevantDateAndTime_dateOnly_returnsDateAndEmptyTime()
            throws ErmActuallyException {
        Deadline deadline = new Deadline("submit report", "2026-08-25");

        assertEquals(LocalDate.of(2026, 8, 25), deadline.getRelevantDate().orElseThrow());
        assertTrue(deadline.getRelevantTime().isEmpty());
    }

    @Test
    public void relevantDateAndTime_timedDeadline_returnsBothValues()
            throws ErmActuallyException {
        Deadline deadline = new Deadline("submit report", "2026-08-25 1900");

        assertEquals(LocalDate.of(2026, 8, 25), deadline.getRelevantDate().orElseThrow());
        assertEquals(LocalTime.of(19, 0), deadline.getRelevantTime().orElseThrow());
    }

    @Test
    public void constructor_descriptionWithWhitespace_trimsDescription()
            throws ErmActuallyException {
        Deadline deadline = new Deadline(
                "  submit report  ",
                "2026-08-25");

        assertEquals("submit report", deadline.getDescription());
    }

    @Test
    public void constructor_blankDescription_exceptionThrown() {
        assertConstructorThrows("", "2026-08-25", BLANK_DESCRIPTION_MESSAGE);
    }

    @Test
    public void constructor_whitespaceDescription_exceptionThrown() {
        assertConstructorThrows("   ", "2026-08-25", BLANK_DESCRIPTION_MESSAGE);
    }

    @Test
    public void constructor_nullDescription_exceptionThrown() {
        assertConstructorThrows(null, "2026-08-25", BLANK_DESCRIPTION_MESSAGE);
    }

    @Test
    public void constructor_blankDeadline_exceptionThrown() {
        assertConstructorThrows("submit report", "", BLANK_DEADLINE_MESSAGE);
    }

    @Test
    public void constructor_whitespaceDeadline_exceptionThrown() {
        assertConstructorThrows("submit report", "   ", BLANK_DEADLINE_MESSAGE);
    }

    @Test
    public void constructor_nullDeadline_exceptionThrown() {
        assertConstructorThrows("submit report", null, BLANK_DEADLINE_MESSAGE);
    }

    @Test
    public void constructor_invalidDate_exceptionThrown() {
        assertConstructorThrows("submit report", "2026-02-30", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void constructor_invalidTime_exceptionThrown() {
        assertConstructorThrows("submit report", "2026-08-25 2500", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void constructor_malformedStoredTime_exceptionThrown() {
        assertConstructorThrows("submit report", "2026-08-25T19:99", INVALID_FORMAT_MESSAGE);
    }

    @Test
    public void toString_dateOnly_returnsFriendlyDate() throws ErmActuallyException {
        Deadline deadline = new Deadline("submit report", "2026-08-25");

        assertEquals(
                "[D][ ] submit report (by: Aug 25 2026)",
                deadline.toString());
    }

    @Test
    public void toString_dateWithTime_returnsFriendlyDateAndTime()
            throws ErmActuallyException {
        Deadline deadline = new Deadline("submit report", "2026-08-25 1900");

        assertEquals(
                "[D][ ] submit report (by: Aug 25 2026 7:00 PM)",
                deadline.toString());
    }

    /**
     * Verifies the exception and user-facing message produced for invalid constructor input.
     *
     * @param description deadline description to test
     * @param by deadline date-time value to test
     * @param expectedMessage expected validation message
     */
    private void assertConstructorThrows(String description, String by, String expectedMessage) {
        ErmActuallyException exception =
                assertThrows(ErmActuallyException.class, () -> new Deadline(description, by));

        assertEquals(expectedMessage, exception.getMessage());
    }
}
