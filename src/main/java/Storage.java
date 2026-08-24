import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Loads tasks from disk and saves the current task list to disk.
 */
public class Storage {
    private static final String SAVE_FORMAT_VERSION = "V2";
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage backed by the file at the supplied path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks. A missing file represents an empty task list.
     *
     * @return Tasks restored from the data file.
     * @throws ErmActuallyException If the file cannot be read or contains invalid task data.
     */
    public ArrayList<Task> load() throws ErmActuallyException {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }

            ArrayList<Task> tasks = new ArrayList<>();
            for (String savedTask : Files.readAllLines(filePath)) {
                tasks.add(createTaskFromSavedLine(savedTask));
            }
            return tasks;
        } catch (IOException | SecurityException | ErmActuallyException e) {
            throw new ErmActuallyException("I couldn't load your tasks.");
        }
    }

    /**
     * Saves all tasks using a versioned format that preserves special characters.
     *
     * @param tasks Tasks to save.
     * @throws ErmActuallyException If the data file cannot be written.
     */
    public void save(TaskList tasks) throws ErmActuallyException {
        ArrayList<String> savedTasks = new ArrayList<>();
        for (Task task : tasks) {
            savedTasks.add(formatTaskForSaving(task));
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, savedTasks);
        } catch (IOException | SecurityException e) {
            throw new ErmActuallyException("I couldn't save your tasks.");
        }
    }

    /** Recreates one task from either the current or legacy save format. */
    private Task createTaskFromSavedLine(String savedTask) throws ErmActuallyException {
        String[] parts = savedTask.split(" \\| ", -1);
        if (parts.length > 0 && parts[0].equals(SAVE_FORMAT_VERSION)) {
            return createVersionTwoTask(parts);
        }
        return createLegacyTask(parts);
    }

    /** Recreates a task written in the current Base64-encoded save format. */
    private Task createVersionTwoTask(String[] parts) throws ErmActuallyException {
        if (parts.length < 4) {
            throw invalidSavedTask();
        }
        String[] details = new String[parts.length - 3];
        try {
            for (int i = 3; i < parts.length; i++) {
                details[i - 3] = new String(Base64.getDecoder().decode(parts[i]), StandardCharsets.UTF_8);
            }
        } catch (IllegalArgumentException e) {
            throw invalidSavedTask();
        }
        return createTask(parts[1], parts[2], details);
    }

    /** Recreates a task written in the earlier plain-text save format. */
    private Task createLegacyTask(String[] parts) throws ErmActuallyException {
        if (parts.length < 3) {
            throw invalidSavedTask();
        }
        String[] details = new String[parts.length - 2];
        System.arraycopy(parts, 2, details, 0, details.length);
        return createTask(parts[0], parts[1], details);
    }

    /** Creates and restores a task after validating its saved fields. */
    private Task createTask(String type, String status, String[] details) throws ErmActuallyException {
        if (!status.equals("0") && !status.equals("1")) {
            throw invalidSavedTask();
        }

        Task task;
        if (type.equals("T") && details.length == 1) {
            task = new Todo(details[0]);
        } else if (type.equals("D") && details.length == 2) {
            task = new Deadline(details[0], details[1]);
        } else if (type.equals("E") && details.length == 3) {
            task = new Event(details[0], details[1], details[2]);
        } else {
            throw invalidSavedTask();
        }

        if (status.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Converts a task to one line in the current save format. */
    private String formatTaskForSaving(Task task) {
        String isDone = task.isDone() ? "1" : "0";
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return joinSavedFields("D", isDone, deadline.description, deadline.toStorageString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return joinSavedFields("E", isDone, event.description,
                    event.getFromStorageString(), event.getToStorageString());
        }
        return joinSavedFields("T", isDone, task.description);
    }

    /** Encodes task details and joins all fields into one save-file line. */
    private String joinSavedFields(String type, String status, String... details) {
        ArrayList<String> fields = new ArrayList<>();
        fields.add(SAVE_FORMAT_VERSION);
        fields.add(type);
        fields.add(status);
        for (String detail : details) {
            fields.add(Base64.getEncoder().encodeToString(detail.getBytes(StandardCharsets.UTF_8)));
        }
        return String.join(FIELD_SEPARATOR, fields);
    }

    /** Creates the consistent exception used for malformed saved data. */
    private static ErmActuallyException invalidSavedTask() {
        return new ErmActuallyException("I couldn't load your tasks.");
    }
}
