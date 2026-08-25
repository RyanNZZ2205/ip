package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests the behavior of {@link Deadline}.
 */
public class DeadlineTest {
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
        //Arrange
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
        //Arrange
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
    public void constructor_blankDescription_exceptionThrown() {
        assertThrows(
                ErmActuallyException.class,
                () -> new Deadline("", "2026-08-25"));
    }

    @Test
    public void constructor_blankDeadline_exceptionThrown() {
        assertThrows(
                ErmActuallyException.class,
                () -> new Deadline("submit report", ""));
    }

    @Test
    public void constructor_invalidDate_exceptionThrown() {
        assertThrows(
                ErmActuallyException.class,
                () -> new Deadline("submit report", "2026-02-30"));
    }

    @Test
    public void constructor_invalidTime_exceptionThrown() {
        assertThrows(
                ErmActuallyException.class,
                () -> new Deadline(
                        "submit report",
                        "2026-08-25 2500"));
    }
}
