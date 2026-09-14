package ermactually;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ermactually.task.Deadline;
import ermactually.task.Event;
import ermactually.task.Task;
import ermactually.task.TaskList;
import ermactually.task.Todo;

/** Tests loading and saving tasks in both supported persistence formats. */
public class StorageTest {
    private static final String LOAD_ERROR_MESSAGE = "I couldn't load your tasks.";
    private static final String SAVE_ERROR_MESSAGE = "I couldn't save your tasks.";

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_duplicateSavedTasks_exceptionThrown() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | borrow book\nT | 1 | borrow book");

        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Storage(dataFile).load());

        assertEquals(LOAD_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void load_missingFile_returnsEmptyList() throws ErmActuallyException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveAndLoad_allTaskTypes_preservesFieldsAndCompletionState()
            throws ErmActuallyException, IOException {
        Path dataFile = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(dataFile);
        Todo todo = new Todo("read | review 😀");
        todo.markAsDone();
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                todo,
                new Deadline("submit report", "2026-09-14 1900"),
                new Event("conference", "2026-09-15", "2026-09-17 1730"))));

        storage.save(tasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(Files.exists(dataFile));
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][X] read | review 😀", loadedTasks.get(0).toString());
        assertEquals("[D][ ] submit report (by: Sep 14 2026 7:00 PM)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] conference (from: Sep 15 2026 to: Sep 17 2026 5:30 PM)",
                loadedTasks.get(2).toString());
        assertTrue(Files.readAllLines(dataFile).stream().allMatch(line -> line.startsWith("V2 | ")));
    }

    @Test
    public void load_legacyTasks_restoresEverySupportedType()
            throws IOException, ErmActuallyException {
        Path dataFile = temporaryDirectory.resolve("legacy.txt");
        Files.write(dataFile, List.of(
                "T | 1 | borrow book",
                "D | 0 | return book | 2026-09-14",
                "E | 1 | trip | 2026-09-15T09:00 | 2026-09-16T18:00"));

        ArrayList<Task> tasks = new Storage(dataFile).load();

        assertEquals(List.of(
                "[T][X] borrow book",
                "[D][ ] return book (by: Sep 14 2026)",
                "[E][X] trip (from: Sep 15 2026 9:00 AM to: Sep 16 2026 6:00 PM)"),
                tasks.stream().map(Task::toString).toList());
    }

    @Test
    public void load_invalidLines_throwsConsistentError() throws IOException {
        List<String> invalidLines = List.of(
                "T | 0",
                "T | 2 | task",
                "X | 0 | task",
                "T | 0 | task | extra",
                "D | 0 | task",
                "E | 0 | task | 2026-09-14",
                "V2 | T | 0",
                "V2 | T | 0 | not-base64!",
                "V2 | X | 0 | dGFzaw==");

        for (int i = 0; i < invalidLines.size(); i++) {
            Path dataFile = temporaryDirectory.resolve("invalid-" + i + ".txt");
            Files.writeString(dataFile, invalidLines.get(i));

            ErmActuallyException exception = assertThrows(
                    ErmActuallyException.class, () -> new Storage(dataFile).load());
            assertEquals(LOAD_ERROR_MESSAGE, exception.getMessage());
        }
    }

    @Test
    public void load_oneValidThenInvalidLine_doesNotReturnPartialResults() throws IOException {
        Path dataFile = temporaryDirectory.resolve("partially-invalid.txt");
        Files.write(dataFile, List.of("T | 0 | valid", "X | 0 | invalid"));

        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Storage(dataFile).load());

        assertEquals(LOAD_ERROR_MESSAGE, exception.getMessage());
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

    @Test
    public void load_directoryInsteadOfFile_throwsLoadError() {
        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> new Storage(temporaryDirectory).load());

        assertEquals(LOAD_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void save_emptyList_createsEmptyFile() throws ErmActuallyException, IOException {
        Path dataFile = temporaryDirectory.resolve("empty.txt");

        new Storage(dataFile).save(new TaskList());

        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void save_pathIsExistingDirectory_throwsSaveError() {
        Storage storage = new Storage(temporaryDirectory);

        ErmActuallyException exception = assertThrows(
                ErmActuallyException.class, () -> storage.save(new TaskList()));

        assertEquals(SAVE_ERROR_MESSAGE, exception.getMessage());
        assertFalse(Files.isRegularFile(temporaryDirectory));
    }
}
