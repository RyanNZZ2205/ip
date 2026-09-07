package ermactually.task;

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
     * Returns this task in the format used by the list command.
     *
     * @return The task status and description.
     */
    @Override
    public String toString() {
        return "[" + taskType.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
