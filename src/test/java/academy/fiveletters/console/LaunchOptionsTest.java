package academy.fiveletters.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Аргументы запуска")
class LaunchOptionsTest {

    @Test
    @DisplayName("Без аргументов seed не задан и ответ скрыт")
    void noArguments() {
        assertThat(LaunchOptions.parse()).isEqualTo(new LaunchOptions(null, false));
    }

    @Test
    @DisplayName("Флаги можно указывать в любом порядке")
    void flagsInAnyOrder() {
        assertThat(LaunchOptions.parse("--seed", "42", "--show-answer")).isEqualTo(new LaunchOptions(42L, true));
        assertThat(LaunchOptions.parse("--show-answer", "--seed", "42")).isEqualTo(new LaunchOptions(42L, true));
        assertThat(LaunchOptions.parse("--show-answer")).isEqualTo(new LaunchOptions(null, true));
    }

    @Test
    @DisplayName("seed читается из аргументов")
    void seedIsParsed() {
        assertThat(LaunchOptions.parse("--seed", "42").seed()).isEqualTo(42L);
        assertThat(LaunchOptions.parse("--seed", "-7").seed()).isEqualTo(-7L);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"--seed abc", "--seed", "--answer 42", "--seed 1 2", "42", "--show-answer yes"})
    @DisplayName("Неверные аргументы вызывают ошибку")
    void invalidArgumentsAreRejected(String args) {
        assertThatThrownBy(() -> LaunchOptions.parse(args.split(" "))).isInstanceOf(IllegalArgumentException.class);
    }
}
