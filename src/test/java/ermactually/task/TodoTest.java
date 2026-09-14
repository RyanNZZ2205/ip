package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/**
 * Tests the validation behavior of {@link Todo}.
 */
public class TodoTest {
    @Test
    public void constructor_blankDescription_exceptionThrown() {
        ErmActuallyException exception =
                assertThrows(ErmActuallyException.class, () -> new Todo("   "));

        assertEquals("Please add a description of the todo!", exception.getMessage());
    }
}
