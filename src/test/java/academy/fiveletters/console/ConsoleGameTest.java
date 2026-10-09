package academy.fiveletters.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import academy.fiveletters.Constants;
import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.GameSession;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Консольная игра")
class ConsoleGameTest {

    private final Dictionary dictionary = new Dictionary(List.of("озеро", "арбуз", "сорок", "мышка"));

    @Test
    @DisplayName("Вывод победы совпадает с форматом задания")
    void winOutputFormat() {
        var output = play("1\nарбуз\nсорок\nмышка\nозеро\n2\n");

        assertThat(output.lines())
                .containsSubsequence(
                        "❌🟡❌❌🟡 арбуз",
                        "Осталось попыток: 5",
                        "❌🟡🟡🟡❌ сорок",
                        "Осталось попыток: 4",
                        "❌❌❌❌❌ мышка",
                        "Осталось попыток: 3",
                        "✅✅✅✅✅ озеро",
                        "Победа! Слово угадано за 4 попытки");
    }

    @Test
    @DisplayName("Вывод поражения совпадает с форматом задания")
    void loseOutputFormat() {
        var output = play("1\n" + "арбуз\n".repeat(6) + "2\n");

        assertThat(output).contains("Неудача. Загаданное слово: озеро").doesNotContain("Осталось попыток: 0");
    }

    @Test
    @DisplayName("Неверный ввод в партии объясняется и не тратит попытку")
    void invalidGuessIsExplained() {
        var output = play("1\nдом\nozero\nабвгд\nарбуз\n");

        assertThat(output.lines())
                .containsSubsequence(
                        "Нужно слово из 5 букв. Попытка не потрачена.",
                        "Можно вводить только русские буквы. Попытка не потрачена.",
                        "Такого слова нет в словаре. Попытка не потрачена.",
                        "❌🟡❌❌🟡 арбуз",
                        "Осталось попыток: 5");
    }

    @Test
    @DisplayName("Пункт меню можно ввести с пробелами")
    void menuChoiceIsStripped() {
        var output = play(" 1 \nозеро\n 2 \n");

        assertThat(output).contains("Победа!").contains("До встречи!");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"", "1\n", "1\nарбуз\n"})
    @DisplayName("Конец ввода спокойно завершает программу")
    void endOfInputStopsGame(String input) {
        assertThatCode(() -> play(input)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0} {1}")
    @CsvSource({
        "1, попытку",
        "2, попытки",
        "4, попытки",
        "5, попыток",
        "6, попыток",
        "11, попыток",
        "21, попытку",
        "22, попытки"
    })
    @DisplayName("Слово попытка склоняется по числу")
    void attemptsPhraseIsDeclined(int count, String word) {
        assertThat(ConsoleGame.attemptsPhrase(count)).isEqualTo(count + " " + word);
    }

    @Test
    @DisplayName("Загаданное слово показывается только с флагом")
    void answerIsShownOnlyWithFlag() {
        assertThat(play("1\n", true, false)).contains("Загадано: озеро");
        assertThat(play("1\n", false, false)).doesNotContain("Загадано");
    }

    @Test
    @DisplayName("При выходе печатается статистика за запуск")
    void statisticsIsPrintedOnExit() {
        var output = play("1\nозеро\n1\n" + "арбуз\n".repeat(6) + "2\n");

        assertThat(output.lines()).containsSubsequence("Сыграно партий: 2. Побед: 1 (50%).", "До встречи!");
    }

    @Test
    @DisplayName("Без сыгранных партий статистика не печатается")
    void noStatisticsWithoutGames() {
        assertThat(play("2\n")).doesNotContain("Сыграно");
    }

    @Test
    @DisplayName("Подсказка открывает первую ненайденную букву и не тратит попытку")
    void hintRevealsLetter() {
        var output = play("1\n?\nарбуз\n");

        assertThat(output.lines())
                .containsSubsequence("Подсказка: на 1 месте буква о", "❌🟡❌❌🟡 арбуз", "Осталось попыток: 5");
    }

    @Test
    @DisplayName("Вторая подсказка в партии не даётся")
    void secondHintIsRefused() {
        var output = play("1\n?\n?\n");

        assertThat(output).contains("Подсказка в этой партии уже использована.");
    }

    @Test
    @DisplayName("С цветом буквы раскрашиваются, значки остаются")
    void colorIsUsedWithFlag() {
        var output = play("1\nарбуз\nозеро\n", false, true);

        assertThat(output)
                .contains("❌🟡❌❌🟡 ")
                .contains(Constants.ANSI_GREEN_BACKGROUND)
                .contains(Constants.ANSI_YELLOW_BACKGROUND)
                .contains(Constants.ANSI_GRAY_BACKGROUND)
                .contains(Constants.ANSI_RESET);
    }

    @Test
    @DisplayName("Без флага цвета кодов ANSI нет")
    void noColorWithoutFlag() {
        assertThat(play("1\nарбуз\nозеро\n")).doesNotContain("\u001B");
    }

    @Test
    @DisplayName("Повтор проигрывает попытки из списка")
    void replayPlaysGuesses() {
        var output = replay(List.of("арбуз", "озеро"));

        assertThat(output.lines())
                .containsSubsequence(
                        "Повтор партии", "❌🟡❌❌🟡 арбуз", "✅✅✅✅✅ озеро", "Победа! Слово угадано за 2 попытки");
        assertThat(output).doesNotContain("1. Новая игра");
    }

    @Test
    @DisplayName("Попытки после конца партии при повторе не используются")
    void replayIgnoresExtraGuesses() {
        var output = replay(List.of("озеро", "арбуз"));

        assertThat(output).contains("Победа! Слово угадано за 1 попытку").doesNotContain("арбуз");
    }

    @Test
    @DisplayName("Если попыток не хватило, повтор сообщает об этом")
    void replayWithTooFewGuesses() {
        var output = replay(List.of("арбуз"));

        assertThat(output).contains("Попытки кончились, партия не завершена.");
    }

    @Test
    @DisplayName("Каждая завершённая партия передаётся на запись со всем вводом")
    void finishedGamesAreRecorded() {
        var recorded = new ArrayList<List<String>>();
        GameRecorder recorder = inputs -> {
            recorded.add(inputs);
            return Path.of("replays", "game_1.txt");
        };

        var output = output(ConsoleGame::run, "1\n?\nдом\n\nарбуз\nозеро\n1\nозеро\n2\n", false, false, recorder);

        assertThat(recorded).containsExactly(List.of("?", "дом", "арбуз", "озеро"), List.of("озеро"));
        assertThat(output).contains("Повтор сохранён: " + Path.of("replays", "game_1.txt"));
    }

    @Test
    @DisplayName("Незавершённая партия не записывается")
    void unfinishedGameIsNotRecorded() {
        var recorded = new ArrayList<List<String>>();

        output(ConsoleGame::run, "1\nарбуз\n", false, false, inputs -> {
            recorded.add(inputs);
            return Path.of("game.txt");
        });

        assertThat(recorded).isEmpty();
    }

    @Test
    @DisplayName("Ошибка записи не прерывает игру")
    void recordErrorDoesNotStopGame() {
        GameRecorder failing = inputs -> {
            throw new IOException("диск заполнен");
        };

        var output = output(ConsoleGame::run, "1\nозеро\n2\n", false, false, failing);

        assertThat(output)
                .contains("Не удалось сохранить повтор: диск заполнен")
                .contains("До встречи!");
    }

    private String play(String input) {
        return play(input, false, false);
    }

    private String play(String input, boolean showAnswer, boolean color) {
        return output(ConsoleGame::run, input, showAnswer, color);
    }

    private String replay(List<String> guesses) {
        return output(console -> console.replay(guesses), "", false, false);
    }

    private String output(Consumer<ConsoleGame> action, String input, boolean showAnswer, boolean color) {
        return output(action, input, showAnswer, color, null);
    }

    private String output(
            Consumer<ConsoleGame> action, String input, boolean showAnswer, boolean color, GameRecorder recorder) {
        var bytes = new ByteArrayOutputStream();
        var out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        var in = new BufferedReader(new StringReader(input));

        action.accept(
                new ConsoleGame(() -> new GameSession(dictionary, "озеро", 6), in, out, showAnswer, color, recorder));

        return bytes.toString(StandardCharsets.UTF_8);
    }
}
