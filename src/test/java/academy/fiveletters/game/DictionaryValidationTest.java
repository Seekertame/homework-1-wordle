package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Проверка словаря")
class DictionaryValidationTest {

    @Test
    @DisplayName("Комментарии и пустые строки пропускаются")
    void commentsAndBlankLinesAreSkipped() {
        var dictionary = Dictionary.fromLines(List.of("# комментарий", "", "   ", "  # с отступом", "озеро"));

        assertThat(dictionary.words()).containsExactly("озеро");
    }

    @Test
    @DisplayName("Слова приводятся к нижнему регистру")
    void wordsAreNormalized() {
        var dictionary = new Dictionary(List.of("  ОЗЕРО ", "Арбуз"));

        assertThat(dictionary.words()).containsExactly("озеро", "арбуз");
    }

    @Test
    @DisplayName("Порядок слов сохраняется")
    void wordOrderIsPreserved() {
        var dictionary = new Dictionary(List.of("сорок", "арбуз", "озеро"));

        assertThat(dictionary.words()).containsExactly("сорок", "арбуз", "озеро");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"озер", "озерко", "ozero", "оз3ро", "оз-ро", "ёжики"})
    @DisplayName("Неверное слово вызывает ошибку")
    void invalidWordIsRejected(String word) {
        assertThatThrownBy(() -> new Dictionary(List.of("озеро", word)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(word);
    }

    @Test
    @DisplayName("Файл без слов вызывает ошибку")
    void fileWithOnlyCommentsIsRejected() {
        assertThatThrownBy(() -> Dictionary.fromLines(List.of("# только комментарий", "")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("пуст");
    }

    @Test
    @DisplayName("Словарь находит только свои слова")
    void containsChecksMembership() {
        var dictionary = new Dictionary(List.of("озеро", "арбуз"));

        assertThat(dictionary.contains("озеро")).isTrue();
        assertThat(dictionary.contains("сорок")).isFalse();
    }

    @Test
    @DisplayName("Список слов нельзя изменить снаружи")
    void wordsCannotBeModifiedFromOutside() {
        var dictionary = new Dictionary(List.of("озеро"));

        assertThatThrownBy(() -> dictionary.words().add("арбуз")).isInstanceOf(UnsupportedOperationException.class);
    }
}
