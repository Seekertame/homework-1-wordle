package academy.fiveletters.mandatory.mr3;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.GameSeries;
import academy.fiveletters.game.LetterMatcher;
import academy.fiveletters.game.LetterStatus;
import academy.fiveletters.support.CliRunner;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: меню и режим автопроверки. */
@DisplayName("MR3. Меню и детерминированный режим")
class MenuTest {

    private static final String SEED = "42";

    @Test
    @DisplayName("Некорректный пункт меню не роняет программу")
    void invalidMenuChoiceDoesNotCrash() {
        var result = CliRunner.runWithInput("abc\n5\n\n2\n", "--seed", SEED);

        assertThat(result.exitCode()).as(result.toString()).isZero();
        assertThat(result.stdout()).contains("Нет такого пункта меню").contains("До встречи!");
    }

    @Test
    @DisplayName("Можно сыграть несколько партий подряд без перезапуска")
    void severalGamesInARow() {
        var series = new GameSeries(Dictionary.load(), Long.parseLong(SEED));
        var firstAnswer = series.next().answer();
        var secondAnswer = series.next().answer();
        var wrongGuess = differentWord(secondAnswer);
        var input = "1\n" + firstAnswer + "\n1\n" + (wrongGuess + "\n").repeat(6) + "2\n";

        var result = CliRunner.runWithInput(input, "--seed", SEED);

        assertThat(result.exitCode()).as(result.toString()).isZero();
        assertThat(result.stdout())
                .contains("Победа! Слово угадано за 1 попытку")
                .contains("Неудача. Загаданное слово: " + secondAnswer);
    }

    @Test
    @DisplayName("Детерминированный режим даёт предсказуемый вывод для автопроверки")
    void deterministicModeProducesPredictableOutput() {
        var answer =
                new GameSeries(Dictionary.load(), Long.parseLong(SEED)).next().answer();
        var guess = differentWord(answer);
        var input = "1\n" + guess + "\n" + answer + "\n2\n";
        var expectedLine = symbols(answer, guess) + " " + guess;

        var first = CliRunner.runWithInput(input, "--seed", SEED);
        var second = CliRunner.runWithInput(input, "--seed", SEED);

        assertThat(first.stdout())
                .contains(expectedLine + System.lineSeparator() + "Осталось попыток: 5")
                .contains("Победа! Слово угадано за 2 попытки");
        assertThat(second.stdout()).isEqualTo(first.stdout());
    }

    private static String differentWord(String answer) {
        return "арбуз".equals(answer) ? "сорок" : "арбуз";
    }

    private static String symbols(String answer, String guess) {
        return LetterMatcher.match(answer, guess).stream()
                .map(LetterStatus::symbol)
                .collect(Collectors.joining());
    }
}
