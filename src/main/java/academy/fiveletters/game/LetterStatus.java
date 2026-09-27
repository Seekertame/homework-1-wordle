package academy.fiveletters.game;

public enum LetterStatus {
    CORRECT("✅"),
    PRESENT("🟡"),
    ABSENT("❌");

    private final String symbol;

    LetterStatus(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
