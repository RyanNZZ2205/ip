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

    /**
     * Creates the application with a console interface and file-backed storage.
     *
     * @param filePath Path of the file used to store tasks.
     */
    public ErmActually(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(Path.of(filePath));
        this.tasks = new TaskList();
    }

    /** Runs the application until input ends or the user enters {@code bye}. */
    public void run() {
        ui.showWelcome();
        loadTasks();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            try {
                switch (Parser.getCommandType(command)) {
                case BYE:
                    ui.showFarewell();
                    return;
                case LIST:
                    ui.showTaskList(tasks);
                    break;
                case ON:
                    ui.showTasksOnDate(tasks, Parser.parseDate(command));
                    break;
                case MARK:
                    int markIndex = Parser.parseTaskIndex(command);
                    tasks.mark(markIndex);
                    saveTasks();
                    ui.showTaskMarked(tasks.get(markIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = Parser.parseTaskIndex(command);
                    tasks.unmark(unmarkIndex);
                    saveTasks();
                    ui.showTaskUnmarked(tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    Task removedTask = tasks.delete(Parser.parseTaskIndex(command));
                    saveTasks();
                    ui.showTaskDeleted(removedTask, tasks.size());
                    break;
                case TODO:
                    addTask(Parser.parseTodo(command));
                    break;
                case DEADLINE:
                    addTask(Parser.parseDeadline(command));
                    break;
                case EVENT:
                    addTask(Parser.parseEvent(command));
                    break;
                case UNKNOWN:
                    ui.showError("actually.. what are you saying??");
                    break;
                }
            } catch (ErmActuallyException e) {
                ui.showError(e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                ui.showError("That task number does not exist.");
            }
        }
    }

    /** Loads saved tasks, falling back to an empty task list when loading fails. */
    private void loadTasks() {
        try {
            tasks = new TaskList(storage.load());
        } catch (ErmActuallyException e) {
            ui.showError(e.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Adds and saves a parsed task, then displays its confirmation.
     *
     * @param task Parsed task to add.
     */
    private void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        ui.showTaskAdded(task, tasks.size());
    }

    /** Saves the task list and reports a failure without interrupting the command response. */
    private void saveTasks() {
        try {
            storage.save(tasks);
        } catch (ErmActuallyException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Starts ErmActually using the default task data file.
     *
     * @param args Command-line arguments, which this application does not use.
     */
    public static void main(String[] args) {
        new ErmActually("data/ErmActually.txt").run();
    }
}
