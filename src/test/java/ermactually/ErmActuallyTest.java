package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests shared command processing used by both the console and GUI. */
public class ErmActuallyTest {
    @TempDir
    private Path temporaryDirectory;

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
}
