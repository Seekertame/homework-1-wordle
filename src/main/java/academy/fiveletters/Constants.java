package academy.fiveletters;

/** Константы игры. Собраны в одном месте, чтобы их было легко найти и поменять. */
public final class Constants {

    public static final int WORD_LENGTH = 5;

    public static final int DEFAULT_MAX_ATTEMPTS = 6;

    /** Только строчные русские буквы. Буква ё в этот диапазон не входит. */
    public static final String RUSSIAN_LETTERS = "[а-я]+";

    public static final String DICTIONARY_RESOURCE = "/dictionary.txt";

    public static final String EASY_WORDS_RESOURCE = "/levels/easy.txt";

    /** Файл категории лежит в этой папке и называется {@code <id>.txt}. */
    public static final String CATEGORY_RESOURCE_DIR = "/categories/";

    /** Невидимый символ BOM в начале файла. Блокнот и PowerShell 5.1 добавляют его, когда сохраняют файл в UTF-8. */
    public static final String UTF8_BOM = "\uFEFF";

    /** Папка, куда флаг {@code --record} сохраняет повторы партий. */
    public static final String REPLAY_DIR = "replays";

    /** Строка файла повтора с seed партии, например {@code seed 42}. */
    public static final String REPLAY_SEED_KEY = "seed";

    public static final String REPLAY_LEVEL_KEY = "level";

    public static final String REPLAY_CATEGORY_KEY = "category";

    /** Что ввести во время партии, чтобы получить подсказку. */
    public static final String HINT_COMMAND = "?";

    /** Коды ANSI: терминал не печатает их, а меняет цвет текста. Этот код возвращает обычный цвет. */
    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_BLACK_TEXT = "\u001B[30m";

    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";

    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";

    public static final String ANSI_GRAY_BACKGROUND = "\u001B[100m";

    public static final String USAGE = "Запуск: [--seed число] [--show-answer] [--level easy|hard] "
            + "[--category animals|food|nature|home|clothes|people] [--color] [--record] [--replay файл]";

    /** Код выхода при неверных аргументах запуска. Код 0 значит, что программа завершилась успешно. */
    public static final int INVALID_ARGUMENTS_EXIT_CODE = 2;

    private Constants() {}
}
