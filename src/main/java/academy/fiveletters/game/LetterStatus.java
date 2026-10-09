package academy.fiveletters.game;

/** Статус одной буквы в попытке. */
public enum LetterStatus {
    CORRECT("✅"),
    PRESENT("🟡"),
    /** Буквы нет в слове, или все такие буквы ответа уже учтены. */
    ABSENT("❌");

    private final String symbol;

    LetterStatus(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
