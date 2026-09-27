package academy.fiveletters.game;

import java.util.Random;

public final class GameSeries {

    private final Dictionary dictionary;
    private final Random random;

    public GameSeries(Dictionary dictionary, long seed) {
        this.dictionary = dictionary;
        this.random = new Random(seed);
    }

    public GameSession next() {
        return Game.startGame(dictionary, Game.DEFAULT_MAX_ATTEMPTS, random.nextLong());
    }
}
