package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("Подсчёт букв")
class LetterMatcherTest {

    @ParameterizedTest(name = "Загадано {0}, ввод {1}, результат {2}")
    @CsvSource({
        "озеро, ооооо, ✅❌❌❌✅",
        "озеро, сорок, ❌🟡🟡🟡❌",
        "арбуз, бабах, ❌🟡✅❌❌",
        "сорок, окрос, 🟡🟡✅✅🟡",
        "арбуз, ааааа, ✅❌❌❌❌"
    })
    @DisplayName("Повторяющиеся буквы")
    void repeatedLetters(String answer, String guess, String expected) {
        assertThat(symbols(LetterMatcher.match(answer, guess))).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0} и {1}")
    @CsvSource({"озеро, озер", "озеро, озерко"})
    @DisplayName("Слова разной длины вызывают ошибку")
    void differentLengthsAreRejected(String answer, String guess) {
        assertThatThrownBy(() -> LetterMatcher.match(answer, guess)).isInstanceOf(IllegalArgumentException.class);
    }

    private static String symbols(List<LetterStatus> statuses) {
        return statuses.stream().map(LetterStatus::symbol).collect(Collectors.joining());
    }
}
