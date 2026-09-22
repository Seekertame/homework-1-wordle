package academy.fiveletters.mandatory.mr2;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: раскраска букв. */
@DisplayName("MR2. Проверка букв")
class LetterMatchingTest {

    @Test
    @Disabled("MR2: реализуй тест и удали эту строку")
    @DisplayName("Базовый случай: загадано \"озеро\", ввод \"арбуз\" -> ❌🟡❌❌🟡")
    void basicCase() {
        fail("Тест не реализован");
    }

    @Test
    @Disabled("MR2: реализуй тест и удали эту строку")
    @DisplayName("Полное совпадение: загадано \"озеро\", ввод \"озеро\" -> ✅✅✅✅✅")
    void exactMatch() {
        fail("Тест не реализован");
    }

    @Test
    @Disabled("MR2: реализуй тест и удали эту строку")
    @DisplayName("Повторяющиеся буквы: загадано \"сорок\", ввод \"оооом\" -> ❌✅❌✅❌")
    void repeatedLettersAreNotDoubleCounted() {
        fail("Тест не реализован");
    }

    @Test
    @Disabled("MR2: реализуй тест и удали эту строку")
    @DisplayName("Ни одна буква не подошла: все позиции ❌")
    void noMatchingLetters() {
        fail("Тест не реализован");
    }
}
