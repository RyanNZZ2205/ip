import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles all console input and output for ErmActually.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;

    /** Creates a console UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays the chatbot banner and greeting. */
    public void showWelcome() {
        String banner = "+----------------+\n"
                + "|  Erm Actually  |\n"
                + "+----------------+";
        String welcome = "Greetings! I'm Erm Actually.\n"
                + "What can I actually do for you?";
        showMessage(banner + "\n" + welcome);
    }

    /** @return Whether another command is available from standard input. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** @return The next command, with surrounding whitespace removed. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays the farewell message. */
    public void showFarewell() {
        showMessage("Farewell! Hope you stop by again soon!");
    }

    /** Displays every task with its one-based list number. */
    public void showTaskList(TaskList tasks) {
        System.out.println(LINE);
        System.out.println(" Here are the tasks in your list:");
        if (tasks.isEmpty()) {
            System.out.println("Woohoo! No tasks found!");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i));
            }
        }
        System.out.println(LINE);
    }

    /** Displays deadlines and events occurring on a requested date. */
    public void showTasksOnDate(TaskList tasks, LocalDate requestedDate) {
        System.out.println(LINE);
        System.out.println(" Here are the tasks occurring on " + requestedDate + ":");
        ArrayList<Integer> matchingIndexes = tasks.findIndexesOn(requestedDate);
        for (int index : matchingIndexes) {
            System.out.println(" " + (index + 1) + ". " + tasks.get(index));
        }
        if (matchingIndexes.isEmpty()) {
            System.out.println(" No deadlines or events found.");
        }
        System.out.println(LINE);
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Alright! I've added this new task:");
        System.out.println("   " + task);
        System.out.println(" Wow! you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /** Displays confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println(LINE);
        System.out.println("oh! good job you've actually finished this task:");
        System.out.println(" " + task);
        System.out.println(LINE);
    }

    /** Displays confirmation that a task was unmarked. */
    public void showTaskUnmarked(Task task) {
        System.out.println(LINE);
        System.out.println("oh? okay then I'll unmark it for you:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    /** Displays confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /** Displays an error in the chatbot's output format. */
    public void showError(String message) {
        showMessage(" uhohhhh... " + message);
    }

    /** Displays a message between separator lines. */
    private void showMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
