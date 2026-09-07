package ermactually.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Owns the application's task collection and its task-list operations.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks, typically loaded from storage.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "The initial task collection must exist";
        assert !tasks.contains(null) : "The initial task collection must not contain null";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "A task list must not contain null";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Marks the task at a zero-based index as completed. */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /** Marks the task at a zero-based index as incomplete. */
    public void unmark(int index) {
        tasks.get(index).unmarkAsDone();
    }

    /** Returns the task at a zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the task list has no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Finds the original zero-based indexes of tasks whose descriptions contain a keyword.
     * Matching is case-insensitive and preserves task-list order.
     *
     * @param keyword Keyword to search for in task descriptions.
     * @return Matching indexes in task-list order.
     */
    public ArrayList<Integer> findIndexes(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Finds the original zero-based indexes of deadlines and events occurring on a date.
     *
     * @param date Date to search for.
     * @return Matching indexes in task-list order.
     */
    public ArrayList<Integer> findIndexesOn(LocalDate date) {
        return IntStream.range(0, tasks.size())
                .filter(index -> {
                    Task task = tasks.get(index);
                    return task instanceof Deadline
                            && ((Deadline) task).occursOn(date)
                            || task instanceof Event
                            && ((Event) task).occursOn(date);
                })
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Allows collaborators such as Storage to process each task without exposing the list. */
    @Override
    public Iterator<Task> iterator() {
        return new ArrayList<>(tasks).iterator();
    }
}
