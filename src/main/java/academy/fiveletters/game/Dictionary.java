package academy.fiveletters.game;

import static academy.fiveletters.Constants.DICTIONARY_RESOURCE;
import static academy.fiveletters.Constants.RUSSIAN_LETTERS;
import static academy.fiveletters.Constants.WORD_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Словарь игры. Из него загадывается слово, и с ним сверяется ввод игрока.
 *
 * <p>Словарь неизменяемый. Любой способ его создать проходит через конструктор, поэтому в нём только проверенные слова.
 */
public class Dictionary {

    private final List<String> words;

    /**
     * Создаёт словарь из готового списка слов. Слова приводятся к нижнему регистру, пробелы по краям убираются.
     *
     * @throws IllegalArgumentException если слово не из 5 русских букв или список пуст
     */
    public Dictionary(List<String> candidates) {
        var result = new ArrayList<String>();

        for (var candidate : candidates) {
            var word = candidate.strip().toLowerCase(Locale.ROOT);

            if (word.length() != WORD_LENGTH || !word.matches(RUSSIAN_LETTERS)) {
                throw new IllegalArgumentException("Слово не из " + WORD_LENGTH + " русских букв: '" + candidate + "'");
            }

            result.add(word);
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Словарь пуст");
        }

        this.words = List.copyOf(result);
    }

    /** Создаёт словарь из строк файла. Пустые строки и строки, которые начинаются с {@code #}, пропускаются. */
    public static Dictionary fromLines(List<String> lines) {
        var words = new ArrayList<String>();

        for (var line : lines) {
            var trimmed = line.strip();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            words.add(trimmed);
        }

        return new Dictionary(words);
    }

    /** Загружает общий словарь из {@code src/main/resources/dictionary.txt}. */
    public static Dictionary load() {
        return load(DICTIONARY_RESOURCE);
    }

    /**
     * Загружает словарь из файла в ресурсах программы, например {@code /categories/animals.txt}.
     *
     * @throws IllegalStateException если такого файла нет
     */
    public static Dictionary load(String resource) {
        try (InputStream inputStream = Dictionary.class.getResourceAsStream(resource)) {

            if (inputStream == null) {
                throw new IllegalStateException("Не найден ресурс " + resource);
            }

            var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            return fromLines(reader.lines().toList());

        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать словарь", e);
        }
    }

    /** Проверяет слово. Слово должно быть уже в нижнем регистре. */
    public boolean contains(String word) {
        return words.contains(word);
    }

    public boolean containsAll(Dictionary other) {
        return words.containsAll(other.words);
    }

    /**
     * Отбирает слова по условию. Порядок слов сохраняется.
     *
     * @throws IllegalArgumentException если ни одно слово не подошло
     */
    public Dictionary filter(Predicate<String> condition) {
        return new Dictionary(words.stream().filter(condition).toList());
    }

    public int size() {
        return words.size();
    }

    /** Слова в порядке файла. От этого порядка зависит выбор слова по seed. Список нельзя изменить. */
    public List<String> words() {
        return words;
    }
}
