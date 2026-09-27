package academy.fiveletters.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.Game;
import academy.fiveletters.game.GameSession;
import academy.fiveletters.game.GameStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: завершение партии. */
@DisplayName("MR2. Победа и поражение")
class GameOutcomeTest {

    private final GameSession session = new GameSession(Dictionary.load(), "озеро", Game.DEFAULT_MAX_ATTEMPTS);

    @Test
    @DisplayName("Угаданное слово переводит сессию в статус WIN")
    void correctGuessWinsTheGame() {
        Game.applyGuess(session, "озеро");

        assertThat(session.status()).isEqualTo(GameStatus.WIN);
    }

    @Test
    @DisplayName("После 6 неудачных попыток сессия переходит в статус LOSE")
    void sixFailedAttemptsLoseTheGame() {
        for (int i = 0; i < 5; i++) {
            Game.applyGuess(session, "арбуз");
        }
        assertThat(session.status()).isEqualTo(GameStatus.IN_PROGRESS);

        Game.applyGuess(session, "арбуз");

        assertThat(session.status()).isEqualTo(GameStatus.LOSE);
    }

    @Test
    @DisplayName("При поражении показывается загаданное слово")
    void answerIsRevealedOnLoss() {
        for (int i = 0; i < 6; i++) {
            Game.applyGuess(session, "арбуз");
        }

        assertThat(session.status()).isEqualTo(GameStatus.LOSE);
        assertThat(session.answer()).isEqualTo("озеро");
    }

    @Test
    @DisplayName("Завершённая партия больше не принимает попытки")
    void finishedGameRejectsFurtherGuesses() {
        Game.applyGuess(session, "озеро");

        assertThatThrownBy(() -> Game.applyGuess(session, "арбуз")).isInstanceOf(IllegalStateException.class);
        assertThat(session.attemptsUsed()).isEqualTo(1);
    }
}
