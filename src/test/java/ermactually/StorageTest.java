package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ermactually.task.TaskList;
import ermactually.task.Todo;

/** Tests validation and persistence behavior provided by {@link Storage}. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_duplicateSavedTasks_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | borrow book\nT | 1 | borrow book");

        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Storage(dataFile).load());

        assertEquals("I couldn't load your tasks.", exception.getMessage());
    }

    @Test
    public void save_existingFile_replacesCompleteContents() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "old contents");
        TaskList tasks = new TaskList();
        tasks.add(new Todo("borrow book"));

        new Storage(dataFile).save(tasks);

        assertEquals("V2 | T | 0 | Ym9ycm93IGJvb2s=" + System.lineSeparator(),
                Files.readString(dataFile));
    }
}
