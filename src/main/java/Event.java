public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event with a description, start time, and end time.
     *
     * @param description task description
     * @param from event start time
     * @param to event end time
     * @throws ErmActuallyException if a required value is empty
     */
    public Event(String description, String from, String to) throws ErmActuallyException {
        super(validateDescription(description));
        this.from = validateTime(from, "start time");
        this.to = validateTime(to, "end time");
    }

    private static String validateDescription(String description) throws ErmActuallyException {
        if (description == null || description.trim().isEmpty()) {
            throw new ErmActuallyException("The description of an event cannot be empty.");
        }
        return description.trim();
    }

    private static String validateTime(String time, String timeName) throws ErmActuallyException {
        if (time == null || time.trim().isEmpty()) {
            throw new ErmActuallyException("The event " + timeName + " cannot be empty.");
        }
        return time.trim();
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}

