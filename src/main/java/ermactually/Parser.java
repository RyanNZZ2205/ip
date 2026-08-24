package ermactually;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import ermactually.task.Deadline;
import ermactually.task.Event;
import ermactually.task.Todo;

/**
 * Recognizes user commands and converts their arguments into application values.
 */
public class Parser {
    /** Commands understood by ErmActually. */
    public enum CommandType {
        BYE, LIST, ON, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, UNKNOWN
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
        }
        return CommandType.UNKNOWN;
    }

    /** Parses the one-based task number in a command into a zero-based index. */
    public static int parseTaskIndex(String command) throws ErmActuallyException {
        int firstSpace = command.indexOf(' ');
        String argument = firstSpace < 0 ? "" : command.substring(firstSpace + 1).trim();
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
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ErmActuallyException("Please add a description for todo!");
        }
        return new Todo(description);
    }

    /** Creates a deadline from a validated {@code deadline} command. */
    public static Deadline parseDeadline(String command) throws ErmActuallyException {
        String details = command.substring("deadline".length()).trim();
        String[] parts = details.split(" /by", 2);
        if (parts.length != 2) {
            throw new ErmActuallyException("Please add a /by for the deadline.");
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
        String details = command.substring("event".length()).trim();
        String[] fromSplit = details.split("/from", 2);
        if (fromSplit.length != 2) {
            throw new ErmActuallyException("Please add a /from for the event!");
        }

        String description = fromSplit[0].trim();
        String[] toSplit = fromSplit[1].split("/to", 2);
        if (toSplit.length != 2) {
            throw new ErmActuallyException("Please add a /to and /from for the event!");
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
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }
}
