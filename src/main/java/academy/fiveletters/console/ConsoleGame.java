package academy.fiveletters.console;

import static academy.fiveletters.Constants.ANSI_BLACK_TEXT;
import static academy.fiveletters.Constants.ANSI_GRAY_BACKGROUND;
import static academy.fiveletters.Constants.ANSI_GREEN_BACKGROUND;
import static academy.fiveletters.Constants.ANSI_RESET;
import static academy.fiveletters.Constants.ANSI_YELLOW_BACKGROUND;
import static academy.fiveletters.Constants.HINT_COMMAND;
import static academy.fiveletters.Constants.WORD_LENGTH;

import academy.fiveletters.game.Attempt;
import academy.fiveletters.game.Game;
import academy.fiveletters.game.GameSession;
import academy.fiveletters.game.GameStatus;
import academy.fiveletters.game.GuessError;
import academy.fiveletters.game.GuessResult;
import academy.fiveletters.game.LetterStatus;
import academy.fiveletters.game.Statistics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

/**
 * Консольный интерфейс игры: меню, цикл партий и вывод результата.
 *
 * <p>Правил игры здесь нет. Консоль читает строки, вызывает {@link Game} и печатает результат.
 */
public final class ConsoleGame {

    private final Supplier<GameSession> sessions;
    private final BufferedReader in;
    private final PrintStream out;
    private final boolean showAnswer;
    private final boolean color;
    private final @Nullable GameRecorder recorder;
    private final Statistics statistics = new Statistics();

    public ConsoleGame(
            Supplier<GameSession> sessions, BufferedReader in, PrintStream out, boolean showAnswer, boolean color) {
        this(sessions, in, out, showAnswer, color, null);
    }

    /** {@code recorder} сохраняет каждую завершённую партию. Если он {@code null}, партии не сохраняются. */
    public ConsoleGame(
            Supplier<GameSession> sessions,
            BufferedReader in,
            PrintStream out,
            boolean showAnswer,
            boolean color,
            @Nullable GameRecorder recorder) {
        this.sessions = sessions;
        this.in = in;
        this.out = out;
        this.showAnswer = showAnswer;
        this.color = color;
        this.recorder = recorder;
    }

    /** Показывает меню и проводит партии, пока игрок не выберет выход или пока не закончится ввод. */
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
                    if (!playGame(this::readLine)) {
                        return;
                    }
                }
                case "2" -> {
                    printStatistics();
                    out.println("До встречи!");
                    return;
                }
                default -> out.println("Нет такого пункта меню. Введите 1 или 2.");
            }
        }
    }

    /**
     * Повторяет одну партию без меню. Попытки берутся из списка, а не с клавиатуры. Попытки после конца партии не
     * используются.
     *
     * @param guesses попытки по порядку
     */
    public void replay(List<String> guesses) {
        out.println("Повтор партии");
        var remaining = guesses.iterator();
        if (!playGame(() -> remaining.hasNext() ? remaining.next() : null)) {
            out.println("Попытки кончились, партия не завершена.");
        }
    }

    private boolean playGame(GuessSource guesses) {
        var session = sessions.get();
        out.println("Угадайте слово из " + WORD_LENGTH + " букв. Попыток: " + session.maxAttempts());
        out.println("Для подсказки введите " + HINT_COMMAND);
        if (showAnswer) {
            out.println("Загадано: " + session.answer());
        }

        var inputs = new ArrayList<String>();
        while (session.status() == GameStatus.IN_PROGRESS) {
            var guess = guesses.next();
            if (guess == null) {
                return false;
            }
            if (!guess.isBlank()) {
                inputs.add(guess.strip());
            }

            if (guess.strip().equals(HINT_COMMAND)) {
                printHint(session);
                continue;
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
        statistics.record(session);
        saveReplay(inputs);
        return true;
    }

    private void saveReplay(List<String> inputs) {
        var currentRecorder = recorder;
        if (currentRecorder == null) {
            return;
        }
        try {
            out.println("Повтор сохранён: " + currentRecorder.save(inputs));
        } catch (IOException e) {
            out.println("Не удалось сохранить повтор: " + e.getMessage());
        }
    }

    private void printAttempt(GameSession session, Attempt attempt) {
        var symbols = attempt.statuses().stream().map(LetterStatus::symbol).collect(Collectors.joining());
        var word = color ? colored(attempt) : attempt.guess();
        out.println(symbols + " " + word);

        if (session.status() == GameStatus.IN_PROGRESS) {
            out.println("Осталось попыток: " + (session.maxAttempts() - session.attemptsUsed()));
        }
    }

    private static String colored(Attempt attempt) {
        var result = new StringBuilder();
        for (int i = 0; i < attempt.guess().length(); i++) {
            result.append(background(attempt.statuses().get(i)))
                    .append(ANSI_BLACK_TEXT)
                    .append(' ')
                    .append(attempt.guess().charAt(i))
                    .append(' ')
                    .append(ANSI_RESET);
        }
        return result.toString();
    }

    private static String background(LetterStatus status) {
        return switch (status) {
            case CORRECT -> ANSI_GREEN_BACKGROUND;
            case PRESENT -> ANSI_YELLOW_BACKGROUND;
            case ABSENT -> ANSI_GRAY_BACKGROUND;
        };
    }

    private void printHint(GameSession session) {
        if (session.hintUsed()) {
            out.println("Подсказка в этой партии уже использована.");
            return;
        }
        Game.hint(session)
                .ifPresentOrElse(
                        hint -> out.println("Подсказка: на " + (hint.position() + 1) + " месте буква " + hint.letter()),
                        () -> out.println("Все буквы уже найдены, подсказать нечего."));
    }

    private void printStatistics() {
        if (statistics.played() > 0) {
            out.println("Сыграно партий: " + statistics.played() + ". Побед: " + statistics.won() + " ("
                    + statistics.winPercent() + "%).");
        }
    }

    private static String errorMessage(GuessError error) {
        return switch (error) {
            case WRONG_LENGTH -> "Нужно слово из " + WORD_LENGTH + " букв. Попытка не потрачена.";
            case NOT_LETTERS -> "Можно вводить только русские буквы. Попытка не потрачена.";
            case NOT_IN_DICTIONARY -> "Такого слова нет в словаре. Попытка не потрачена.";
        };
    }

    /** Склоняет слово «попытка» по числу: 1 попытку, 2 попытки, 5 попыток, 11 попыток, 21 попытку. */
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

    /** Откуда берутся попытки: с клавиатуры или из списка при повторе. {@code null} значит, что попытки кончились. */
    @FunctionalInterface
    private interface GuessSource {
        @Nullable
        String next();
    }
}
