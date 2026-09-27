package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Партия")
class GameSessionTest {

    private final Dictionary dictionary = new Dictionary(List.of("озеро", "арбуз", "сорок"));

    @Test
    @DisplayName("Новая партия начинается без попыток")
    void newSessionIsInProgress() {
        var session = new GameSession(dictionary, "озеро", 6);

        assertThat(session.status()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(session.attemptsUsed()).isZero();
        assertThat(session.history()).isEmpty();
        assertThat(session.answer()).isEqualTo("озеро");
        assertThat(session.maxAttempts()).isEqualTo(6);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("maxAttempts должно быть больше нуля: {0}")
    void nonPositiveMaxAttemptsIsRejected(int maxAttempts) {
        assertThatThrownBy(() -> new GameSession(dictionary, "озеро", maxAttempts))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(maxAttempts));
    }

    @Test
    @DisplayName("Загаданное слово должно быть из словаря")
    void answerOutsideDictionaryIsRejected() {
        assertThatThrownBy(() -> new GameSession(dictionary, "кошка", 6))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("кошка");
    }

    @Test
    @DisplayName("Неверная попытка не завершает партию")
    void wrongGuessKeepsGameInProgress() {
        var session = new GameSession(dictionary, "озеро", 6);

        session.recordAttempt("арбуз");

        assertThat(session.status()).isEqualTo(GameStatus.IN_PROGRESS);
        assertThat(session.attemptsUsed()).isEqualTo(1);
        assertThat(session.history()).containsExactly("арбуз");
    }

    @Test
    @DisplayName("Угаданное слово приводит к победе")
    void correctGuessWins() {
        var session = new GameSession(dictionary, "озеро", 6);

        session.recordAttempt("озеро");

        assertThat(session.status()).isEqualTo(GameStatus.WIN);
    }

    @Test
    @DisplayName("Партия проиграна, когда кончились попытки")
    void runningOutOfAttemptsLoses() {
        var session = new GameSession(dictionary, "озеро", 2);

        session.recordAttempt("арбуз");
        session.recordAttempt("сорок");

        assertThat(session.status()).isEqualTo(GameStatus.LOSE);
    }

    @Test
    @DisplayName("Слово угадано на последней попытке и это победа")
    void correctGuessOnLastAttemptWins() {
        var session = new GameSession(dictionary, "озеро", 2);

        session.recordAttempt("арбуз");
        session.recordAttempt("озеро");

        assertThat(session.status()).isEqualTo(GameStatus.WIN);
    }

    @Test
    @DisplayName("Выигранная партия не принимает новые попытки")
    void wonSessionRejectsAttempts() {
        var session = new GameSession(dictionary, "озеро", 6);
        session.recordAttempt("озеро");

        assertThatThrownBy(() -> session.recordAttempt("арбуз")).isInstanceOf(IllegalStateException.class);
        assertThat(session.attemptsUsed()).isEqualTo(1);
    }

    @Test
    @DisplayName("Проигранная партия не принимает новые попытки")
    void lostSessionRejectsAttempts() {
        var session = new GameSession(dictionary, "озеро", 1);
        session.recordAttempt("арбуз");

        assertThatThrownBy(() -> session.recordAttempt("озеро")).isInstanceOf(IllegalStateException.class);
        assertThat(session.status()).isEqualTo(GameStatus.LOSE);
    }

    @Test
    @DisplayName("Историю нельзя изменить снаружи")
    void historyCannotBeModifiedFromOutside() {
        var session = new GameSession(dictionary, "озеро", 6);

        assertThatThrownBy(() -> session.history().add("арбуз")).isInstanceOf(UnsupportedOperationException.class);
        assertThat(session.attemptsUsed()).isZero();
    }
}
