package academy.fiveletters.console;

import academy.fiveletters.game.Category;
import academy.fiveletters.game.Level;
import org.jspecify.annotations.Nullable;

/**
 * Аргументы запуска программы.
 *
 * @param seed seed серии партий или {@code null}, если он не задан
 * @param showAnswer печатать ли загаданное слово в начале каждой партии
 * @param level уровень сложности или {@code null}, если он не задан
 * @param category категория слов или {@code null}, если она не задана
 * @param color раскрашивать ли буквы попытки
 * @param record сохранять ли каждую партию для повтора
 * @param replay файл повтора или {@code null}, если повтора нет
 */
public record LaunchOptions(
        @Nullable Long seed,
        boolean showAnswer,
        @Nullable Level level,
        @Nullable Category category,
        boolean color,
        boolean record,
        @Nullable String replay) {

    /**
     * Разбирает аргументы командной строки. Флаги можно указывать в любом порядке.
     *
     * @throws IllegalArgumentException если аргумент неизвестен, у флага нет значения, значение неверное, указаны сразу
     *     {@code --level} и {@code --category} или вместе с {@code --replay} указаны настройки, которые берутся из
     *     файла повтора
     */
    public static LaunchOptions parse(String... args) {
        Long seed = null;
        boolean showAnswer = false;
        Level level = null;
        Category category = null;
        boolean color = false;
        boolean record = false;
        String replay = null;

        int i = 0;
        while (i < args.length) {
            switch (args[i]) {
                case "--seed" -> {
                    seed = parseSeed(valueAfter(args, i));
                    i += 2;
                }
                case "--show-answer" -> {
                    showAnswer = true;
                    i++;
                }
                case "--level" -> {
                    level = Level.fromId(valueAfter(args, i));
                    i += 2;
                }
                case "--category" -> {
                    category = Category.fromId(valueAfter(args, i));
                    i += 2;
                }
                case "--color" -> {
                    color = true;
                    i++;
                }
                case "--record" -> {
                    record = true;
                    i++;
                }
                case "--replay" -> {
                    replay = valueAfter(args, i);
                    i += 2;
                }
                default -> throw new IllegalArgumentException("Неизвестный аргумент: " + args[i]);
            }
        }

        var options = new LaunchOptions(seed, showAnswer, level, category, color, record, replay);
        options.checkCombination();
        return options;
    }

    private void checkCombination() {
        if (level != null && category != null) {
            throw new IllegalArgumentException("Нужно выбрать что-то одно: --level или --category");
        }
        if (replay != null && (seed != null || level != null || category != null || record)) {
            throw new IllegalArgumentException(
                    "С --replay не указывают --seed, --level, --category и --record: они берутся из файла повтора");
        }
    }

    private static String valueAfter(String[] args, int flagIndex) {
        if (flagIndex + 1 >= args.length) {
            throw new IllegalArgumentException("Нет значения для " + args[flagIndex]);
        }
        return args[flagIndex + 1];
    }

    private static long parseSeed(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("seed должен быть целым числом, передано: " + value, e);
        }
    }
}
