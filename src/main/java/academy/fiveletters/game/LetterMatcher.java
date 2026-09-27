package academy.fiveletters.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LetterMatcher {

    private LetterMatcher() {}

    public static List<LetterStatus> match(String answer, String guess) {
        if (answer.length() != guess.length()) {
            throw new IllegalArgumentException("Слова разной длины: '" + answer + "' и '" + guess + "'");
        }

        List<LetterStatus> statuses = new ArrayList<>(Collections.nCopies(answer.length(), LetterStatus.ABSENT));
        List<Integer> unmatchedPositions = new ArrayList<>();
        Map<Character, Integer> freeLetters = new HashMap<>();

        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == guess.charAt(i)) {
                statuses.set(i, LetterStatus.CORRECT);
            } else {
                unmatchedPositions.add(i);
                freeLetters.merge(answer.charAt(i), 1, Integer::sum);
            }
        }

        for (int i : unmatchedPositions) {
            char letter = guess.charAt(i);
            if (freeLetters.getOrDefault(letter, 0) > 0) {
                statuses.set(i, LetterStatus.PRESENT);
                freeLetters.merge(letter, -1, Integer::sum);
            }
        }

        return List.copyOf(statuses);
    }
}
