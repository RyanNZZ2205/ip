package ermactually;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import ermactually.task.Deadline;
import ermactually.task.Event;
import ermactually.task.SortDirection;
import ermactually.task.Todo;

/**
 * Recognizes user commands and converts their arguments into application values.
 */
public class Parser {
    private static final Pattern DEADLINE_SEPARATOR = Pattern.compile("(?:^|\\s+)/by(?=\\s|$)");
    private static final Pattern EVENT_FROM_SEPARATOR = Pattern.compile("(?:^|\\s+)/from(?=\\s|$)");
    private static final Pattern EVENT_TO_SEPARATOR = Pattern.compile("(?:^|\\s+)/to(?=\\s|$)");

    /** Commands understood by ErmActually. */
    public enum CommandType {
        BYE, LIST, FIND, ON, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, SORT, UNKNOWN
    }

    /**
     * Identifies the command represented by a complete input line.
     *
     * @param command Complete trimmed user command.
     * @return Recognized command type, or {@link CommandType#UNKNOWN}.
     */
    public static CommandType getCommandType(String command) {
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (matchesCommand(command, "find")) {
            return CommandType.FIND;
        } else if (matchesCommand(command, "on")) {
            return CommandType.ON;
        } else if (matchesCommand(command, "mark")) {
            return CommandType.MARK;
        } else if (matchesCommand(command, "unmark")) {
            return CommandType.UNMARK;
        } else if (matchesCommand(command, "delete")) {
            return CommandType.DELETE;
        } else if (matchesCommand(command, "todo")) {
            return CommandType.TODO;
        } else if (matchesCommand(command, "deadline")) {
            return CommandType.DEADLINE;
        } else if (matchesCommand(command, "event")) {
            return CommandType.EVENT;
        } else if (matchesCommand(command, "sort")) {
            return CommandType.SORT;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses the optional direction supplied to a {@code sort} command.
     *
     * @param command Complete sort command.
     * @return Requested direction, defaulting to ascending.
     * @throws ErmActuallyException If the direction is unsupported or extra arguments are present.
     */
    public static SortDirection parseSortDirection(String command) throws ErmActuallyException {
        assert getCommandType(command) == CommandType.SORT
                : "parseSortDirection must receive a sort command";
        String direction = command.substring("sort".length()).trim();
        if (direction.isEmpty() || direction.equals("asc")) {
            return SortDirection.ASCENDING;
        } else if (direction.equals("desc")) {
            return SortDirection.DESCENDING;
        }
        throw new ErmActuallyException("Please use: sort [asc|desc].");
    }

    /**
     * Extracts the keyword supplied to a {@code find} command.
     *
     * @param command Complete find command.
     * @return Non-empty keyword to search for.
     * @throws ErmActuallyException If no keyword was provided.
     */
    public static String parseKeyword(String command) throws ErmActuallyException {
        assert getCommandType(command) == CommandType.FIND
                : "parseKeyword must receive a find command";
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new ErmActuallyException("Please provide a keyword to find.");
        }
        return keyword;
    }

    /** Parses the one-based task number in a command into a zero-based index. */
    public static int parseTaskIndex(String command) throws ErmActuallyException {
        CommandType commandType = getCommandType(command);
        assert commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                || commandType == CommandType.DELETE
                : "parseTaskIndex must receive a task-changing command";
        int firstWhitespace = findFirstWhitespace(command);
        String argument = firstWhitespace < 0 ? "" : command.substring(firstWhitespace + 1).trim();
        try {
            int taskNumber = Integer.parseInt(argument);
            if (taskNumber < 1) {
                throw new ErmActuallyException("Please provide a valid task number.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new ErmActuallyException("Please provide a valid task number.");
        }
    }

    /** Parses the ISO date supplied to an {@code on} command. */
    public static LocalDate parseDate(String command) throws ErmActuallyException {
        assert getCommandType(command) == CommandType.ON
                : "parseDate must receive an on command";
        String dateText = command.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new ErmActuallyException("Please provide a date in yyyy-MM-dd format.");
        }
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new ErmActuallyException("Please provide a valid date in yyyy-MM-dd format.");
        }
    }

    /** Creates a todo from a validated {@code todo} command. */
    public static Todo parseTodo(String command) throws ErmActuallyException {
        assert getCommandType(command) == CommandType.TODO
                : "parseTodo must receive a todo command";
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ErmActuallyException("Please add a description for todo!");
        }
        return new Todo(description);
    }

    /** Creates a deadline from a validated {@code deadline} command. */
    public static Deadline parseDeadline(String command) throws ErmActuallyException {
        assert getCommandType(command) == CommandType.DEADLINE
                : "parseDeadline must receive a deadline command";
        String details = command.substring("deadline".length()).trim();
        String[] parts = DEADLINE_SEPARATOR.split(details, -1);
        if (parts.length != 2) {
            throw new ErmActuallyException("Please use exactly one /by for the deadline.");
        }

        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new ErmActuallyException("Please add a description for deadline!");
        }
        if (by.isEmpty()) {
            throw new ErmActuallyException("Please add a deadline using /by.");
        }
        return new Deadline(description, by);
    }

    /** Creates an event from a validated {@code event} command. */
    public static Event parseEvent(String command) throws ErmActuallyException {
        assert getCommandType(command) == CommandType.EVENT
                : "parseEvent must receive an event command";
        String details = command.substring("event".length()).trim();
        String[] fromSplit = EVENT_FROM_SEPARATOR.split(details, -1);
        if (fromSplit.length != 2) {
            throw new ErmActuallyException("Please use exactly one /from for the event.");
        }

        String description = fromSplit[0].trim();
        String[] toSplit = EVENT_TO_SEPARATOR.split(fromSplit[1], -1);
        if (toSplit.length != 2) {
            throw new ErmActuallyException("Please use exactly one /to after /from for the event.");
        }

        String from = toSplit[0].trim();
        String to = toSplit[1].trim();
        if (description.isEmpty()) {
            throw new ErmActuallyException("Please add a description for this event!");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new ErmActuallyException("Event /to and /from details cannot be empty! Please add them in.");
        }
        return new Event(description, from, to);
    }

    /** Returns whether the input is a keyword alone or followed by arguments. */
    private static boolean matchesCommand(String command, String keyword) {
        return command.equals(keyword)
                || command.length() > keyword.length()
                && command.startsWith(keyword)
                && Character.isWhitespace(command.charAt(keyword.length()));
    }

    /** Returns the position of the first whitespace character, or {@code -1} if absent. */
    private static int findFirstWhitespace(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
