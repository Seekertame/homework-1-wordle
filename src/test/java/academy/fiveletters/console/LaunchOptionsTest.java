package academy.fiveletters.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.fiveletters.game.Category;
import academy.fiveletters.game.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Аргументы запуска")
class LaunchOptionsTest {

    @Test
    @DisplayName("Без аргументов ничего не задано")
    void noArguments() {
        assertThat(LaunchOptions.parse()).isEqualTo(new LaunchOptions(null, false, null, null, false, false, null));
    }

    @Test
    @DisplayName("Флаги можно указывать в любом порядке")
    void flagsInAnyOrder() {
        var expected = new LaunchOptions(42L, true, null, null, true, true, null);

        assertThat(LaunchOptions.parse("--seed", "42", "--show-answer", "--color", "--record"))
                .isEqualTo(expected);
        assertThat(LaunchOptions.parse("--record", "--color", "--show-answer", "--seed", "42"))
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("seed читается из аргументов")
    void seedIsParsed() {
        assertThat(LaunchOptions.parse("--seed", "42").seed()).isEqualTo(42L);
        assertThat(LaunchOptions.parse("--seed", "-7").seed()).isEqualTo(-7L);
    }

    @Test
    @DisplayName("Уровень читается из аргументов")
    void levelIsParsed() {
        assertThat(LaunchOptions.parse("--level", "easy").level()).isEqualTo(Level.EASY);
        assertThat(LaunchOptions.parse("--seed", "1", "--level", "hard").level())
                .isEqualTo(Level.HARD);
    }

    @Test
    @DisplayName("Категория читается из аргументов")
    void categoryIsParsed() {
        assertThat(LaunchOptions.parse("--category", "animals").category()).isEqualTo(Category.ANIMALS);
    }

    @Test
    @DisplayName("Файл повтора читается из аргументов")
    void replayIsParsed() {
        var options = LaunchOptions.parse("--replay", "game.txt", "--color", "--show-answer");

        assertThat(options.replay()).isEqualTo("game.txt");
        assertThat(options.color()).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "--seed abc",
                "--seed",
                "--answer 42",
                "--seed 1 2",
                "42",
                "--show-answer yes",
                "--record yes",
                "--level",
                "--level medium",
                "--category cars",
                "--level easy --category food",
                "--replay",
                "--replay game.txt --seed 1",
                "--replay game.txt --level easy",
                "--replay game.txt --category food",
                "--replay game.txt --record"
            })
    @DisplayName("Неверные аргументы вызывают ошибку")
    void invalidArgumentsAreRejected(String args) {
        assertThatThrownBy(() -> LaunchOptions.parse(args.split(" "))).isInstanceOf(IllegalArgumentException.class);
    }
}
