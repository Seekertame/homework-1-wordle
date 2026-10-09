package academy.fiveletters;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.game.Category;
import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.GameSeries;
import academy.fiveletters.support.CliRunner;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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

    @Test
    @DisplayName("С категорией программа сообщает её и загадывает слово из неё")
    void categoryIsAnnouncedAndUsed() {
        var animals = Category.ANIMALS.answers(Dictionary.load());

        var result = CliRunner.runWithInput("1\n", "--seed", "42", "--category", "animals", "--show-answer");

        assertThat(result.exitCode()).as(result.toString()).isZero();
        assertThat(result.stdout()).contains("Категория: Животные");
        var answer = result.stdout()
                .lines()
                .filter(line -> line.startsWith("Загадано: "))
                .findFirst()
                .orElseThrow()
                .substring("Загадано: ".length());
        assertThat(animals.contains(answer)).isTrue();
    }

    @Test
    @DisplayName("С уровнем программа сообщает его")
    void levelIsAnnounced() {
        var result = CliRunner.runWithInput("2\n", "--level", "easy");

        assertThat(result.exitCode()).as(result.toString()).isZero();
        assertThat(result.stdout()).contains("Уровень: лёгкий");
    }

    @Test
    @DisplayName("Партия с флагом записи сохраняется, и её можно повторить")
    void recordedGameCanBeReplayed(@TempDir Path dir) throws IOException {
        var series = new GameSeries(Dictionary.load(), 42);
        series.next();
        var second = series.next().answer();
        var first = new GameSeries(Dictionary.load(), 42).next().answer();
        var guess = "арбуз".equals(second) ? "сорок" : "арбуз";
        var input = "1\n" + first + "\n1\n" + guess + "\n" + second + "\n2\n";

        var played = CliRunner.run(dir, input, "--seed", "42", "--record");

        assertThat(played.exitCode()).as(played.toString()).isZero();
        var file = dir.resolve("replays").resolve("game_" + series.lastSeed() + ".txt");
        assertThat(file).exists();
        assertThat(played.stdout())
                .contains("Повтор сохранён: "
                        + Path.of("replays", file.getFileName().toString()));

        var replayed = CliRunner.run(dir, null, "--replay", "replays/" + file.getFileName());

        assertThat(replayed.exitCode()).as(replayed.toString()).isZero();
        assertThat(replayed.stdout())
                .contains("Повтор партии")
                .contains("Победа! Слово угадано за 2 попытки")
                .doesNotContain("1. Новая игра");
    }

    @Test
    @DisplayName("Повтор читает файл, написанный вручную, с BOM от Блокнота и PowerShell")
    void replayReadsHandWrittenFileWithBom(@TempDir Path dir) throws IOException {
        var answer = new GameSeries(Dictionary.load(), 42).next().answer();
        var seed = firstGameSeed(42);
        Files.writeString(dir.resolve("game.txt"), "\uFEFFseed " + seed + "\n" + answer + "\n", StandardCharsets.UTF_8);

        var result = CliRunner.run(dir, null, "--replay", "game.txt");

        assertThat(result.exitCode()).as(result.toString()).isZero();
        assertThat(result.stdout()).contains("Победа! Слово угадано за 1 попытку");
    }

    @Test
    @DisplayName("Повтор без файла сообщает, где искал файл")
    void replayWithMissingFileFails(@TempDir Path dir) {
        var result = CliRunner.run(dir, null, "--replay", "missing.txt");

        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.stderr()).contains("файл не найден").contains("missing.txt");
    }

    @Test
    @DisplayName("Повтор из файла без seed завершается с ошибкой")
    void replayWithoutSeedFails(@TempDir Path dir) throws IOException {
        Files.writeString(dir.resolve("game.txt"), "арбуз\n", StandardCharsets.UTF_8);

        var result = CliRunner.run(dir, null, "--replay", "game.txt");

        assertThat(result.exitCode()).isEqualTo(2);
        assertThat(result.stderr()).contains("нет строки «seed число»");
    }

    private static long firstGameSeed(long runSeed) {
        var series = new GameSeries(Dictionary.load(), runSeed);
        series.next();
        return series.lastSeed();
    }
}
