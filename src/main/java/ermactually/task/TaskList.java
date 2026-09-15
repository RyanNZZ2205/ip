package ermactually.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;

import ermactually.ErmActuallyException;

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

    /**
     * Adds a unique task to the end of the list.
     *
     * @param task Task to add.
     * @throws ErmActuallyException If a task with the same details already exists.
     */
    public void add(Task task) throws ErmActuallyException {
        assert task != null : "A task list must not contain null";
        if (tasks.contains(task)) {
            throw new ErmActuallyException("That task already exists.");
        }
        tasks.add(task);
    }

    /**
     * Restores a task at a specific position after an operation fails.
     *
     * @param index Position at which to restore the task.
     * @param task Task to restore.
     */
    public void add(int index, Task task) {
        assert task != null : "A task list must not contain null";
        tasks.add(index, task);
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
     * Creates a stably sorted copy using each task's relevant date and time.
     * Dated tasks precede todos, and date-only tasks precede timed tasks on the same date.
     *
     * @param direction Direction used for date and time comparisons.
     * @return A sorted task list without changing this task list.
     */
    public TaskList createChronologicallySorted(SortDirection direction) {
        assert direction != null : "A sort direction must be provided";
        ArrayList<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort((firstTask, secondTask) -> compareChronologically(
                firstTask, secondTask, direction));
        return new TaskList(sortedTasks);
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
     * Finds the original zero-based indexes of tasks occurring on a date.
     *
     * @param date Date to search for.
     * @return Matching indexes in task-list order.
     */
    public ArrayList<Integer> findIndexesOn(LocalDate date) {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
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

    /** Compares two tasks while keeping todos and missing times in their fixed groups. */
    private static int compareChronologically(Task firstTask, Task secondTask,
            SortDirection direction) {
        Optional<LocalDate> firstDate = firstTask.getRelevantDate();
        Optional<LocalDate> secondDate = secondTask.getRelevantDate();
        if (firstDate.isEmpty() || secondDate.isEmpty()) {
            return compareOptionalPresence(firstDate.isPresent(), secondDate.isPresent(), true);
        }

        int dateComparison = firstDate.get().compareTo(secondDate.get());
        if (dateComparison != 0) {
            return applyDirection(dateComparison, direction);
        }

        Optional<LocalTime> firstTime = firstTask.getRelevantTime();
        Optional<LocalTime> secondTime = secondTask.getRelevantTime();
        if (firstTime.isEmpty() || secondTime.isEmpty()) {
            return compareOptionalPresence(firstTime.isPresent(), secondTime.isPresent(), false);
        }
        return applyDirection(firstTime.get().compareTo(secondTime.get()), direction);
    }

    /** Orders optional values using a fixed presence rule without applying the sort direction. */
    private static int compareOptionalPresence(boolean isFirstPresent, boolean isSecondPresent,
            boolean isPresentFirst) {
        if (isFirstPresent == isSecondPresent) {
            return 0;
        }
        return isFirstPresent == isPresentFirst ? -1 : 1;
    }

    /** Reverses a chronological comparison only when descending order was requested. */
    private static int applyDirection(int comparison, SortDirection direction) {
        return direction == SortDirection.ASCENDING ? comparison : -comparison;
    }
}
