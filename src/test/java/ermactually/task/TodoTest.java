package ermactually.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ermactually.ErmActuallyException;

/** Tests validation and formatting of undated todo tasks. */
public class TodoTest {
    private static final String BLANK_DESCRIPTION_MESSAGE =
            "The description of a todo cannot be empty.";

    @Test
    public void constructor_descriptionWithWhitespace_trimsDescription()
            throws ErmActuallyException {
        Todo todo = new Todo("  read book  ");

        assertEquals("read book", todo.getDescription());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void constructor_missingDescription_exceptionThrown() {
        assertInvalidDescription(null);
        assertInvalidDescription("");
        assertInvalidDescription(" \t ");
    }

    /** Verifies the standard validation failure for a todo description. */
    private void assertInvalidDescription(String description) {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Todo(description));

        assertEquals(BLANK_DESCRIPTION_MESSAGE, exception.getMessage());
    }
}
