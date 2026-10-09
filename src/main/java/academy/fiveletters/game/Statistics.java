package academy.fiveletters.game;

/** Статистика игрока за один запуск программы. */
public final class Statistics {

    private int played;
    private int won;

    /**
     * Учитывает завершённую партию.
     *
     * @throws IllegalArgumentException если партия ещё идёт
     */
    public void record(GameSession session) {
        if (session.status() == GameStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Учесть можно только завершённую партию");
        }
        played++;
        if (session.status() == GameStatus.WIN) {
            won++;
        }
    }

    public int played() {
        return played;
    }

    public int won() {
        return won;
    }

    /** Процент побед, округлённый вниз. Если партий не было, возвращает 0. */
    @SuppressWarnings("checkstyle:MagicNumber")
    public int winPercent() {
        return played == 0 ? 0 : won * 100 / played;
    }
}
