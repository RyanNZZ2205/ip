package ermactually;

import java.nio.file.Path;

import ermactually.task.Task;
import ermactually.task.TaskList;

/**
 * Coordinates the user interface, task storage, task list, and command processing.
 */
public class ErmActually {
    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    private Parser.CommandType commandType = Parser.CommandType.UNKNOWN;
    private String startupError;

    /**
     * Creates the application with a console interface and file-backed storage.
     *
     * @param filePath Path of the file used to store tasks.
     */
    public ErmActually(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(filePath));
        this.tasks = new TaskList();
        loadTasks();
    }

    /** Runs the application until input ends or the user enters {@code bye}. */
    public void run() {
        ui.showWelcome();
        if (startupError != null) {
            ui.showResponse(startupError);
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            String response = getResponse(command);
            ui.showResponse(response);
            if (commandType == Parser.CommandType.BYE) {
                return;
            }
        }
    }

    /** Loads saved tasks and records an error that either interface can display. */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (ErmActuallyException e) {
            tasks = new TaskList();
            startupError = ui.formatError(e.getMessage());
        }
    }

    /**
     * Adds and saves a parsed task, then formats its confirmation.
     *
     * @param task Parsed task to add.
     */
    private String addTask(Task task) throws ErmActuallyException {
        int originalTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == originalTaskCount + 1
                : "Adding a task must increase the task count by one";
        saveTasks();
        return ui.formatTaskAdded(task, tasks.size());
    }

    /** Saves the task list. */
    private void saveTasks() throws ErmActuallyException {
        storage.save(tasks);
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input User's chat message.
     * @return Generated response.
     */
    public String getResponse(String input) {
        String command = input.trim();
        commandType = Parser.getCommandType(command);

        try {
            switch (commandType) {
                case BYE:
                    return ui.formatFarewell();
                case LIST:
                    return ui.formatTaskList(tasks);
                case FIND:
                    return ui.formatMatchingTasks(tasks, Parser.parseKeyword(command));
                case ON:
                    return ui.formatTasksOnDate(tasks, Parser.parseDate(command));
                case MARK:
                    int markIndex = Parser.parseTaskIndex(command);
                    tasks.mark(markIndex);
                    assert tasks.get(markIndex).isDone()
                            : "A marked task must be complete";
                    saveTasks();
                    return ui.formatTaskMarked(tasks.get(markIndex));
                case UNMARK:
                    int unmarkIndex = Parser.parseTaskIndex(command);
                    tasks.unmark(unmarkIndex);
                    assert !tasks.get(unmarkIndex).isDone()
                            : "An unmarked task must be incomplete";
                    saveTasks();
                    return ui.formatTaskUnmarked(tasks.get(unmarkIndex));
                case DELETE:
                    Task removedTask = tasks.delete(Parser.parseTaskIndex(command));
                    saveTasks();
                    return ui.formatTaskDeleted(removedTask, tasks.size());
                case TODO:
                    return addTask(Parser.parseTodo(command));
                case DEADLINE:
                    return addTask(Parser.parseDeadline(command));
                case EVENT:
                    return addTask(Parser.parseEvent(command));
                default:
                    return ui.formatError("actually.. what are you saying??");
            }
        } catch (ErmActuallyException e) {
            commandType = Parser.CommandType.UNKNOWN;
            return ui.formatError(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            commandType = Parser.CommandType.UNKNOWN;
            return ui.formatError("That task number does not exist.");
        }
    }

    /** Returns an error encountered while loading tasks, or {@code null} if loading succeeded. */
    public String getStartupError() {
        return startupError;
    }

    /** Returns the welcome message shared by the console and graphical interfaces. */
    public String getWelcomeMessage() {
        return ui.formatWelcome();
    }

    /** Returns the type of the most recently processed command. */
    public Parser.CommandType getCommandType() {
        return commandType;
    }

    /**
     * Starts ErmActually using the default task data file.
     *
     * @param args Optional task data file path used primarily by automated UI tests.
     */
    public static void main(String[] args) {
        String filePath = args.length == 0 ? "data/ErmActually.txt" : args[0];
        new ErmActually(filePath).run();
    }
}
