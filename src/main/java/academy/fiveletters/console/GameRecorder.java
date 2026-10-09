package academy.fiveletters.console;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Сохраняет завершённую партию для повтора. */
@FunctionalInterface
public interface GameRecorder {

    /**
     * Сохраняет всё, что игрок ввёл за партию.
     *
     * @return путь к сохранённому файлу
     */
    Path save(List<String> inputs) throws IOException;
}
