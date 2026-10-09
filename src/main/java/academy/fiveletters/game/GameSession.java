package academy.fiveletters.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Одна партия: загаданное слово, лимит попыток, история попыток и статус.
 *
 * <p>Попытки записываются только через {@link Game#applyGuess}, после проверки ввода.
 */
public class GameSession {

    private final Dictionary dictionary;
    private final String answer;
    private final int maxAttempts;
    private final List<Attempt> history = new ArrayList<>();
    private GameStatus status;
    private boolean hintUsed;

    /**
     * Создаёт новую партию в статусе {@link GameStatus#IN_PROGRESS}.
     *
     * @throws IllegalArgumentException если попыток не больше нуля или загаданного слова нет в словаре
     */
    public GameSession(Dictionary dictionary, String answer, int maxAttempts) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException("Количество попыток должно быть больше нуля, передано: " + maxAttempts);
        }

        if (!dictionary.contains(answer)) {
            throw new IllegalArgumentException("Загаданного слова '" + answer + "' нет в словаре");
        }

        this.dictionary = dictionary;
        this.answer = answer;
        this.maxAttempts = maxAttempts;
        this.status = GameStatus.IN_PROGRESS;
    }

    /**
     * Записывает проверенную попытку и пересчитывает статус. Если слово угадано на последней попытке, это победа.
     *
     * @throws IllegalStateException если партия уже завершена
     */
    void recordAttempt(Attempt attempt) {
        if (status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Партия уже завершена со статусом " + status + ", новые попытки не принимаются");
        }

        history.add(attempt);

        if (attempt.guess().equals(answer)) {
            status = GameStatus.WIN;
        } else if (attemptsUsed() == maxAttempts) {
            status = GameStatus.LOSE;
        }
    }

    void useHint() {
        hintUsed = true;
    }

    public Dictionary dictionary() {
        return dictionary;
    }

    public String answer() {
        return answer;
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public int attemptsUsed() {
        return history.size();
    }

    /** Неизменяемая копия истории попыток. */
    public List<Attempt> history() {
        return List.copyOf(history);
    }

    public GameStatus status() {
        return status;
    }

    public boolean hintUsed() {
        return hintUsed;
    }
}
