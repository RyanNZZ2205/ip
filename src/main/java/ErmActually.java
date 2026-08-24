import java.nio.file.Path;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (ErmActuallyException e) {
            ui.showError(e.getMessage());
            tasks = new ArrayList<>();
        }

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            if (command.equals("bye")) { //bye command
                ui.showFarewell();
                break;
            } else if (command.equals("list")) { //list command
                ui.showTaskList(tasks);
            } else if (command.equals("on") || command.startsWith("on ")) { //date search command
                try {
                    String dateText = command.substring(2).trim();
                    if (dateText.isEmpty()) {
                        throw new ErmActuallyException("Please provide a date in yyyy-MM-dd format.");
                    }
                    LocalDate requestedDate = LocalDate.parse(dateText);
                    ui.showTasksOnDate(tasks, requestedDate);
                } catch (DateTimeParseException e) {
                    ui.showError("Please provide a valid date in yyyy-MM-dd format.");
                } catch (ErmActuallyException e) {
                    ui.showError(e.getMessage());
                }
            } else if (command.equals("mark") || command.startsWith("mark ")) { //mark command
                try {
                    int taskIndex = parseTaskIndex(command, "mark");

                    tasks.get(taskIndex).markAsDone();
                    saveTasks(storage, tasks, ui);
                    ui.showTaskMarked(tasks.get(taskIndex));
                } catch (ErmActuallyException e) {
                    ui.showError(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    ui.showError("That task number does not exist.");
                }
            } else if (command.equals("unmark") || command.startsWith("unmark ")) { //unmark command
                try {
                    int taskIndex = parseTaskIndex(command, "unmark");

                    tasks.get(taskIndex).unmarkAsDone();
                    saveTasks(storage, tasks, ui);
                    ui.showTaskUnmarked(tasks.get(taskIndex));
                } catch (ErmActuallyException e) {
                    ui.showError(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    ui.showError("That task number does not exist.");
                }
            } else if (command.equals("delete") || command.startsWith("delete ")) { //delete command
                try {
                    int index = parseTaskIndex(command, "delete");

                    Task removedTask = tasks.remove(index);
                    saveTasks(storage, tasks, ui);
                    ui.showTaskDeleted(removedTask, tasks.size());

                } catch (ErmActuallyException e) {
                    ui.showError(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    ui.showError("That task number does not exist.");
                }
            } else if (command.equals("todo") || command.startsWith("todo ")) { //todo command
                try {
                    String description = command.substring(4).trim();

                    if (description.isEmpty()) {
                        throw new ErmActuallyException("Please add a description for todo!");
                    }

                    Task toDoTask = new Todo(description);

                    tasks.add(toDoTask);
                    saveTasks(storage, tasks, ui);

                    ui.showTaskAdded(toDoTask, tasks.size());
                } catch (ErmActuallyException e) {
                    ui.showError(e.getMessage());
                }

            } else if (command.equals("deadline") || command.startsWith("deadline ")) { //deadline command
                try {
                    String details = command.substring(8).trim();

                    String[] parts = details.split(" /by", 2);

                    if (parts.length != 2) {
                        throw new ErmActuallyException("Please /by for the deadline.");
                    }

                    String description = parts[0].trim();
                    String by = parts[1].trim();

                    if (description.isEmpty()) {
                        throw new ErmActuallyException("Please add a description for deadline!");
                    }

                    if (by.isEmpty()) {
                        throw new ErmActuallyException("Please add a deadline using /by.");
                    }

                    Task deadlineTask = new Deadline(description, by);

                    tasks.add(deadlineTask);
                    saveTasks(storage, tasks, ui);

                    ui.showTaskAdded(deadlineTask, tasks.size());
                } catch (ErmActuallyException e) {
                    ui.showError(e.getMessage());
                }
            } else if (command.equals("event") || command.startsWith("event ")) { //event command
                try {
                    String details = command.substring(5).trim();

                    String[] fromSplit = details.split("/from", 2);

                    if (fromSplit.length != 2) {
                        throw new ErmActuallyException("Please add a /from for the event!");
                    }

                    String description = fromSplit[0].trim();

                    String[] toSplit = fromSplit[1].split("/to", 2);

                    if (toSplit.length != 2) {
                        throw new ErmActuallyException("Please add a /to for the event!");
                    }

                    String from = toSplit[0].trim();
                    String to = toSplit[1].trim();

                    if (description.isEmpty()) {
                        throw new ErmActuallyException("Please add a description for this event!");
                    }

                    if (from.isEmpty() || to.isEmpty()) {
                        throw new ErmActuallyException("Event time details cannot be empty.");
                    }

                    Task task = new Event(description, from, to);

                    tasks.add(task);
                    saveTasks(storage, tasks, ui);

                    ui.showTaskAdded(task, tasks.size());

                } catch (ErmActuallyException e) {
                    ui.showError(e.getMessage());
                }
            } else {
                ui.showError("actually.. what are you saying??");
            }

        }
    }

    /**
     * Converts the task number after a command into a zero-based list index.
     *
     * @param command Complete user command.
     * @param commandName Command keyword at the beginning of the command.
     * @return The zero-based task index.
     * @throws ErmActuallyException If the supplied task number is missing, invalid, or less than one.
     */
    private static int parseTaskIndex(String command, String commandName) throws ErmActuallyException {
        try {
            int taskNumber = Integer.parseInt(command.substring(commandName.length()).trim());
            if (taskNumber < 1) {
                throw new ErmActuallyException("Please provide a valid task number.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new ErmActuallyException("Please provide a valid task number.");
        }
    }

    /**
     * Saves the task list and reports a failure without interrupting the command response.
     *
     * @param storage Storage used to write the tasks.
     * @param tasks Current tasks.
     * @param ui Console interface used to report a failure.
     */
    private static void saveTasks(Storage storage, ArrayList<Task> tasks, Ui ui) {
        try {
            storage.save(tasks);
        } catch (ErmActuallyException e) {
            ui.showError(e.getMessage());
        }
    }

}
