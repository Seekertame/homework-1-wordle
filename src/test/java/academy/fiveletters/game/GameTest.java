package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Начало партии")
class GameTest {

    private final Dictionary dictionary = new Dictionary(List.of("озеро", "арбуз", "сорок"));

    @Test
    @DisplayName("Слово выбирается из словаря")
    void answerIsTakenFromDictionary() {
        var session = Game.startGame(dictionary, 6, 7);

        assertThat(dictionary.contains(session.answer())).isTrue();
    }

    @Test
    @DisplayName("Партия получает заданное число попыток")
    void newGameUsesGivenSettings() {
        var session = Game.startGame(dictionary, 4, 7);

        assertThat(session.maxAttempts()).isEqualTo(4);
        assertThat(session.status()).isEqualTo(GameStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("Неверное число попыток вызывает ошибку")
    void invalidMaxAttemptsIsRejected() {
        assertThatThrownBy(() -> Game.startGame(dictionary, 0, 7)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Словарь из одного слова всегда даёт это слово")
    void singleWordDictionaryAlwaysGivesThatWord() {
        var single = new Dictionary(List.of("озеро"));

        assertThat(Game.startGame(single, 6, 1).answer()).isEqualTo("озеро");
        assertThat(Game.startGame(single, 6, 999).answer()).isEqualTo("озеро");
    }
}
