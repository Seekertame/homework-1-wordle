package academy.fiveletters.game;

import java.util.List;

public record Attempt(String guess, List<LetterStatus> statuses) {

    public Attempt {
        statuses = List.copyOf(statuses);
    }
}
