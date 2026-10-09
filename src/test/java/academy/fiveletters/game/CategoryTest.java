package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.fiveletters.Constants;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Категории слов")
class CategoryTest {

    private final Dictionary dictionary = Dictionary.load();

    @ParameterizedTest(name = "{0}")
    @EnumSource(Category.class)
    @DisplayName("Слова категории загружаются, и все они есть в общем словаре")
    void categoryWordsAreInDictionary(Category category) {
        var answers = category.answers(dictionary);

        assertThat(answers.size()).isGreaterThanOrEqualTo(10);
        assertThat(dictionary.containsAll(answers)).isTrue();
    }

    @Test
    @DisplayName("Слово не повторяется в разных категориях")
    void categoriesDoNotOverlap() {
        var seen = new HashSet<String>();

        for (var category : Category.values()) {
            for (var word : category.answers(dictionary).words()) {
                assertThat(seen.add(word))
                        .as("слово %s уже есть в другой категории", word)
                        .isTrue();
            }
        }
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Category.class)
    @DisplayName("Партия в категории загадывает слово этой категории")
    void gameUsesCategoryWords(Category category) {
        var answers = category.answers(dictionary);

        var session = new GameSeries(dictionary, answers, 42).next();

        assertThat(answers.contains(session.answer())).isTrue();
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(Category.class)
    @DisplayName("Категория находится по названию и есть в подсказке запуска")
    void categoryIsFoundById(Category category) {
        assertThat(Category.fromId(category.id())).isEqualTo(category);
        assertThat(Constants.USAGE).contains(category.id());
    }

    @Test
    @DisplayName("Неизвестная категория вызывает ошибку")
    void unknownCategoryIsRejected() {
        assertThatThrownBy(() -> Category.fromId("cars")).isInstanceOf(IllegalArgumentException.class);
    }
}
