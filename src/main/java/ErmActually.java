import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Starts ErmActually, loading saved tasks before greeting the user and processing commands.
 */
public class ErmActually {
    private static final Path SAVE_FILE = Path.of("data", "ErmActually.txt");
    private static final String SAVE_FORMAT_VERSION = "V2";
    private static final String FIELD_SEPARATOR = " | ";

    /**
     * Runs the command loop until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which this application does not use.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        ArrayList<Task> tasks = loadTasks(ui);

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
                    saveTasks(tasks, ui);
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
                    saveTasks(tasks, ui);
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
                    saveTasks(tasks, ui);
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
                    saveTasks(tasks, ui);

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
                    saveTasks(tasks, ui);

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
                    saveTasks(tasks, ui);

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
     * Saves the current tasks in a versioned text format that safely preserves special characters.
     *
     * @param tasks Tasks to save.
     */
    private static void saveTasks(ArrayList<Task> tasks, Ui ui) {
        ArrayList<String> savedTasks = new ArrayList<>();
        for (Task task : tasks) {
            savedTasks.add(formatTaskForSaving(task));
        }

        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.write(SAVE_FILE, savedTasks);
        } catch (IOException | SecurityException e) {
            ui.showError("I couldn't save your tasks.");
        }
    }

    /**
     * Loads tasks saved by {@link #saveTasks(ArrayList, Ui)}.
     * A missing save file means the task list starts empty.
     *
     * @return The saved tasks, or an empty list when no save file exists.
     */
    private static ArrayList<Task> loadTasks(Ui ui) {
        try {
            if (!Files.exists(SAVE_FILE)) {
                return new ArrayList<>();
            }

            ArrayList<Task> tasks = new ArrayList<>();
            for (String savedTask : Files.readAllLines(SAVE_FILE)) {
                tasks.add(createTaskFromSavedLine(savedTask));
            }
            return tasks;
        } catch (IOException | SecurityException | ErmActuallyException e) {
            ui.showError("I couldn't load your tasks.");
            return new ArrayList<>();
        }
    }

    /**
     * Recreates one task from the text format used in the save file.
     *
     * @param savedTask One line from the save file.
     * @return The recreated task with its saved completion status.
     * @throws ErmActuallyException If the saved task is not valid.
     */
    private static Task createTaskFromSavedLine(String savedTask) throws ErmActuallyException {
        String[] parts = savedTask.split(" \\| ", -1);
        if (parts.length > 0 && parts[0].equals(SAVE_FORMAT_VERSION)) {
            return createVersionTwoTask(parts);
        }
        return createLegacyTask(parts);
    }

    /**
     * Recreates a task written by the current version of the application.
     *
     * @param parts Fields in a version-two saved task.
     * @return The recreated task.
     * @throws ErmActuallyException If the saved task is invalid.
     */
    private static Task createVersionTwoTask(String[] parts) throws ErmActuallyException {
        if (parts.length < 4) {
            throw new ErmActuallyException("Invalid saved task.");
        }
        String[] details = new String[parts.length - 3];
        try {
            for (int i = 3; i < parts.length; i++) {
                details[i - 3] = new String(Base64.getDecoder().decode(parts[i]), StandardCharsets.UTF_8);
            }
        } catch (IllegalArgumentException e) {
            throw new ErmActuallyException("Invalid saved task.");
        }
        return createTask(parts[1], parts[2], details);
    }

    /**
     * Recreates a task written by the earlier plain-text save format.
     *
     * @param parts Fields in a legacy saved task.
     * @return The recreated task.
     * @throws ErmActuallyException If the saved task is invalid.
     */
    private static Task createLegacyTask(String[] parts) throws ErmActuallyException {
        if (parts.length < 3) {
            throw new ErmActuallyException("Invalid saved task.");
        }
        String[] details = new String[parts.length - 2];
        System.arraycopy(parts, 2, details, 0, details.length);
        return createTask(parts[0], parts[1], details);
    }

    /**
     * Creates a task after validating its saved type, status, and required details.
     *
     * @param type Saved task type.
     * @param status Saved completion status.
     * @param details Saved task details.
     * @return The recreated task.
     * @throws ErmActuallyException If the saved task is invalid.
     */
    private static Task createTask(String type, String status, String[] details) throws ErmActuallyException {
        if (!status.equals("0") && !status.equals("1")) {
            throw new ErmActuallyException("Invalid saved task.");
        }
        Task task;
        if (type.equals("T") && details.length == 1) {
            task = new Todo(details[0]);
        } else if (type.equals("D") && details.length == 2) {
            task = new Deadline(details[0], details[1]);
        } else if (type.equals("E") && details.length == 3) {
            task = new Event(details[0], details[1], details[2]);
        } else {
            throw new ErmActuallyException("Invalid saved task.");
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a task to one line of the save-file format.
     *
     * @param task Task to format.
     * @return A text line containing the task's type, completion status, and details.
     */
    private static String formatTaskForSaving(Task task) {
        String isDone = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return joinSavedFields("D", isDone, deadline.description, deadline.toStorageString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return joinSavedFields("E", isDone, event.description,
                    event.getFromStorageString(), event.getToStorageString());
        }
        return joinSavedFields("T", isDone, task.description);
    }

    /**
     * Encodes task details before joining them into one versioned save-file line.
     *
     * @param type Task type.
     * @param status Completion status.
     * @param details Task details to encode.
     * @return A safely formatted save-file line.
     */
    private static String joinSavedFields(String type, String status, String... details) {
        ArrayList<String> fields = new ArrayList<>();
        fields.add(SAVE_FORMAT_VERSION);
        fields.add(type);
        fields.add(status);
        for (String detail : details) {
            fields.add(Base64.getEncoder().encodeToString(detail.getBytes(StandardCharsets.UTF_8)));
        }
        return String.join(FIELD_SEPARATOR, fields);
    }

}
