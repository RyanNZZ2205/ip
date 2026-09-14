package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ermactually.task.Deadline;
import ermactually.task.Task;
import ermactually.task.TaskList;
import ermactually.task.Todo;

/** Tests shared command processing used by both the console and GUI. */
public class ErmActuallyTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_sortAscending_persistsOrderAndUpdatesLaterCommands()
            throws IOException {
        ErmActually ermActually = createErmActually();
        ermActually.getResponse("todo buy milk");
        ermActually.getResponse("deadline later /by 2026-09-10");
        ermActually.getResponse("deadline earlier /by 2026-09-09");

        String sortResponse = ermActually.getResponse("\t sort\tasc \t");
        String markResponse = ermActually.getResponse("mark 1");
        ermActually.getResponse("event newly added /from 2026-09-01 /to 2026-09-01");

        assertEquals(" Here are the tasks in your list, sorted in ascending order:\n"
                + " 1. [D][ ] earlier (by: Sep 09 2026)\n"
                + " 2. [D][ ] later (by: Sep 10 2026)\n"
                + " 3. [T][ ] buy milk", sortResponse);
        assertEquals("oh! good job you've actually finished this task:\n"
                + " [D][X] earlier (by: Sep 09 2026)", markResponse);

        ErmActually restartedApplication = createErmActually();
        assertEquals(" Here are the tasks in your list:\n"
                + " 1. [D][X] earlier (by: Sep 09 2026)\n"
                + " 2. [D][ ] later (by: Sep 10 2026)\n"
                + " 3. [T][ ] buy milk\n"
                + " 4. [E][ ] newly added (from: Sep 01 2026 to: Sep 01 2026)",
                restartedApplication.getResponse("list"));
    }

    @Test
    public void getResponse_sortDescending_returnsCompleteSortedList() {
        ErmActually ermActually = createErmActually();
        ermActually.getResponse("deadline earlier /by 2026-09-09 0900");
        ermActually.getResponse("deadline later /by 2026-09-10");

        String response = ermActually.getResponse("sort desc");

        assertEquals(" Here are the tasks in your list, sorted in descending order:\n"
                + " 1. [D][ ] later (by: Sep 10 2026)\n"
                + " 2. [D][ ] earlier (by: Sep 09 2026 9:00 AM)", response);
    }

    @Test
    public void getResponse_sortEmptyList_returnsSpecialSuccessResponse() {
        ErmActually ermActually = createErmActually();

        String response = ermActually.getResponse("sort");

        assertEquals(" No tasks to sort.", response);
        assertEquals(Parser.CommandType.SORT, ermActually.getCommandType());
    }

    @Test
    public void getResponse_invalidSort_returnsUsageErrorAndKeepsOrder() {
        ErmActually ermActually = createErmActually();
        ermActually.getResponse("todo buy milk");
        ermActually.getResponse("deadline submit report /by 2026-09-09");

        String response = ermActually.getResponse("sort asc desc");

        assertEquals(" uhohhhh... Please use: sort [asc|desc].", response);
        assertEquals(Parser.CommandType.UNKNOWN, ermActually.getCommandType());
        assertEquals(" Here are the tasks in your list:\n"
                + " 1. [T][ ] buy milk\n"
                + " 2. [D][ ] submit report (by: Sep 09 2026)",
                ermActually.getResponse("list"));
    }

    @Test
    public void getResponse_sortOneLegacyTask_rewritesFileInVersionTwoFormat()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("ErmActually.txt");
        Files.writeString(dataFile, "T | 0 | buy milk");
        ErmActually ermActually = new ErmActually(dataFile.toString());

        ermActually.getResponse("sort");

        assertTrue(Files.readString(dataFile).startsWith("V2 | T | 0 | "));
    }

    @Test
    public void getResponse_sortSaveFails_keepsOriginalInMemoryOrder()
            throws ErmActuallyException {
        ArrayList<Task> initialTasks = new ArrayList<>(List.of(
                new Todo("buy milk"),
                new Deadline("submit report", "2026-09-09")));
        ErmActually ermActually = new ErmActually(new FailingStorage(initialTasks));

        String response = ermActually.getResponse("sort");

        assertEquals(" uhohhhh... I couldn't save your tasks.", response);
        assertEquals(Parser.CommandType.UNKNOWN, ermActually.getCommandType());
        assertEquals(" Here are the tasks in your list:\n"
                + " 1. [T][ ] buy milk\n"
                + " 2. [D][ ] submit report (by: Sep 09 2026)",
                ermActually.getResponse("list"));
    }

    @Test
    public void getResponse_addTodoThenList_returnsAddedTask() {
        ErmActually ermActually = createErmActually();

        String addResponse = ermActually.getResponse("todo borrow book");
        String listResponse = ermActually.getResponse("list");

        assertEquals(" Alright! I've added this new task:\n"
                + "   [T][ ] borrow book\n"
                + " Wow! you have 1 tasks in the list.", addResponse);
        assertEquals(" Here are the tasks in your list:\n"
                + " 1. [T][ ] borrow book", listResponse);
    }

    @Test
    public void getResponse_allTaskTypesAndSearches_returnsExpectedResponses() {
        ErmActually ermActually = createErmActually();

        String deadlineResponse = ermActually.getResponse("deadline submit /by 2026-09-14");
        String eventResponse = ermActually.getResponse(
                "event conference /from 2026-09-13 /to 2026-09-15");
        String findResponse = ermActually.getResponse("find SUBMIT");
        String onResponse = ermActually.getResponse("on 2026-09-14");

        assertEquals(" Alright! I've added this new task:\n"
                + "   [D][ ] submit (by: Sep 14 2026)\n"
                + " Wow! you have 1 tasks in the list.", deadlineResponse);
        assertEquals(" Alright! I've added this new task:\n"
                + "   [E][ ] conference (from: Sep 13 2026 to: Sep 15 2026)\n"
                + " Wow! you have 2 tasks in the list.", eventResponse);
        assertEquals(" Here are the matching tasks in your list:\n"
                + " 1. [D][ ] submit (by: Sep 14 2026)", findResponse);
        assertEquals(" Here are the tasks occurring on 2026-09-14:\n"
                + " 1. [D][ ] submit (by: Sep 14 2026)\n"
                + " 2. [E][ ] conference (from: Sep 13 2026 to: Sep 15 2026)", onResponse);
    }

    @Test
    public void getResponse_unmarkAndDelete_updatesListAndCommandTypes() {
        ErmActually ermActually = createErmActually();
        ermActually.getResponse("todo first");
        ermActually.getResponse("todo second");
        ermActually.getResponse("mark 2");

        String unmarkResponse = ermActually.getResponse("unmark 2");
        assertEquals(Parser.CommandType.UNMARK, ermActually.getCommandType());
        String deleteResponse = ermActually.getResponse("delete 1");

        assertEquals("oh? okay then I'll unmark it for you:\n  [T][ ] second", unmarkResponse);
        assertEquals(" Noted. I've removed this task:\n"
                + "   [T][ ] first\n"
                + " Now you have 1 tasks in the list.", deleteResponse);
        assertEquals(Parser.CommandType.DELETE, ermActually.getCommandType());
        assertEquals(" Here are the tasks in your list:\n 1. [T][ ] second",
                ermActually.getResponse("list"));
    }

    @Test
    public void getResponse_byeAndUnknownInput_returnsExpectedResponsesAndTypes() {
        ErmActually ermActually = createErmActually();

        assertEquals(" uhohhhh... actually.. what are you saying??",
                ermActually.getResponse("sing a song"));
        assertEquals(Parser.CommandType.UNKNOWN, ermActually.getCommandType());
        assertEquals("Farewell! Hope you stop by again soon!", ermActually.getResponse("bye"));
        assertEquals(Parser.CommandType.BYE, ermActually.getCommandType());
    }

    @Test
    public void getResponse_invalidParsedInput_returnsErrorAndUnknownType() {
        ErmActually ermActually = createErmActually();

        assertEquals(" uhohhhh... Please add a description for todo!",
                ermActually.getResponse("todo"));
        assertEquals(Parser.CommandType.UNKNOWN, ermActually.getCommandType());
        assertEquals(" uhohhhh... Please provide a valid date in yyyy-MM-dd format.",
                ermActually.getResponse("on Tuesday"));
        assertEquals(Parser.CommandType.UNKNOWN, ermActually.getCommandType());
    }

    @Test
    public void constructor_loadSucceeds_hasNoStartupError() {
        assertNull(createErmActually().getStartupError());
    }

    @Test
    public void constructor_loadFails_recordsStartupErrorAndUsesEmptyList() {
        ErmActually ermActually = new ErmActually(new LoadFailingStorage());

        assertEquals(" uhohhhh... I couldn't load your tasks.", ermActually.getStartupError());
        assertEquals(" Here are the tasks in your list:\nWoohoo! No tasks found!",
                ermActually.getResponse("list"));
    }

    @Test
    public void getResponse_addSaveFails_returnsError() {
        ErmActually ermActually = new ErmActually(new FailingStorage(new ArrayList<>()));

        assertEquals(" uhohhhh... I couldn't save your tasks.",
                ermActually.getResponse("todo read"));
        assertEquals(Parser.CommandType.UNKNOWN, ermActually.getCommandType());
    }

    @Test
    public void getWelcomeMessage_returnsBannerAndGreeting() {
        ErmActually ermActually = createErmActually();

        String welcomeMessage = ermActually.getWelcomeMessage();

        assertEquals("+----------------+\n"
                + "|  Erm Actually  |\n"
                + "+----------------+\n"
                + "Greetings! I'm Erm Actually.\n"
                + "What can I actually do for you?", welcomeMessage);
    }

    @Test
    public void getResponse_markTask_updatesTaskAndCommandType() {
        ErmActually ermActually = createErmActually();
        ermActually.getResponse("todo borrow book");

        String response = ermActually.getResponse("mark 1");

        assertEquals("oh! good job you've actually finished this task:\n"
                + " [T][X] borrow book", response);
        assertEquals(Parser.CommandType.MARK, ermActually.getCommandType());
    }

    @Test
    public void constructor_savedTaskExists_listResponseContainsSavedTask() {
        ErmActually firstInstance = createErmActually();
        firstInstance.getResponse("todo borrow book");

        ErmActually secondInstance = createErmActually();

        assertEquals(" Here are the tasks in your list:\n"
                + " 1. [T][ ] borrow book", secondInstance.getResponse("list"));
    }

    @Test
    public void getResponse_invalidTaskNumber_returnsErrorAndUnknownCommandType() {
        ErmActually ermActually = createErmActually();

        String response = ermActually.getResponse("delete 1");

        assertEquals(" uhohhhh... That task number does not exist.", response);
        assertEquals(Parser.CommandType.UNKNOWN, ermActually.getCommandType());
    }

    /** Creates an application backed by a temporary test file. */
    private ErmActually createErmActually() {
        Path dataFile = temporaryDirectory.resolve("ErmActually.txt");
        return new ErmActually(dataFile.toString());
    }

    /** Provides loaded tasks but rejects every save attempt. */
    private static class FailingStorage extends Storage {
        private final ArrayList<Task> initialTasks;

        FailingStorage(ArrayList<Task> initialTasks) {
            super(Path.of("unused-test-file.txt"));
            this.initialTasks = initialTasks;
        }

        @Override
        public ArrayList<Task> load() {
            return new ArrayList<>(initialTasks);
        }

        @Override
        public void save(TaskList tasks) throws ErmActuallyException {
            throw new ErmActuallyException("I couldn't save your tasks.");
        }
    }

    /** Rejects loading so startup-error handling can be tested. */
    private static class LoadFailingStorage extends Storage {
        LoadFailingStorage() {
            super(Path.of("unused-test-file.txt"));
        }

        @Override
        public ArrayList<Task> load() throws ErmActuallyException {
            throw new ErmActuallyException("I couldn't load your tasks.");
        }
    }
}
