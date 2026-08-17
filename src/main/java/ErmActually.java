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
        String[] tasks = new String[100];
        int taskCount = 0;
        while (true) {
            String command = scanner.nextLine();

            System.out.println(line);
            if (command.equals("bye")) {
                System.out.println(farewell);
                System.out.println(line);
                break;
            } else if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else if (command.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;

                tasks[taskIndex] = "[X]" + tasks[taskIndex].substring(3);

                System.out.println("oh! good job you've actually finished this task:");
                System.out.println(" " + tasks[taskIndex]);
            } else if (command.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(command.substring(7));
                int taskIndex = taskNumber - 1;

                tasks[taskIndex] = "[ ]" + tasks[taskIndex].substring(3);

                System.out.println("oh? okay then I'll unmark it for you:");
                System.out.println("  " + tasks[taskIndex]);
            } else {
                tasks[taskCount] = "[ ] " + command;
                taskCount++;
                System.out.println("added: " + command);
            }

            System.out.println(line);
        }
    }
}
