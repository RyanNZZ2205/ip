import java.util.Scanner;
import java.util.ArrayList;

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

        //Stores Task objects in a dynamically sized list
        ArrayList<Task> tasks = new ArrayList<>();

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

                int taskNumber = Integer.parseInt(command.substring(5));
                int taskIndex = taskNumber - 1;

                tasks.get(taskIndex).markAsDone();

                System.out.println(line);
                System.out.println("oh! good job you've actually finished this task:");
                System.out.println(" " + tasks.get(taskIndex));
                System.out.println(line);

            }

            //unmark command
            else if (command.startsWith("unmark ")) {

                int taskNumber = Integer.parseInt(command.substring(7));
                int taskIndex = taskNumber - 1;

                tasks.get(taskIndex).unmarkAsDone();

                System.out.println(line);
                System.out.println("oh? okay then I'll unmark it for you:");
                System.out.println("  " + tasks.get(taskIndex));
                System.out.println(line);

            }

            //delete command
            else if (command.startsWith("delete ")) {
                try {
                    int taskNumber = Integer.parseInt(command.substring(7).trim());
                    int index = taskNumber - 1;

                    Task removedTask = tasks.remove(index);

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

    private static void showError(String message) {
        String line = "____________________________________________________________";
        System.out.println(line);
        System.out.println(" uhohhhh... " + message);
        System.out.println(line);
    }
}
