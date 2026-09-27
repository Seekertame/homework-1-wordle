package academy.fiveletters.game;

import java.util.ArrayList;
import java.util.List;

public class GameSession {

    private final Dictionary dictionary;
    private final String answer;
    private final int maxAttempts;
    private final List<Attempt> history = new ArrayList<>();
    private GameStatus status;

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

    public List<Attempt> history() {
        return List.copyOf(history);
    }

    public GameStatus status() {
        return status;
    }
}
