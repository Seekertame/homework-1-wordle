package academy.fiveletters.game;

import java.util.Random;

public final class Game {

    public static final int DEFAULT_MAX_ATTEMPTS = 6;

    private Game() {}

    public static GameSession startGame(Dictionary dictionary, int maxAttempts, long seed) {
        var words = dictionary.words();
        var random = new Random(seed);
        var answer = words.get(random.nextInt(words.size()));

        return new GameSession(dictionary, answer, maxAttempts);
    }
}
