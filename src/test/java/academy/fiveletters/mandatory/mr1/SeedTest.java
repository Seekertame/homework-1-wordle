package academy.fiveletters.mandatory.mr1;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.Game;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: одинаковый seed обязан давать одинаковое загаданное слово. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {

    private final Dictionary dictionary = Dictionary.load();

    @Test
    @DisplayName("Одинаковый seed даёт одинаковое загаданное слово")
    void sameSeedProducesSameAnswer() {
        var first = Game.startGame(dictionary, Game.DEFAULT_MAX_ATTEMPTS, 42);
        var second = Game.startGame(dictionary, Game.DEFAULT_MAX_ATTEMPTS, 42);

        assertThat(second.answer()).isEqualTo(first.answer());
    }

    @Test
    @DisplayName("Разные seed'ы дают разные слова хотя бы иногда")
    void differentSeedsProduceDifferentAnswers() {
        var answers = LongStream.range(0, 20)
                .mapToObj(seed -> Game.startGame(dictionary, Game.DEFAULT_MAX_ATTEMPTS, seed)
                        .answer())
                .collect(toSet());

        assertThat(answers).hasSizeGreaterThan(1);
    }
}
