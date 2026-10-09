package academy.fiveletters.game;

/** Результат {@link Game#applyGuess}: попытка принята или ввод отклонён. Других вариантов нет. */
public sealed interface GuessResult {

    /** Ввод принят, попытка записана в партию. */
    record Accepted(Attempt attempt) implements GuessResult {}

    /** Ввод отклонён, попытка не потрачена. */
    record Rejected(GuessError error) implements GuessResult {}
}
