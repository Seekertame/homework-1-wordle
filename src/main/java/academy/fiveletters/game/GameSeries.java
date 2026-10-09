package academy.fiveletters.game;

import static academy.fiveletters.Constants.DEFAULT_MAX_ATTEMPTS;

import java.util.Random;
import org.jspecify.annotations.Nullable;

/**
 * Серия партий за один запуск программы.
 *
 * <p>Из одного seed серия получает отдельный seed для каждой партии. Поэтому слова в партиях разные, а при том же seed
 * вся серия повторяется.
 */
public final class GameSeries {

    private final Dictionary dictionary;
    private final Dictionary answers;
    private final Random random;
    private @Nullable Long lastSeed;

    /** Ответ выбирается из всего словаря. */
    public GameSeries(Dictionary dictionary, long seed) {
        this(dictionary, dictionary, seed);
    }

    /** Ответ выбирается из {@code answers}, а ввод проверяется по всему {@code dictionary}. */
    public GameSeries(Dictionary dictionary, Dictionary answers, long seed) {
        this.dictionary = dictionary;
        this.answers = answers;
        this.random = new Random(seed);
    }

    public GameSession next() {
        long seed = random.nextLong();
        lastSeed = seed;
        return Game.startGame(dictionary, answers, DEFAULT_MAX_ATTEMPTS, seed);
    }

    /**
     * Seed последней начатой партии. По нему эту партию можно повторить через {@link Game#startGame}.
     *
     * @throws IllegalStateException если партий ещё не было
     */
    public long lastSeed() {
        var seed = lastSeed;
        if (seed == null) {
            throw new IllegalStateException("В серии ещё не было партий");
        }
        return seed;
    }
}
