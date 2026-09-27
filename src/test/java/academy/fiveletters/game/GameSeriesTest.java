package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Серия партий")
class GameSeriesTest {

    private final Dictionary dictionary = Dictionary.load();

    @Test
    @DisplayName("Одинаковый seed даёт одинаковую серию слов")
    void sameSeedGivesSameSeries() {
        assertThat(answers(new GameSeries(dictionary, 42), 5)).isEqualTo(answers(new GameSeries(dictionary, 42), 5));
    }

    @Test
    @DisplayName("В серии встречаются разные слова")
    void gamesInSeriesDiffer() {
        assertThat(new HashSet<>(answers(new GameSeries(dictionary, 42), 10))).hasSizeGreaterThan(1);
    }

    private static List<String> answers(GameSeries series, int count) {
        var answers = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            answers.add(series.next().answer());
        }
        return answers;
    }
}
