package ermactually;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import ermactually.task.Task;
import ermactually.task.TaskList;

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

    /** Returns whether another command is available from standard input. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Returns the next command with surrounding whitespace removed. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays a complete response between separator lines. */
    public void showResponse(String response) {
        showMessage(response);
    }

    /** Formats every task with its one-based list number. */
    String formatTaskList(TaskList tasks) {
        StringBuilder response = new StringBuilder(" Here are the tasks in your list:");
        if (tasks.isEmpty()) {
            response.append("\nWoohoo! No tasks found!");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                response.append("\n ")
                        .append(i + 1)
                        .append(". ")
                        .append(tasks.get(i));
            }
        }
        return response.toString();
    }

    /** Formats tasks whose descriptions contain the requested keyword. */
    String formatMatchingTasks(TaskList tasks, String keyword) {
        StringBuilder response = new StringBuilder(" Here are the matching tasks in your list:");
        ArrayList<Integer> matchingIndexes = tasks.findIndexes(keyword);
        for (int index : matchingIndexes) {
            response.append("\n ")
                    .append(index + 1)
                    .append(". ")
                    .append(tasks.get(index));
        }
        if (matchingIndexes.isEmpty()) {
            response.append("\n No matching tasks found.");
        }
        return response.toString();
    }

    /** Formats deadlines and events occurring on a requested date. */
    String formatTasksOnDate(TaskList tasks, LocalDate requestedDate) {
        StringBuilder response = new StringBuilder(" Here are the tasks occurring on ")
                .append(requestedDate)
                .append(":");
        ArrayList<Integer> matchingIndexes = tasks.findIndexesOn(requestedDate);
        for (int index : matchingIndexes) {
            response.append("\n ")
                    .append(index + 1)
                    .append(". ")
                    .append(tasks.get(index));
        }
        if (matchingIndexes.isEmpty()) {
            response.append("\n No deadlines or events found.");
        }
        return response.toString();
    }

    /** Formats confirmation that a task was added. */
    String formatTaskAdded(Task task, int taskCount) {
        return " Alright! I've added this new task:\n"
                + "   " + task + "\n"
                + " Wow! you have " + taskCount + " tasks in the list.";
    }

    /** Formats confirmation that a task was marked as done. */
    String formatTaskMarked(Task task) {
        return "oh! good job you've actually finished this task:\n " + task;
    }

    /** Formats confirmation that a task was unmarked. */
    String formatTaskUnmarked(Task task) {
        return "oh? okay then I'll unmark it for you:\n  " + task;
    }

    /** Formats confirmation that a task was deleted. */
    String formatTaskDeleted(Task task, int taskCount) {
        return " Noted. I've removed this task:\n"
                + "   " + task + "\n"
                + " Now you have " + taskCount + " tasks in the list.";
    }

    /** Formats an error in the chatbot's output format. */
    String formatError(String message) {
        return " uhohhhh... " + message;
    }

    /** Formats the farewell message. */
    String formatFarewell() {
        return "Farewell! Hope you stop by again soon!";
    }

    /** Displays a message between separator lines. */
    private void showMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
