import java.util.Scanner;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Starts ErmActually, loading saved tasks before greeting the user and processing commands.
 */
public class ErmActually {
    private static final Path SAVE_FILE = Path.of("data", "ErmActually.txt");
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

        while (true) {
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
            else if (command.startsWith("mark ")) {
                try {
                    int taskNumber = Integer.parseInt(command.substring(5));
                    int taskIndex = taskNumber - 1;

                    tasks.get(taskIndex).markAsDone();
                    saveTasks(tasks);

                    System.out.println(line);
                    System.out.println("oh! good job you've actually finished this task:");
                    System.out.println(" " + tasks.get(taskIndex));
                    System.out.println(line);
                } catch (NumberFormatException e) {
                    showError("Please provide a valid task number.");
                } catch (IndexOutOfBoundsException e) {
                    showError("That task number does not exist.");
                }
            }


            //unmark command
            else if (command.startsWith("unmark ")) {
                try {
                    int taskNumber = Integer.parseInt(command.substring(7));
                    int taskIndex = taskNumber - 1;

                    tasks.get(taskIndex).unmarkAsDone();
                    saveTasks(tasks);

                    System.out.println(line);
                    System.out.println("oh? okay then I'll unmark it for you:");
                    System.out.println("  " + tasks.get(taskIndex));
                    System.out.println(line);
                } catch (NumberFormatException e) {
                    showError("Please provide a valid task number.");
                } catch (IndexOutOfBoundsException e) {
                    showError("That task number does not exist.");
                }
            }

            //delete command
            else if (command.startsWith("delete ")) {
                try {
                    int taskNumber = Integer.parseInt(command.substring(7).trim());
                    int index = taskNumber - 1;

                    Task removedTask = tasks.remove(index);
                    saveTasks(tasks);

                    System.out.println(line);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(line);

                } catch (NumberFormatException e) {
                    showError("Please provide a valid task number.");
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
     * Saves the current tasks in a simple text format for a future startup loader.
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
        } catch (IOException e) {
            showError("I couldn't save your tasks.");
        }
    }

    /**
     * Loads tasks saved by {@link #saveTasks(ArrayList)}. A missing save file means the task list starts empty.
     *
     * @return the saved tasks, or an empty list when no save file exists
     */
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(SAVE_FILE)) {
            return tasks;
        }

        try {
            for (String savedTask : Files.readAllLines(SAVE_FILE)) {
                tasks.add(createTaskFromSavedLine(savedTask));
            }
        } catch (IOException | ErmActuallyException e) {
            showError("I couldn't load your tasks.");
        }
        return tasks;
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
        Task task;
        if (parts[0].equals("T")) {
            task = new Todo(parts[2]);
        } else if (parts[0].equals("D")) {
            task = new Deadline(parts[2], parts[3]);
        } else {
            task = new Event(parts[2], parts[3], parts[4]);
        }

        if (parts[1].equals("1")) {
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
            return "D | " + isDone + " | " + deadline.description + " | " + deadline.by;
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + isDone + " | " + event.description + " | " + event.from + " | " + event.to;
        }
        return "T | " + isDone + " | " + task.description;
    }

    private static void showError(String message) {
        String line = "____________________________________________________________";
        System.out.println(line);
        System.out.println(" uhohhhh... " + message);
        System.out.println(line);
    }
}
