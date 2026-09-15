package ermactually.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a task the user can complete or leave unfinished.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType taskType;

    /**
     * Creates an unfinished task with the given description and type.
     *
     * @param description Task description.
     * @param taskType Task type.
     */
    public Task(String description, TaskType taskType) {
        assert description != null && !description.isBlank()
                : "A task must have a non-empty description";
        assert taskType != null : "A task must have a type";
        this.description = description;
        this.taskType = taskType;
        this.isDone = false;
    }

    /**
     * Returns the icon representing this task's completion status.
     *
     * @return "X" when the task is complete; otherwise, a space.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void unmarkAsDone() {
        isDone = false;
    }

    /**
     * Returns whether this task is complete.
     *
     * @return True if the task is complete.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task description.
     *
     * @return Task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether another task has the same type and user-supplied details.
     * Completion status is deliberately excluded because it is mutable task state.
     *
     * @param other Object to compare with this task.
     * @return {@code true} when both objects describe the same task.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Task otherTask = (Task) other;
        return description.equals(otherTask.description);
    }

    /** Returns a hash based on the immutable details used by {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), description);
    }

    /**
     * Returns the date used to place this task chronologically.
     *
     * @return Relevant date, or an empty value for an undated task.
     */
    public Optional<LocalDate> getRelevantDate() {
        return Optional.empty();
    }

    /**
     * Returns the optional time used after comparing this task's relevant date.
     *
     * @return Relevant time, or an empty value when no time was supplied.
     */
    public Optional<LocalTime> getRelevantTime() {
        return Optional.empty();
    }

    /**
     * Returns whether this task occurs on the requested date.
     *
     * @param date Date to compare with this task.
     * @return {@code false} because a basic task has no date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task in the format used by the list command.
     *
     * @return The task status and description.
     */
    @Override
    public String toString() {
        return "[" + taskType.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
