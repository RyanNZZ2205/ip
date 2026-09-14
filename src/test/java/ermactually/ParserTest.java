package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ermactually.task.Deadline;
import ermactually.task.Event;
import ermactually.task.SortDirection;
import ermactually.task.Todo;

/**
 * Tests the command parsing behavior provided by {@link Parser}.
 */
public class ParserTest {
    private static final String INVALID_TASK_NUMBER_MESSAGE =
            "Please provide a valid task number.";
    private static final String INVALID_SORT_MESSAGE = "Please use: sort [asc|desc].";

    @Test
    public void getCommandType_allSupportedCommands_returnsExpectedTypes() {
        assertEquals(Parser.CommandType.BYE, Parser.getCommandType("bye"));
        assertEquals(Parser.CommandType.LIST, Parser.getCommandType("list"));
        assertEquals(Parser.CommandType.FIND, Parser.getCommandType("find"));
        assertEquals(Parser.CommandType.ON, Parser.getCommandType("on 2026-09-14"));
        assertEquals(Parser.CommandType.MARK, Parser.getCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, Parser.getCommandType("unmark 1"));
        assertEquals(Parser.CommandType.DELETE, Parser.getCommandType("delete 1"));
        assertEquals(Parser.CommandType.TODO, Parser.getCommandType("todo read"));
        assertEquals(Parser.CommandType.DEADLINE,
                Parser.getCommandType("deadline submit /by 2026-09-14"));
        assertEquals(Parser.CommandType.EVENT,
                Parser.getCommandType("event meeting /from 2026-09-14 /to 2026-09-14"));
    }

    @Test
    public void getCommandType_commandPrefixWithoutSeparator_returnsUnknown() {
        assertEquals(Parser.CommandType.UNKNOWN, Parser.getCommandType("listing"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.getCommandType("findings"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.getCommandType("sorter"));
        assertEquals(Parser.CommandType.UNKNOWN, Parser.getCommandType(""));
    }

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
    public void parseDate_validDate_returnsLocalDate() throws ErmActuallyException {
        assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("on 2024-02-29"));
    }

    @Test
    public void parseDate_missingOrInvalidDate_exceptionThrown() {
        String missingDateMessage = "Please provide a date in yyyy-MM-dd format.";
        String invalidDateMessage = "Please provide a valid date in yyyy-MM-dd format.";

        assertParserError(missingDateMessage, () -> Parser.parseDate("on"));
        assertParserError(invalidDateMessage, () -> Parser.parseDate("on 2023-02-29"));
        assertParserError(invalidDateMessage, () -> Parser.parseDate("on 14-09-2026"));
    }

    @Test
    public void parseTodo_validCommand_returnsTrimmedTodo() throws ErmActuallyException {
        Todo todo = Parser.parseTodo("todo   read book   ");

        assertEquals("read book", todo.getDescription());
    }

    @Test
    public void parseTodo_missingDescription_exceptionThrown() {
        assertParserError("Please add a description for todo!", () -> Parser.parseTodo("todo   "));
    }

    @Test
    public void parseDeadline_validCommand_returnsDeadline() throws ErmActuallyException {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2026-09-14 1900");

        assertEquals("submit report", deadline.getDescription());
        assertEquals("2026-09-14T19:00", deadline.toStorageString());
    }

    @Test
    public void parseDeadline_missingFields_exceptionThrown() {
        String missingDelimiterMessage = "Please add a /by for the deadline.";

        assertParserError(missingDelimiterMessage, () -> Parser.parseDeadline("deadline submit report"));
        assertParserError(missingDelimiterMessage, () -> Parser.parseDeadline("deadline /by 2026-09-14"));
        assertParserError("Please add a deadline using /by.", () ->
                Parser.parseDeadline("deadline submit report /by"));
    }

    @Test
    public void parseEvent_validCommand_returnsEvent() throws ErmActuallyException {
        Event event = Parser.parseEvent(
                "event project meeting /from 2026-09-14 0900 /to 2026-09-14 1000");

        assertEquals("project meeting", event.getDescription());
        assertEquals("2026-09-14T09:00", event.getFromStorageString());
        assertEquals("2026-09-14T10:00", event.getToStorageString());
    }

    @Test
    public void parseEvent_missingFields_exceptionThrown() {
        String emptyEndpointMessage =
                "Event /to and /from details cannot be empty! Please add them in.";

        assertParserError("Please add a /from for the event!", () -> Parser.parseEvent("event meeting"));
        assertParserError("Please add a /to and /from for the event!", () ->
                Parser.parseEvent("event meeting /from 2026-09-14"));
        assertParserError("Please add a description for this event!", () ->
                Parser.parseEvent("event /from 2026-09-14 /to 2026-09-15"));
        assertParserError(emptyEndpointMessage, () ->
                Parser.parseEvent("event meeting /from /to 2026-09-15"));
        assertParserError(emptyEndpointMessage, () ->
                Parser.parseEvent("event meeting /from 2026-09-14 /to"));
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

    /** Runs parsing code and verifies its user-facing error message. */
    private void assertParserError(String expectedMessage, ThrowingOperation operation) {
        ErmActuallyException exception = assertThrows(ErmActuallyException.class, operation::run);

        assertEquals(expectedMessage, exception.getMessage());
    }

    /** Represents parser code expected to throw {@link ErmActuallyException}. */
    @FunctionalInterface
    private interface ThrowingOperation {
        /** Runs the parser operation. */
        void run() throws ErmActuallyException;
    }
}
