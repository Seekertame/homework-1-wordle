package academy.fiveletters.game;

import static academy.fiveletters.Constants.EASY_WORDS_RESOURCE;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Уровень сложности. Определяет, из каких слов загадывается ответ. Ввод по-прежнему проверяется по всему словарю.
 *
 * <p>Сложность слова зависит от того, насколько оно распространено. Распространённые слова лежат в файле
 * {@code src/main/resources/levels/easy.txt}.
 */
public enum Level {
    /** Распространённые слова из списка {@code levels/easy.txt}. */
    EASY("easy", "лёгкий"),
    /** Все остальные слова словаря, менее распространённые. */
    HARD("hard", "сложный");

    private final String id;
    private final String title;

    Level(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    /**
     * Отбирает из словаря слова этого уровня.
     *
     * @throws IllegalStateException если в списке распространённых слов есть слово не из общего словаря
     */
    public Dictionary answers(Dictionary dictionary) {
        var easyWords = Dictionary.load(EASY_WORDS_RESOURCE);
        if (!dictionary.containsAll(easyWords)) {
            throw new IllegalStateException("В списке лёгких слов есть слова, которых нет в общем словаре");
        }

        return switch (this) {
            case EASY -> easyWords;
            case HARD -> dictionary.filter(word -> !easyWords.contains(word));
        };
    }

    /**
     * Находит уровень по названию из аргумента {@code --level}.
     *
     * @throws IllegalArgumentException если такого уровня нет
     */
    public static Level fromId(String id) {
        for (var level : values()) {
            if (level.id.equals(id)) {
                return level;
            }
        }
        var known = Arrays.stream(values()).map(Level::id).collect(Collectors.joining(", "));
        throw new IllegalArgumentException("Неизвестный уровень: " + id + ". Есть: " + known);
    }
}
