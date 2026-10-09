package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Статистика за запуск")
class StatisticsTest {

    private final Dictionary dictionary = new Dictionary(List.of("озеро", "арбуз"));

    @Test
    @DisplayName("Новая статистика пустая")
    void newStatisticsIsEmpty() {
        var statistics = new Statistics();

        assertThat(statistics.played()).isZero();
        assertThat(statistics.won()).isZero();
        assertThat(statistics.winPercent()).isZero();
    }

    @Test
    @DisplayName("Победа и поражение учитываются")
    void winAndLossAreCounted() {
        var statistics = new Statistics();

        statistics.record(finished("озеро"));
        statistics.record(finished("арбуз"));

        assertThat(statistics.played()).isEqualTo(2);
        assertThat(statistics.won()).isEqualTo(1);
        assertThat(statistics.winPercent()).isEqualTo(50);
    }

    @Test
    @DisplayName("Процент побед округляется вниз")
    void winPercentIsRoundedDown() {
        var statistics = new Statistics();

        statistics.record(finished("озеро"));
        statistics.record(finished("озеро"));
        statistics.record(finished("арбуз"));

        assertThat(statistics.winPercent()).isEqualTo(66);
    }

    @Test
    @DisplayName("Незавершённую партию учесть нельзя")
    void unfinishedGameIsRejected() {
        var session = new GameSession(dictionary, "озеро", 6);

        assertThatThrownBy(() -> new Statistics().record(session)).isInstanceOf(IllegalArgumentException.class);
    }

    private GameSession finished(String guess) {
        var session = new GameSession(dictionary, "озеро", 1);
        Game.applyGuess(session, guess);
        return session;
    }
}
