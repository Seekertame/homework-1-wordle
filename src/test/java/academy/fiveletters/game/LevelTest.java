package academy.fiveletters.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.fiveletters.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Уровни сложности")
class LevelTest {

    private final Dictionary dictionary = Dictionary.load();
    private final Dictionary easyWords = Dictionary.load(Constants.EASY_WORDS_RESOURCE);

    @Test
    @DisplayName("Лёгкий уровень берёт слова из списка распространённых")
    void easyLevelUsesCommonWords() {
        var answers = Level.EASY.answers(dictionary);

        assertThat(answers.words()).isEqualTo(easyWords.words());
        assertThat(dictionary.containsAll(answers)).isTrue();
    }

    @Test
    @DisplayName("Сложный уровень берёт все остальные слова")
    void hardLevelUsesOtherWords() {
        var answers = Level.HARD.answers(dictionary);

        assertThat(answers.words()).isNotEmpty().noneMatch(easyWords::contains);
    }

    @Test
    @DisplayName("Уровни делят словарь без остатка")
    void levelsSplitDictionary() {
        var easy = Level.EASY.answers(dictionary).size();
        var hard = Level.HARD.answers(dictionary).size();

        assertThat(easy + hard).isEqualTo(dictionary.size());
    }

    @Test
    @DisplayName("Партия на уровне загадывает слово этого уровня")
    void gameUsesLevelWords() {
        var answers = Level.EASY.answers(dictionary);

        for (long seed = 0; seed < 20; seed++) {
            assertThat(answers.contains(
                            Game.startGame(dictionary, answers, 6, seed).answer()))
                    .isTrue();
        }
    }

    @Test
    @DisplayName("Уровень находится по названию")
    void levelIsFoundById() {
        assertThat(Level.fromId("easy")).isEqualTo(Level.EASY);
        assertThat(Level.fromId("hard")).isEqualTo(Level.HARD);
        assertThatThrownBy(() -> Level.fromId("medium")).isInstanceOf(IllegalArgumentException.class);
    }
}
