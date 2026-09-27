package academy.fiveletters.console;

import academy.fiveletters.game.Attempt;
import academy.fiveletters.game.Game;
import academy.fiveletters.game.GameSession;
import academy.fiveletters.game.GameStatus;
import academy.fiveletters.game.GuessError;
import academy.fiveletters.game.GuessResult;
import academy.fiveletters.game.LetterStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

public final class ConsoleGame {

    private final Supplier<GameSession> sessions;
    private final BufferedReader in;
    private final PrintStream out;
    private final boolean showAnswer;

    public ConsoleGame(Supplier<GameSession> sessions, BufferedReader in, PrintStream out, boolean showAnswer) {
        this.sessions = sessions;
        this.in = in;
        this.out = out;
        this.showAnswer = showAnswer;
    }

    public void run() {
        while (true) {
            out.println("1. Новая игра");
            out.println("2. Выход");

            var choice = readLine();
            if (choice == null) {
                return;
            }

            switch (choice.strip()) {
                case "1" -> {
                    if (!playGame()) {
                        return;
                    }
                }
                case "2" -> {
                    out.println("До встречи!");
                    return;
                }
                default -> out.println("Нет такого пункта меню. Введите 1 или 2.");
            }
        }
    }

    private boolean playGame() {
        var session = sessions.get();
        out.println("Угадайте слово из 5 букв. Попыток: " + session.maxAttempts());
        if (showAnswer) {
            out.println("Загадано: " + session.answer());
        }

        while (session.status() == GameStatus.IN_PROGRESS) {
            var guess = readLine();
            if (guess == null) {
                return false;
            }

            switch (Game.applyGuess(session, guess)) {
                case GuessResult.Accepted(Attempt attempt) -> printAttempt(session, attempt);
                case GuessResult.Rejected(GuessError error) -> out.println(errorMessage(error));
            }
        }

        if (session.status() == GameStatus.WIN) {
            out.println("Победа! Слово угадано за " + attemptsPhrase(session.attemptsUsed()));
        } else {
            out.println("Неудача. Загаданное слово: " + session.answer());
        }
        return true;
    }

    private void printAttempt(GameSession session, Attempt attempt) {
        var symbols = attempt.statuses().stream().map(LetterStatus::symbol).collect(Collectors.joining());
        out.println(symbols + " " + attempt.guess());

        if (session.status() == GameStatus.IN_PROGRESS) {
            out.println("Осталось попыток: " + (session.maxAttempts() - session.attemptsUsed()));
        }
    }

    private static String errorMessage(GuessError error) {
        return switch (error) {
            case WRONG_LENGTH -> "Нужно слово из 5 букв. Попытка не потрачена.";
            case NOT_LETTERS -> "Можно вводить только русские буквы. Попытка не потрачена.";
            case NOT_IN_DICTIONARY -> "Такого слова нет в словаре. Попытка не потрачена.";
        };
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    static String attemptsPhrase(int count) {
        if (count % 100 >= 11 && count % 100 <= 14) {
            return count + " попыток";
        }
        return switch (count % 10) {
            case 1 -> count + " попытку";
            case 2, 3, 4 -> count + " попытки";
            default -> count + " попыток";
        };
    }

    private @Nullable String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать ввод", e);
        }
    }
}
