import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
     * @param args command-line arguments, which this application does not use
     */
    public static void main(String[] args) {
        String banner = "+----------------+\n"
                + "|  Erm Actually  |\n"
                + "+----------------+";

        String line = "____________________________________________________________";

        String welcome = "Greetings! I'm Erm Actually.\n"
                + "What can I actually do for you?";

        String farewell = "Farewell! Hope you stop by again soon!";

        System.out.println(line);
        System.out.println(banner);
        System.out.println(welcome);
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);

        ArrayList<Task> tasks = loadTasks();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine().trim();

            //Bye Command
            if (command.equals("bye")) {
                System.out.println(line);
                System.out.println(farewell);
                System.out.println(line);
                break;
            }

            //List Command
            else if (command.equals("list")) {

                System.out.println(line);
                System.out.println(" Here are the tasks in your list:");

                if (tasks.isEmpty()) {
                    System.out.println("Woohoo! No tasks found!");
                } else {
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + ". " + tasks.get(i));
                    }
                }

                System.out.println(line);
            }

            //mark command
            else if (command.equals("mark") || command.startsWith("mark ")) {
                try {
                    int taskIndex = parseTaskIndex(command, "mark");

                    tasks.get(taskIndex).markAsDone();
                    saveTasks(tasks);

                    System.out.println(line);
                    System.out.println("oh! good job you've actually finished this task:");
                    System.out.println(" " + tasks.get(taskIndex));
                    System.out.println(line);
                } catch (ErmActuallyException e) {
                    showError(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    showError("That task number does not exist.");
                }
            }


            //unmark command
            else if (command.equals("unmark") || command.startsWith("unmark ")) {
                try {
                    int taskIndex = parseTaskIndex(command, "unmark");

                    tasks.get(taskIndex).unmarkAsDone();
                    saveTasks(tasks);

                    System.out.println(line);
                    System.out.println("oh? okay then I'll unmark it for you:");
                    System.out.println("  " + tasks.get(taskIndex));
                    System.out.println(line);
                } catch (ErmActuallyException e) {
                    showError(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    showError("That task number does not exist.");
                }
            }

            //delete command
            else if (command.equals("delete") || command.startsWith("delete ")) {
                try {
                    int index = parseTaskIndex(command, "delete");

                    Task removedTask = tasks.remove(index);
                    saveTasks(tasks);

                    System.out.println(line);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(line);

                } catch (ErmActuallyException e) {
                    showError(e.getMessage());
                } catch (IndexOutOfBoundsException e) {
                    showError("That task number does not exist.");
                }
            }

            //todo command
            else if (command.equals("todo") || command.startsWith("todo ")) {
                try {
                    String description = command.substring(4).trim();

                    if (description.isEmpty()) {
                        throw new ErmActuallyException("Please add a description for todo!");
                    }

                    Task toDoTask = new Todo(description);

                    tasks.add(toDoTask);
                    saveTasks(tasks);

                    showTaskAdded(toDoTask, tasks.size());
                } catch (ErmActuallyException e) {
                    showError(e.getMessage());
                }

            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
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
                    saveTasks(tasks);

                    showTaskAdded(deadlineTask, tasks.size());
                } catch (ErmActuallyException e) {
                    showError(e.getMessage());
                }
            }

            //event commmand
            else if (command.equals("event") || command.startsWith("event ")) {

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
                    saveTasks(tasks);

                    showTaskAdded(task, tasks.size());

                } catch (ErmActuallyException e) {
                    showError(e.getMessage());
                }
            }

            //unknown commands
            else {
                showError("actually.. what are you saying??");
            }

        }
    }

    private static void showTaskAdded(Task task, int taskCount) {
        System.out.println("____________________________________________________________");
        System.out.println(" Alright! I've added this new task:");
        System.out.println("   " + task);
        System.out.println(" Wow! you have " + taskCount + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    /**
     * Converts the task number after a command into a zero-based list index.
     *
     * @param command complete user command
     * @param commandName command keyword at the beginning of the command
     * @return the zero-based task index
     * @throws ErmActuallyException if the supplied task number is missing, invalid, or less than one
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
     * @param tasks tasks to save
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        ArrayList<String> savedTasks = new ArrayList<>();
        for (Task task : tasks) {
            savedTasks.add(formatTaskForSaving(task));
        }

        try {
            Files.createDirectories(SAVE_FILE.getParent());
            Files.write(SAVE_FILE, savedTasks);
        } catch (IOException | SecurityException e) {
            showError("I couldn't save your tasks.");
        }
    }

    /**
     * Loads tasks saved by {@link #saveTasks(ArrayList)}. A missing save file means the task list starts empty.
     *
     * @return the saved tasks, or an empty list when no save file exists
     */
    private static ArrayList<Task> loadTasks() {
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
            showError("I couldn't load your tasks.");
            return new ArrayList<>();
        }
    }

    /**
     * Recreates one task from the text format used in the save file.
     *
     * @param savedTask one line from the save file
     * @return the recreated task with its saved completion status
     * @throws ErmActuallyException if the saved task is not valid
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
     * @param parts fields in a version-two saved task
     * @return the recreated task
     * @throws ErmActuallyException if the saved task is invalid
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
     * @param parts fields in a legacy saved task
     * @return the recreated task
     * @throws ErmActuallyException if the saved task is invalid
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
     * @param type saved task type
     * @param status saved completion status
     * @param details saved task details
     * @return the recreated task
     * @throws ErmActuallyException if the saved task is invalid
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
     * @param task task to format
     * @return a text line containing the task's type, completion status, and details
     */
    private static String formatTaskForSaving(Task task) {
        String isDone = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return joinSavedFields("D", isDone, deadline.description, deadline.by);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return joinSavedFields("E", isDone, event.description, event.from, event.to);
        }
        return joinSavedFields("T", isDone, task.description);
    }

    /**
     * Encodes task details before joining them into one versioned save-file line.
     *
     * @param type task type
     * @param status completion status
     * @param details task details to encode
     * @return a safely formatted save-file line
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

    private static void showError(String message) {
        String line = "____________________________________________________________";
        System.out.println(line);
        System.out.println(" uhohhhh... " + message);
        System.out.println(line);
    }
}
