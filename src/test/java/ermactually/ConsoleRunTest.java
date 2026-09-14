package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the console loop with controlled standard input and output. */
public class ConsoleRunTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void run_commandsThenInputEnds_printsCompleteConversation() {
        String input = "todo read book\nlist\n";

        String output = runWithConsoleInput(input);

        assertEquals("____________________________________________________________\n"
                + "+----------------+\n"
                + "|  Erm Actually  |\n"
                + "+----------------+\n"
                + "Greetings! I'm Erm Actually.\n"
                + "What can I actually do for you?\n"
                + "____________________________________________________________\n"
                + "____________________________________________________________\n"
                + " Alright! I've added this new task:\n"
                + "   [T][ ] read book\n"
                + " Wow! you have 1 tasks in the list.\n"
                + "____________________________________________________________\n"
                + "____________________________________________________________\n"
                + " Here are the tasks in your list:\n"
                + " 1. [T][ ] read book\n"
                + "____________________________________________________________\n", output);
    }

    @Test
    public void run_byeCommand_printsFarewellAndStopsBeforeLaterInput() {
        String output = runWithConsoleInput("bye\ntodo ignored\n");

        assertEquals("____________________________________________________________\n"
                + "+----------------+\n"
                + "|  Erm Actually  |\n"
                + "+----------------+\n"
                + "Greetings! I'm Erm Actually.\n"
                + "What can I actually do for you?\n"
                + "____________________________________________________________\n"
                + "____________________________________________________________\n"
                + "Farewell! Hope you stop by again soon!\n"
                + "____________________________________________________________\n", output);
    }

    /** Runs a fresh application against controlled process streams and restores them afterward. */
    private String runWithConsoleInput(String input) {
        java.io.InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
            Path dataFile = temporaryDirectory.resolve("tasks-" + System.nanoTime() + ".txt");
            new ErmActually(dataFile.toString()).run();
            return capturedOutput.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
    }
}
