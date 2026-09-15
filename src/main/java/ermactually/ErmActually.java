package ermactually;

import java.nio.file.Path;

import ermactually.task.SortDirection;
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
        this(new Storage(Path.of(filePath)));
    }

    /**
     * Creates the application with supplied storage for deterministic integration testing.
     *
     * @param storage Storage used to load and save tasks.
     */
    ErmActually(Storage storage) {
        assert storage != null : "Application storage must exist";
        this.ui = new Ui();
        this.storage = storage;
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
        try {
            saveTasks();
        } catch (ErmActuallyException e) {
            tasks.delete(tasks.size() - 1);
            throw e;
        }
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
        if (input == null) {
            commandType = Parser.CommandType.UNKNOWN;
            return ui.formatError("Please enter a command.");
        }
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
                    return markTask(command);
                case UNMARK:
                    return unmarkTask(command);
                case DELETE:
                    return deleteTask(command);
                case TODO:
                    return addTask(Parser.parseTodo(command));
                case DEADLINE:
                    return addTask(Parser.parseDeadline(command));
                case EVENT:
                    return addTask(Parser.parseEvent(command));
                case SORT:
                    return sortTasks(command);
                default:
                    return ui.formatError("erm actually.. what are you trying to say??");
            }
        } catch (ErmActuallyException e) {
            commandType = Parser.CommandType.UNKNOWN;
            return ui.formatError(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            commandType = Parser.CommandType.UNKNOWN;
            return ui.formatError("actually that task number doesn't exist!");
        }
    }

    /** Marks and saves the requested task, restoring its original state if saving fails. */
    private String markTask(String command) throws ErmActuallyException {
        int taskIndex = Parser.parseTaskIndex(command);
        boolean wasAlreadyMarked = tasks.get(taskIndex).isDone();
        tasks.mark(taskIndex);
        assert tasks.get(taskIndex).isDone() : "A marked task must be complete";
        try {
            saveTasks();
        } catch (ErmActuallyException e) {
            if (!wasAlreadyMarked) {
                tasks.unmark(taskIndex);
            }
            throw e;
        }
        return ui.formatTaskMarked(tasks.get(taskIndex));
    }

    /** Unmarks and saves the requested task, restoring its original state if saving fails. */
    private String unmarkTask(String command) throws ErmActuallyException {
        int taskIndex = Parser.parseTaskIndex(command);
        boolean wasAlreadyUnmarked = !tasks.get(taskIndex).isDone();
        tasks.unmark(taskIndex);
        assert !tasks.get(taskIndex).isDone() : "An unmarked task must be incomplete";
        try {
            saveTasks();
        } catch (ErmActuallyException e) {
            if (!wasAlreadyUnmarked) {
                tasks.mark(taskIndex);
            }
            throw e;
        }
        return ui.formatTaskUnmarked(tasks.get(taskIndex));
    }

    /** Deletes and saves the requested task, restoring it at its original position if saving fails. */
    private String deleteTask(String command) throws ErmActuallyException {
        int taskIndex = Parser.parseTaskIndex(command);
        Task removedTask = tasks.delete(taskIndex);
        try {
            saveTasks();
        } catch (ErmActuallyException e) {
            tasks.add(taskIndex, removedTask);
            throw e;
        }
        return ui.formatTaskDeleted(removedTask, tasks.size());
    }

    /** Sorts and saves the task list, leaving the in-memory order unchanged if saving fails. */
    private String sortTasks(String command) throws ErmActuallyException {
        SortDirection direction = Parser.parseSortDirection(command);
        if (tasks.isEmpty()) {
            return ui.formatNoTasksToSort();
        }
        TaskList sortedTasks = tasks.createChronologicallySorted(direction);
        storage.save(sortedTasks);
        tasks = sortedTasks;
        return ui.formatSortedTaskList(tasks, direction);
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
