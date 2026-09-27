package academy.fiveletters.game;

import java.util.Locale;
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

    public static GuessResult applyGuess(GameSession session, String guess) {
        if (session.status() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Партия уже завершена со статусом " + session.status());
        }

        var word = guess.strip().toLowerCase(Locale.ROOT).replace('ё', 'е');

        if (word.length() != Dictionary.WORD_LENGTH) {
            return new GuessResult.Rejected(GuessError.WRONG_LENGTH);
        }
        if (!word.matches("[а-я]+")) {
            return new GuessResult.Rejected(GuessError.NOT_LETTERS);
        }
        if (!session.dictionary().contains(word)) {
            return new GuessResult.Rejected(GuessError.NOT_IN_DICTIONARY);
        }

        var attempt = new Attempt(word, LetterMatcher.match(session.answer(), word));
        session.recordAttempt(attempt);

        return new GuessResult.Accepted(attempt);
    }
}
