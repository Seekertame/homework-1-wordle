package academy.fiveletters.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.GameSession;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
        assertThat(play("1\n", true)).contains("Загадано: озеро");
        assertThat(play("1\n", false)).doesNotContain("Загадано");
    }

    private String play(String input) {
        return play(input, false);
    }

    private String play(String input, boolean showAnswer) {
        var bytes = new ByteArrayOutputStream();
        var out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        var in = new BufferedReader(new StringReader(input));

        new ConsoleGame(() -> new GameSession(dictionary, "озеро", 6), in, out, showAnswer).run();

        return bytes.toString(StandardCharsets.UTF_8);
    }
}
