package academy.fiveletters.console;

import static academy.fiveletters.Constants.REPLAY_CATEGORY_KEY;
import static academy.fiveletters.Constants.REPLAY_LEVEL_KEY;
import static academy.fiveletters.Constants.REPLAY_SEED_KEY;
import static academy.fiveletters.Constants.UTF8_BOM;

import academy.fiveletters.game.Category;
import academy.fiveletters.game.Level;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Запись партии для повтора: seed партии, уровень или категория и всё, что игрок ввёл.
 *
 * <p>В файле сначала идут строки {@code seed}, {@code level} и {@code category}, потом ввод игрока по одной строке.
 * Пустые строки, строки с {@code #} и невидимый BOM в начале файла пропускаются.
 *
 * @param seed seed этой партии, а не всего запуска
 * @param level уровень или {@code null}, если его не было
 * @param category категория или {@code null}, если её не было
 * @param inputs всё, что игрок ввёл за партию, включая подсказки и отклонённые слова
 */
public record Replay(
        long seed, @Nullable Level level, @Nullable Category category, List<String> inputs) {

    public Replay {
        inputs = List.copyOf(inputs);
    }

    /**
     * Сохраняет запись в папку. Файл называется по seed партии, например {@code game_42.txt}.
     *
     * @return путь к сохранённому файлу
     */
    public Path save(Path dir) throws IOException {
        Files.createDirectories(dir);
        var file = dir.resolve("game_" + seed + ".txt");
        Files.write(file, toLines(), StandardCharsets.UTF_8);
        return file;
    }

    /**
     * Читает запись из файла в UTF-8.
     *
     * @throws IllegalArgumentException если в файле нет seed или он записан неверно
     */
    public static Replay load(Path file) throws IOException {
        return fromLines(Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    List<String> toLines() {
        var lines = new ArrayList<String>();
        lines.add("# Повтор партии «5 букв». Запуск: --replay <путь к этому файлу>");
        lines.add(REPLAY_SEED_KEY + " " + seed);
        if (level != null) {
            lines.add(REPLAY_LEVEL_KEY + " " + level.id());
        }
        if (category != null) {
            lines.add(REPLAY_CATEGORY_KEY + " " + category.id());
        }
        lines.addAll(inputs);
        return lines;
    }

    /**
     * Разбирает строки файла повтора. Строки {@code seed}, {@code level} и {@code category} читаются только до первой
     * строки ввода. Иначе слово игрока, похожее на них, приняли бы за настройку.
     */
    static Replay fromLines(List<String> lines) {
        var meaningful = lines.stream()
                .map(line -> line.replace(UTF8_BOM, "").strip())
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .toList();

        Long seed = null;
        Level level = null;
        Category category = null;
        int firstInput = 0;
        while (firstInput < meaningful.size() && isSetting(meaningful.get(firstInput))) {
            var line = meaningful.get(firstInput);
            if (line.startsWith(REPLAY_SEED_KEY + " ")) {
                seed = parseSeed(valueOf(line));
            } else if (line.startsWith(REPLAY_LEVEL_KEY + " ")) {
                level = Level.fromId(valueOf(line));
            } else {
                category = Category.fromId(valueOf(line));
            }
            firstInput++;
        }

        if (seed == null) {
            throw new IllegalArgumentException("В файле повтора нет строки «seed число»");
        }
        if (level != null && category != null) {
            throw new IllegalArgumentException("В файле повтора указаны сразу уровень и категория");
        }
        return new Replay(seed, level, category, meaningful.subList(firstInput, meaningful.size()));
    }

    private static boolean isSetting(String line) {
        return line.startsWith(REPLAY_SEED_KEY + " ")
                || line.startsWith(REPLAY_LEVEL_KEY + " ")
                || line.startsWith(REPLAY_CATEGORY_KEY + " ");
    }

    private static String valueOf(String line) {
        return line.substring(line.indexOf(' ') + 1).strip();
    }

    private static long parseSeed(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("В файле повтора seed не число: " + value, e);
        }
    }
}
