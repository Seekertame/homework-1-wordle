package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Обработка ввода")
class ApplyGuessTest {

    private final Dictionary dictionary = new Dictionary(List.of("озеро", "арбуз", "берег"));
    private final GameSession session = new GameSession(dictionary, "озеро", 6);

    @Test
    @DisplayName("Принятая попытка попадает в историю")
    void acceptedGuessIsRecorded() {
        var result = Game.applyGuess(session, "арбуз");

        var attempt = new Attempt("арбуз", LetterMatcher.match("озеро", "арбуз"));
        assertThat(result).isEqualTo(new GuessResult.Accepted(attempt));
        assertThat(session.history()).containsExactly(attempt);
    }

    @Test
    @DisplayName("Буква ё заменяется на е")
    void letterYoIsReplaced() {
        var result = Game.applyGuess(session, "берёг");

        assertThat(result).isInstanceOf(GuessResult.Accepted.class);
        assertThat(session.history()).extracting(Attempt::guess).containsExactly("берег");
    }

    @Test
    @DisplayName("Пробелы по краям убираются")
    void spacesAreStripped() {
        var result = Game.applyGuess(session, "  арбуз ");

        assertThat(result).isInstanceOf(GuessResult.Accepted.class);
    }

    @Test
    @DisplayName("Латинские буквы отклоняются")
    void latinLettersAreRejected() {
        var result = Game.applyGuess(session, "ozero");

        assertThat(result).isEqualTo(new GuessResult.Rejected(GuessError.NOT_LETTERS));
    }
}
