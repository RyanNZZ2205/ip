package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the command parsing behavior provided by {@link Parser}.
 */
public class ParserTest {
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "Please provide a valid task number.";

    @Test
    public void getCommandType_findCommand_returnsFind() {
        assertEquals(Parser.CommandType.FIND, Parser.getCommandType("find book"));
    }

    @Test
    public void parseKeyword_validKeyword_returnsTrimmedKeyword() throws ErmActuallyException {
        assertEquals("read book", Parser.parseKeyword("find   read book   "));
    }

    @Test
    public void parseKeyword_missingKeyword_exceptionThrown() {
        ErmActuallyException exception =
                assertThrows(ErmActuallyException.class, () -> Parser.parseKeyword("find   "));

        assertEquals("Please provide a keyword to find.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_firstTask_returnsZero() throws ErmActuallyException {
        assertEquals(0, Parser.parseTaskIndex("mark 1"));
    }

    @Test
    public void parseTaskIndex_laterTask_returnsZeroBasedIndex() throws ErmActuallyException {
        assertEquals(4, Parser.parseTaskIndex("mark 5"));
    }

    @Test
    public void parseTaskIndex_surroundingWhitespace_returnsCorrectIndex()
            throws ErmActuallyException {
        assertEquals(2, Parser.parseTaskIndex("mark   3   "));
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_exceptionThrown() {
        assertInvalidTaskNumber("mark");
    }

    @Test
    public void parseTaskIndex_blankArgument_exceptionThrown() {
        assertInvalidTaskNumber("mark   ");
    }

    @Test
    public void parseTaskIndex_nonNumericArgument_exceptionThrown() {
        assertInvalidTaskNumber("mark abc");
    }

    @Test
    public void parseTaskIndex_zeroTaskNumber_exceptionThrown() {
        assertInvalidTaskNumber("mark 0");
    }

    @Test
    public void parseTaskIndex_negativeTaskNumber_exceptionThrown() {
        assertInvalidTaskNumber("mark -1");
    }

    @Test
    public void parseTaskIndex_decimalTaskNumber_exceptionThrown() {
        assertInvalidTaskNumber("mark 1.5");
    }

    @Test
    public void parseTaskIndex_multipleArguments_exceptionThrown() {
        assertInvalidTaskNumber("mark 1 2");
    }

    /**
     * Verifies that an invalid task-number command produces the standard user-facing error.
     *
     * @param command command containing an invalid task-number argument
     */
    private void assertInvalidTaskNumber(String command) {
        ErmActuallyException exception =
                assertThrows(ErmActuallyException.class, () -> Parser.parseTaskIndex(command));

        assertEquals(INVALID_TASK_NUMBER_MESSAGE, exception.getMessage());
    }
}
