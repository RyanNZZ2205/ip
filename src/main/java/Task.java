/**
 * Represents a task the user can complete or leave unfinished.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType taskType;

    public Task(String description, TaskType taskType) {
        this.description = description;
        this.taskType = taskType;
        this.isDone = false;
    }

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

    public boolean isDone() {
        return isDone;
    }
    /**
     * Returns this task in the format used by the list command.
     *
     * @return the task status and description
     */
    @Override
    public String toString() {
        return "[" + taskType.getSymbol() + "][" + getStatusIcon() + "] " + description;
    }
}
