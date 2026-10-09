package academy.fiveletters.game;

import static academy.fiveletters.game.LetterStatus.ABSENT;
import static academy.fiveletters.game.LetterStatus.CORRECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Подсказка")
class HintTest {

    private final Dictionary dictionary = new Dictionary(List.of("озеро", "арбуз"));
    private final GameSession session = new GameSession(dictionary, "озеро", 6);

    @Test
    @DisplayName("Без попыток открывается первая буква")
    void firstLetterWithoutAttempts() {
        assertThat(Game.hint(session)).contains(new Hint(0, 'о'));
        assertThat(session.hintUsed()).isTrue();
    }

    @Test
    @DisplayName("Найденные места пропускаются")
    void foundPositionsAreSkipped() {
        session.recordAttempt(new Attempt("арбуз", List.of(CORRECT, ABSENT, ABSENT, ABSENT, ABSENT)));

        assertThat(Game.hint(session)).contains(new Hint(1, 'з'));
    }

    @Test
    @DisplayName("Подсказка не тратит попытку")
    void hintDoesNotUseAttempt() {
        Game.hint(session);

        assertThat(session.attemptsUsed()).isZero();
    }

    @Test
    @DisplayName("Вторая подсказка вызывает ошибку")
    void secondHintIsRejected() {
        Game.hint(session);

        assertThatThrownBy(() -> Game.hint(session)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Если все места найдены, подсказки нет и она не расходуется")
    void nothingToRevealWhenAllFound() {
        session.recordAttempt(new Attempt("арбуз", List.of(CORRECT, CORRECT, CORRECT, CORRECT, CORRECT)));

        assertThat(Game.hint(session)).isEmpty();
        assertThat(session.hintUsed()).isFalse();
    }

    @Test
    @DisplayName("В завершённой партии подсказки нет")
    void finishedGameHasNoHint() {
        Game.applyGuess(session, "озеро");

        assertThatThrownBy(() -> Game.hint(session)).isInstanceOf(IllegalStateException.class);
    }
}
