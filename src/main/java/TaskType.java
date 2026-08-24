/**
 * Lists the supported task types and their display symbols.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String symbol;

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
