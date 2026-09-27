package academy.fiveletters.game;

public sealed interface GuessResult {

    record Accepted(Attempt attempt) implements GuessResult {}

    record Rejected(GuessError error) implements GuessResult {}
}
