package academy.fiveletters.game;

import static academy.fiveletters.Constants.RUSSIAN_LETTERS;
import static academy.fiveletters.Constants.WORD_LENGTH;

import java.util.Locale;
import java.util.Optional;
import java.util.Random;

/** Правила игры: начало партии, обработка попытки и подсказка. Состояния не хранит, все методы статические. */
public final class Game {

    private Game() {}

    /**
     * Начинает партию со словом, выбранным по seed. Одинаковые словарь и seed всегда дают одно и то же слово.
     *
     * @throws IllegalArgumentException если попыток не больше нуля
     */
    public static GameSession startGame(Dictionary dictionary, int maxAttempts, long seed) {
        return startGame(dictionary, dictionary, maxAttempts, seed);
    }

    /**
     * Начинает партию со словом из {@code answers}, например из уровня или категории. Ввод проверяется по всему
     * {@code dictionary}.
     *
     * @throws IllegalArgumentException если попыток не больше нуля или выбранного слова нет в {@code dictionary}
     */
    public static GameSession startGame(Dictionary dictionary, Dictionary answers, int maxAttempts, long seed) {
        var words = answers.words();
        var random = new Random(seed);
        var answer = words.get(random.nextInt(words.size()));

        return new GameSession(dictionary, answer, maxAttempts);
    }

    /**
     * Проверяет ввод игрока и, если он верный, записывает попытку в партию. Неверный ввод не тратит попытку.
     *
     * <p>Ввод приводится к нижнему регистру, пробелы по краям убираются, ё заменяется на е. Потом проверяются длина,
     * русские буквы и наличие слова в словаре.
     *
     * @return {@link GuessResult.Accepted} с попыткой или {@link GuessResult.Rejected} с причиной отказа
     * @throws IllegalStateException если партия уже завершена
     */
    public static GuessResult applyGuess(GameSession session, String guess) {
        if (session.status() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Партия уже завершена со статусом " + session.status());
        }

        var word = guess.strip().toLowerCase(Locale.ROOT).replace('ё', 'е');

        if (word.length() != WORD_LENGTH) {
            return new GuessResult.Rejected(GuessError.WRONG_LENGTH);
        }
        if (!word.matches(RUSSIAN_LETTERS)) {
            return new GuessResult.Rejected(GuessError.NOT_LETTERS);
        }
        if (!session.dictionary().contains(word)) {
            return new GuessResult.Rejected(GuessError.NOT_IN_DICTIONARY);
        }

        var attempt = new Attempt(word, LetterMatcher.match(session.answer(), word));
        session.recordAttempt(attempt);

        return new GuessResult.Accepted(attempt);
    }

    /**
     * Открывает одну букву ответа: на первом месте, где игрок ещё ни разу не получил {@link LetterStatus#CORRECT}.
     * Подсказка не тратит попытку. В партии она одна.
     *
     * @return подсказка или пустой {@link Optional}, если все места уже найдены. Тогда подсказка не расходуется
     * @throws IllegalStateException если партия завершена или подсказка уже использована
     */
    public static Optional<Hint> hint(GameSession session) {
        if (session.status() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Партия уже завершена со статусом " + session.status());
        }
        if (session.hintUsed()) {
            throw new IllegalStateException("Подсказка в этой партии уже использована");
        }

        var answer = session.answer();
        for (int position = 0; position < answer.length(); position++) {
            if (!isFound(session, position)) {
                session.useHint();
                return Optional.of(new Hint(position, answer.charAt(position)));
            }
        }
        return Optional.empty();
    }

    private static boolean isFound(GameSession session, int position) {
        return session.history().stream().anyMatch(attempt -> attempt.statuses().get(position) == LetterStatus.CORRECT);
    }
}
