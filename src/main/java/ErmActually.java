/**
 * Starts ErmActually, which greets the user and then exits.
 */
public class ErmActually {
    public static void main(String[] args) {
        String banner = "+----------------+\n"
                + "|  Erm Actually  |\n"
                + "+----------------+";

        String line = "____________________________________________________________";

        String welcome = "Greetings! I'm Erm Actually.\n"
                + "How can I actually help you today?";

        String farewell = "Farewell! Hope you stop by again!";

        System.out.println(line);
        System.out.println(banner);
        System.out.println(welcome);
        System.out.println(line);
        System.out.println(farewell);
        System.out.println(line);
    }
}
