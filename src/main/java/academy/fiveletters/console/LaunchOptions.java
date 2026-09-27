package academy.fiveletters.console;

import org.jspecify.annotations.Nullable;

public record LaunchOptions(@Nullable Long seed, boolean showAnswer) {

    public static final String USAGE = "Запуск: [--seed число] [--show-answer]";

    public static LaunchOptions parse(String... args) {
        Long seed = null;
        boolean showAnswer = false;

        int i = 0;
        while (i < args.length) {
            switch (args[i]) {
                case "--seed" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("Нет значения для --seed");
                    }
                    seed = parseSeed(args[i + 1]);
                    i += 2;
                }
                case "--show-answer" -> {
                    showAnswer = true;
                    i++;
                }
                default -> throw new IllegalArgumentException("Неизвестный аргумент: " + args[i]);
            }
        }

        return new LaunchOptions(seed, showAnswer);
    }

    private static long parseSeed(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("seed должен быть целым числом, передано: " + value, e);
        }
    }
}
