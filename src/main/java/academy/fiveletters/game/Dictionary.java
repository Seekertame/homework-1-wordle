package academy.fiveletters.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Dictionary {
    public static final int WORD_LENGTH = 5;

    private final List<String> words;

    public Dictionary(List<String> candidates) {
        var result = new ArrayList<String>();

        for (var candidate : candidates) {
            var word = candidate.strip().toLowerCase(Locale.ROOT);

            if (word.length() != WORD_LENGTH || !word.matches("[а-я]+")) {
                throw new IllegalArgumentException("Слово не из 5 русских букв: '" + candidate + "'");
            }

            result.add(word);
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Словарь пуст");
        }

        this.words = List.copyOf(result);
    }

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

    public static Dictionary load() {
        try (InputStream inputStream = Dictionary.class.getResourceAsStream("/dictionary.txt")) {

            if (inputStream == null) {
                throw new IllegalStateException("Не найден ресурс dictionary.txt");
            }

            var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            return fromLines(reader.lines().toList());

        } catch (IOException e) {
            throw new UncheckedIOException("Не удалось прочитать словарь", e);
        }
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public int size() {
        return words.size();
    }

    public List<String> words() {
        return words;
    }
}
