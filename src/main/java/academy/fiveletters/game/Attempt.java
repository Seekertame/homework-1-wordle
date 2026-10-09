package academy.fiveletters.game;

import java.util.List;

/**
 * Одна принятая попытка.
 *
 * @param guess введённое слово в нижнем регистре
 * @param statuses статус каждой буквы по порядку
 */
public record Attempt(String guess, List<LetterStatus> statuses) {

    /** Делает копию списка статусов, чтобы попытку нельзя было изменить снаружи. */
    public Attempt {
        statuses = List.copyOf(statuses);
    }
}
