package ermactually.task;

/**
 * Lists the supported task types and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

    /**
     * Creates a task type with its display symbol.
     *
     * @param symbol Symbol used to identify this task type in the console UI.
     */
    TaskType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used to display this task type.
     *
     * @return Task type symbol.
     */
    public String getSymbol() {
        return symbol;
    }
}
