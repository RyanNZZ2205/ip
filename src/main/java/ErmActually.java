import java.nio.file.Path;

/**
 * Starts ErmActually, loading saved tasks before greeting the user and processing commands.
 */
public class ErmActually {
    /**
     * Runs the command loop until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which this application does not use.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(Path.of("data", "ErmActually.txt"));
        ui.showWelcome();
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (ErmActuallyException e) {
            ui.showError(e.getMessage());
            tasks = new TaskList();
        }

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
                    saveTasks(storage, tasks, ui);
                    ui.showTaskMarked(tasks.get(markIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = Parser.parseTaskIndex(command);
                    tasks.unmark(unmarkIndex);
                    saveTasks(storage, tasks, ui);
                    ui.showTaskUnmarked(tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    Task removedTask = tasks.delete(Parser.parseTaskIndex(command));
                    saveTasks(storage, tasks, ui);
                    ui.showTaskDeleted(removedTask, tasks.size());
                    break;
                case TODO:
                    addTask(Parser.parseTodo(command), tasks, storage, ui);
                    break;
                case DEADLINE:
                    addTask(Parser.parseDeadline(command), tasks, storage, ui);
                    break;
                case EVENT:
                    addTask(Parser.parseEvent(command), tasks, storage, ui);
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

    /**
     * Adds and saves a parsed task, then displays its confirmation.
     *
     * @param task Parsed task to add.
     * @param tasks Current task list.
     * @param storage Storage used to save the updated list.
     * @param ui Console interface used to show the result.
     */
    private static void addTask(Task task, TaskList tasks, Storage storage, Ui ui) {
        tasks.add(task);
        saveTasks(storage, tasks, ui);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Saves the task list and reports a failure without interrupting the command response.
     *
     * @param storage Storage used to write the tasks.
     * @param tasks Current tasks.
     * @param ui Console interface used to report a failure.
     */
    private static void saveTasks(Storage storage, TaskList tasks, Ui ui) {
        try {
            storage.save(tasks);
        } catch (ErmActuallyException e) {
            ui.showError(e.getMessage());
        }
    }

}
