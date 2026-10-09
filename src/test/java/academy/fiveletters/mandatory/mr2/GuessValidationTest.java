package academy.fiveletters.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.Constants;
import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.Game;
import academy.fiveletters.game.GameSession;
import academy.fiveletters.game.GuessError;
import academy.fiveletters.game.GuessResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Обязательные тесты: валидация ввода. */
@DisplayName("MR2. Валидация ввода")
class GuessValidationTest {

    private final GameSession session = new GameSession(Dictionary.load(), "озеро", Constants.DEFAULT_MAX_ATTEMPTS);

    @ParameterizedTest
    @ValueSource(strings = {"дом", "домики", ""})
    @DisplayName("Слово не из 5 букв отклоняется: \"{0}\"")
    void wordOfWrongLengthIsRejected(String guess) {
        var result = Game.applyGuess(session, guess);

        assertThat(result).isEqualTo(new GuessResult.Rejected(GuessError.WRONG_LENGTH));
    }

    @ParameterizedTest
    @ValueSource(strings = {"дом12", "дом!!", "до ма"})
    @DisplayName("Ввод с не-буквами отклоняется: \"{0}\"")
    void nonLetterInputIsRejected(String guess) {
        var result = Game.applyGuess(session, guess);

        assertThat(result).isEqualTo(new GuessResult.Rejected(GuessError.NOT_LETTERS));
    }

    @Test
    @DisplayName("Слово, которого нет в словаре, отклоняется")
    void wordOutsideDictionaryIsRejected() {
        var result = Game.applyGuess(session, "абвгд");

        assertThat(result).isEqualTo(new GuessResult.Rejected(GuessError.NOT_IN_DICTIONARY));
    }

    @Test
    @DisplayName("Некорректный ввод не тратит попытку")
    void invalidInputDoesNotConsumeAttempt() {
        Game.applyGuess(session, "дом");
        Game.applyGuess(session, "дом12");
        Game.applyGuess(session, "абвгд");

        assertThat(session.attemptsUsed()).isZero();
        assertThat(session.history()).isEmpty();
    }

    @Test
    @DisplayName("Ввод не зависит от регистра: \"ОЗЕРО\" и \"озеро\" обрабатываются одинаково")
    void inputIsCaseInsensitive() {
        var other = new GameSession(Dictionary.load(), "озеро", Constants.DEFAULT_MAX_ATTEMPTS);

        var upper = Game.applyGuess(session, "ОЗЕРО");
        var lower = Game.applyGuess(other, "озеро");

        assertThat(upper).isEqualTo(lower);
    }
}
