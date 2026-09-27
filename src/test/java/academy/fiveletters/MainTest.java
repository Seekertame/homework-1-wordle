package academy.fiveletters;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.support.CliRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Запуск программы")
class MainTest {

    @Test
    @DisplayName("Неверный seed завершает программу с ошибкой")
    void invalidSeedFailsWithMessage() {
        var result = CliRunner.run("--seed", "abc");

        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.stderr()).contains("seed должен быть целым числом").contains("Запуск: [--seed число]");
        assertThat(result.stdout()).isEmpty();
    }
}
