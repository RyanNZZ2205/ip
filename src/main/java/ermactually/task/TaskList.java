package ermactually.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

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
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
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

    /** @return Number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** @return Whether the task list has no tasks. */
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
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            String normalizedDescription = tasks.get(i).getDescription().toLowerCase(Locale.ROOT);
            if (normalizedDescription.contains(normalizedKeyword)) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    /**
     * Finds the original zero-based indexes of deadlines and events occurring on a date.
     *
     * @param date Date to search for.
     * @return Matching indexes in task-list order.
     */
    public ArrayList<Integer> findIndexesOn(LocalDate date) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean occursOnDate = task instanceof Deadline
                    && ((Deadline) task).occursOn(date);
            occursOnDate = occursOnDate || task instanceof Event
                    && ((Event) task).occursOn(date);
            if (occursOnDate) {
                indexes.add(i);
            }
        }
        return indexes;
    }

    /** Allows collaborators such as Storage to process each task without exposing the list. */
    @Override
    public Iterator<Task> iterator() {
        return new ArrayList<>(tasks).iterator();
    }
}
