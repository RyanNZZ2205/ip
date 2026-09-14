package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ermactually.task.SortDirection;

/**
 * Tests the command parsing behavior provided by {@link Parser}.
 */
public class ParserTest {
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "Please provide a valid task number.";
    private static final String INVALID_SORT_MESSAGE = "Please use: sort [asc|desc].";

    @Test
    public void getCommandType_sortCommands_returnsSort() {
        assertEquals(Parser.CommandType.SORT, Parser.getCommandType("sort"));
        assertEquals(Parser.CommandType.SORT, Parser.getCommandType("sort asc"));
        assertEquals(Parser.CommandType.SORT, Parser.getCommandType("sort\tdesc"));
    }

    @Test
    public void getCommandType_differentSortCasing_returnsUnknown() {
        assertEquals(Parser.CommandType.UNKNOWN, Parser.getCommandType("Sort"));
    }

    @Test
    public void parseSortDirection_missingDirection_returnsAscending()
            throws ErmActuallyException {
        assertEquals(SortDirection.ASCENDING, Parser.parseSortDirection("sort"));
    }

    @Test
    public void parseSortDirection_supportedDirections_returnsRequestedDirection()
            throws ErmActuallyException {
        assertEquals(SortDirection.ASCENDING, Parser.parseSortDirection("sort   asc"));
        assertEquals(SortDirection.DESCENDING, Parser.parseSortDirection("sort\t\tdesc"));
    }

    @Test
    public void parseSortDirection_unsupportedOrAdditionalArguments_exceptionThrown() {
        List<String> invalidCommands = List.of(
                "sort ascending", "sort descending", "sort date", "sort deadline",
                "sort ASC", "sort DESC", "sort asc desc", "sort 1", "sort /");

        for (String command : invalidCommands) {
            ErmActuallyException exception = assertThrows(
                    ErmActuallyException.class, () -> Parser.parseSortDirection(command));
            assertEquals(INVALID_SORT_MESSAGE, exception.getMessage());
        }
    }

    @Test
    public void getCommandType_findCommand_returnsFind() {
        assertEquals(Parser.CommandType.FIND, Parser.getCommandType("find book"));
    }

    @Test
    public void getCommandType_tabSeparatedCommands_returnsMatchingTypes() {
        assertEquals(Parser.CommandType.FIND, Parser.getCommandType("find\tbook"));
        assertEquals(Parser.CommandType.MARK, Parser.getCommandType("mark\t1"));
        assertEquals(Parser.CommandType.DEADLINE,
                Parser.getCommandType("deadline\treport /by 2026-09-15"));
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
        assertEquals(2, Parser.parseTaskIndex("mark\t3"));
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

    @Test
    public void parseDeadline_flexibleWhitespace_returnsDeadline() throws ErmActuallyException {
        assertEquals("[D][ ] submit report (by: Sep 15 2026)",
                Parser.parseDeadline("deadline\tsubmit report\t/by\t2026-09-15").toString());
    }

    @Test
    public void parseDeadline_duplicateSeparator_exceptionThrown() {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> Parser.parseDeadline(
                        "deadline submit report /by 2026-09-15 /by 2026-09-16"));

        assertEquals("Please use exactly one /by for the deadline.", exception.getMessage());
    }

    @Test
    public void parseEvent_duplicateOrReorderedSeparators_exceptionThrown() {
        List<String> invalidCommands = List.of(
                "event meeting /from 2026-09-15 /from 2026-09-16 /to 2026-09-17",
                "event meeting /from 2026-09-15 /to 2026-09-16 /to 2026-09-17",
                "event meeting /to 2026-09-16 /from 2026-09-15");

        for (String command : invalidCommands) {
            assertThrows(ErmActuallyException.class, () -> Parser.parseEvent(command));
        }
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
