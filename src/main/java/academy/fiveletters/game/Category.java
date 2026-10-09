package academy.fiveletters.game;

import static academy.fiveletters.Constants.CATEGORY_RESOURCE_DIR;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Категория слов. Определяет, из каких слов загадывается ответ. Ввод по-прежнему проверяется по всему словарю.
 *
 * <p>Слова каждой категории лежат в своём файле {@code src/main/resources/categories/<id>.txt}.
 */
public enum Category {
    ANIMALS("animals", "Животные"),
    FOOD("food", "Еда"),
    NATURE("nature", "Природа"),
    HOME("home", "Дом и быт"),
    CLOTHES("clothes", "Одежда"),
    PEOPLE("people", "Люди и профессии");

    private final String id;
    private final String title;

    Category(String id, String title) {
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
     * Загружает слова категории и проверяет, что все они есть в общем словаре.
     *
     * @throws IllegalStateException если в категории есть слово не из общего словаря
     */
    public Dictionary answers(Dictionary dictionary) {
        var words = Dictionary.load(CATEGORY_RESOURCE_DIR + id + ".txt");
        if (!dictionary.containsAll(words)) {
            throw new IllegalStateException("В категории " + id + " есть слова, которых нет в общем словаре");
        }
        return words;
    }

    /**
     * Находит категорию по названию из аргумента {@code --category}.
     *
     * @throws IllegalArgumentException если такой категории нет
     */
    public static Category fromId(String id) {
        for (var category : values()) {
            if (category.id.equals(id)) {
                return category;
            }
        }
        var known = Arrays.stream(values()).map(Category::id).collect(Collectors.joining(", "));
        throw new IllegalArgumentException("Неизвестная категория: " + id + ". Есть: " + known);
    }
}
