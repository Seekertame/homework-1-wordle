package academy.fiveletters.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.game.LetterMatcher;
import academy.fiveletters.game.LetterStatus;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: раскраска букв. */
@DisplayName("MR2. Проверка букв")
class LetterMatchingTest {

    @Test
    @DisplayName("Базовый случай: загадано \"озеро\", ввод \"арбуз\" -> ❌🟡❌❌🟡")
    void basicCase() {
        assertThat(symbols(LetterMatcher.match("озеро", "арбуз"))).isEqualTo("❌🟡❌❌🟡");
    }

    @Test
    @DisplayName("Полное совпадение: загадано \"озеро\", ввод \"озеро\" -> ✅✅✅✅✅")
    void exactMatch() {
        assertThat(symbols(LetterMatcher.match("озеро", "озеро"))).isEqualTo("✅✅✅✅✅");
    }

    @Test
    @DisplayName("Повторяющиеся буквы: загадано \"сорок\", ввод \"оооом\" -> ❌✅❌✅❌")
    void repeatedLettersAreNotDoubleCounted() {
        assertThat(symbols(LetterMatcher.match("сорок", "оооом"))).isEqualTo("❌✅❌✅❌");
    }

    @Test
    @DisplayName("Ни одна буква не подошла: все позиции ❌")
    void noMatchingLetters() {
        assertThat(LetterMatcher.match("озеро", "мышка")).containsOnly(LetterStatus.ABSENT);
    }

    private static String symbols(List<LetterStatus> statuses) {
        return statuses.stream().map(LetterStatus::symbol).collect(Collectors.joining());
    }
}
