package academy.fiveletters.game;

/**
 * Подсказка: одна буква ответа и её место.
 *
 * @param position место буквы, считая с нуля
 * @param letter буква ответа на этом месте
 */
public record Hint(int position, char letter) {}
