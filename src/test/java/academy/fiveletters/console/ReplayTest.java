package academy.fiveletters.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.fiveletters.game.Category;
import academy.fiveletters.game.Level;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("Файл повтора")
class ReplayTest {

    @Test
    @DisplayName("Запись сохраняется и читается без потерь")
    void saveAndLoad(@TempDir Path dir) throws IOException {
        var replay = new Replay(-42, Level.EASY, null, List.of("арбуз", "?", "озеро"));

        var file = replay.save(dir);

        assertThat(file.getFileName()).hasToString("game_-42.txt");
        assertThat(Replay.load(file)).isEqualTo(replay);
    }

    @Test
    @DisplayName("Категория сохраняется в файл")
    void categoryIsSaved() {
        var replay = new Replay(7, null, Category.FOOD, List.of("арбуз"));

        assertThat(replay.toLines()).contains("seed 7", "category food").doesNotContain("level easy");
        assertThat(Replay.fromLines(replay.toLines())).isEqualTo(replay);
    }

    @Test
    @DisplayName("BOM, комментарии и пустые строки пропускаются")
    void bomCommentsAndBlankLinesAreSkipped() {
        var lines = List.of("\uFEFF# комментарий", "", "seed 42", "  ", "арбуз", "озеро");

        assertThat(Replay.fromLines(lines)).isEqualTo(new Replay(42, null, null, List.of("арбуз", "озеро")));
    }

    @Test
    @DisplayName("Настройки после первой строки ввода считаются вводом")
    void settingsAfterInputAreInputs() {
        var lines = List.of("seed 42", "арбуз", "seed 1");

        assertThat(Replay.fromLines(lines).inputs()).containsExactly("арбуз", "seed 1");
        assertThat(Replay.fromLines(lines).seed()).isEqualTo(42);
    }

    @Test
    @DisplayName("Файл без seed вызывает ошибку")
    void missingSeedIsRejected() {
        assertThatThrownBy(() -> Replay.fromLines(List.of("арбуз")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("seed");
    }

    @Test
    @DisplayName("Неверный seed вызывает ошибку")
    void invalidSeedIsRejected() {
        assertThatThrownBy(() -> Replay.fromLines(List.of("seed abc"))).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Уровень и категория вместе вызывают ошибку")
    void levelAndCategoryAreRejected() {
        assertThatThrownBy(() -> Replay.fromLines(List.of("seed 1", "level easy", "category food")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
