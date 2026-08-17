import java.util.Scanner;

/**
 * Starts ErmActually, which greets the user, echoes commands, and exits on {@code bye}.
 */
public class ErmActually {
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
        Task[] tasks = new Task[100];
        int taskCount = 0;
        while (true) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println(line);
                System.out.println(farewell);
                System.out.println(line);
                break;
            } else if (command.equals("list")) {
                System.out.println(line);
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
                System.out.println(line);

            } else if (command.startsWith("mark ")) {
                System.out.println(line);
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;

                tasks[taskIndex].markAsDone();

                System.out.println("oh! good job you've actually finished this task:");
                System.out.println(" " + tasks[taskIndex]);
                System.out.println(line);

            } else if (command.startsWith("unmark ")) {
                System.out.println(line);
                int taskNumber = Integer.parseInt(command.substring(7));
                int taskIndex = taskNumber - 1;

                tasks[taskIndex].unmarkAsDone();

                System.out.println("oh? okay then I'll unmark it for you:");
                System.out.println("  " + tasks[taskIndex]);
                System.out.println(line);

            } else if (command.equals("todo") || command.startsWith("todo ")) {
                String description = command.substring(4);
                try {
                    tasks[taskCount] = new Todo(description);
                    taskCount++;
                    showTaskAdded(tasks[taskCount - 1], taskCount);
                } catch (ErmActuallyException e) {
                    showError(e.getMessage());
                }

            } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                String[] parts = command.substring(8).split(" /by", -1);

                if (parts.length < 2) {
                    showError("Please add a deadline using /by.");
                } else {
                    try {
                        tasks[taskCount] = new Deadline(parts[0], parts[1]);
                        taskCount++;
                        showTaskAdded(tasks[taskCount - 1], taskCount);
                    } catch (ErmActuallyException e) {
                        showError(e.getMessage());
                    }
                }

            } else if (command.equals("event") || command.startsWith("event ")) {
                String[] fromParts = command.substring(5).split(" /from", -1);

                if (fromParts.length < 2) {
                    showError("Please add an event start time using /from.");
                } else {
                    String[] toParts = fromParts[1].split(" /to", -1);

                    if (toParts.length < 2) {
                        showError("Please add an event end time using /to.");
                    } else {
                        try {
                            tasks[taskCount] = new Event(fromParts[0], toParts[0], toParts[1]);
                            taskCount++;
                            showTaskAdded(tasks[taskCount - 1], taskCount);
                        } catch (ErmActuallyException e) {
                            showError(e.getMessage());
                        }
                    }
                }
            } else {
                System.out.println(line);
                showError("actually.. what are you saying??");
                System.out.println(line);

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

    private static void showError(String message) {
        String line = "____________________________________________________________";
        System.out.println(line);
        System.out.println(" uhohhhh... " + message);
        System.out.println(line);
    }
}
