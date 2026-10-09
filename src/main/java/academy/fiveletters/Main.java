package academy.fiveletters;

import static academy.fiveletters.Constants.DEFAULT_MAX_ATTEMPTS;
import static academy.fiveletters.Constants.INVALID_ARGUMENTS_EXIT_CODE;
import static academy.fiveletters.Constants.REPLAY_DIR;
import static academy.fiveletters.Constants.USAGE;

import academy.fiveletters.console.ConsoleGame;
import academy.fiveletters.console.GameRecorder;
import academy.fiveletters.console.LaunchOptions;
import academy.fiveletters.console.Replay;
import academy.fiveletters.game.Category;
import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.Game;
import academy.fiveletters.game.GameSeries;
import academy.fiveletters.game.Level;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Random;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Точка входа в игру «5 букв». */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {}

    /**
     * Запускает игру или повтор партии из файла. Если seed не задан, выбирается случайный и пишется в лог, чтобы игру
     * можно было повторить.
     *
     * @param args аргументы запуска, например {@code --seed 42 --show-answer}
     */
    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: {}", String.join(" ", args));
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        var err = new PrintStream(System.err, true, StandardCharsets.UTF_8);

        LaunchOptions options;
        try {
            options = LaunchOptions.parse(args);
        } catch (IllegalArgumentException e) {
            fail(err, e.getMessage() + System.lineSeparator() + USAGE);
            return;
        }

        var dictionary = Dictionary.load();
        var in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        var replayFile = options.replay();
        if (replayFile == null) {
            play(options, dictionary, in, out);
        } else {
            replay(replayFile, options, dictionary, in, out, err);
        }
    }

    private static void play(LaunchOptions options, Dictionary dictionary, BufferedReader in, PrintStream out) {
        var seed = options.seed();
        if (seed == null) {
            seed = new Random().nextLong();
        }
        LOG.info("seed: {}", seed);

        var level = options.level();
        var category = options.category();
        var series = new GameSeries(dictionary, answers(dictionary, level, category, out), seed);

        GameRecorder recorder = null;
        if (options.record()) {
            recorder = inputs -> new Replay(series.lastSeed(), level, category, inputs).save(Path.of(REPLAY_DIR));
        }
        new ConsoleGame(series::next, in, out, options.showAnswer(), options.color(), recorder).run();
    }

    private static void replay(
            String file,
            LaunchOptions options,
            Dictionary dictionary,
            BufferedReader in,
            PrintStream out,
            PrintStream err) {
        Replay replay;
        try {
            replay = Replay.load(Path.of(file));
        } catch (NoSuchFileException e) {
            fail(err, "файл не найден: " + Path.of(file).toAbsolutePath());
            return;
        } catch (IOException | InvalidPathException e) {
            fail(err, "не удалось прочитать файл " + file + ". Файл должен быть в кодировке UTF-8");
            return;
        } catch (IllegalArgumentException e) {
            fail(err, e.getMessage());
            return;
        }

        var seed = replay.seed();
        var answers = answers(dictionary, replay.level(), replay.category(), out);
        new ConsoleGame(
                        () -> Game.startGame(dictionary, answers, DEFAULT_MAX_ATTEMPTS, seed),
                        in,
                        out,
                        options.showAnswer(),
                        options.color())
                .replay(replay.inputs());
    }

    private static Dictionary answers(
            Dictionary dictionary, @Nullable Level level, @Nullable Category category, PrintStream out) {
        if (level != null) {
            out.println("Уровень: " + level.title());
            return level.answers(dictionary);
        }
        if (category != null) {
            out.println("Категория: " + category.title());
            return category.answers(dictionary);
        }
        return dictionary;
    }

    private static void fail(PrintStream err, @Nullable String message) {
        err.println("Ошибка: " + message);
        System.exit(INVALID_ARGUMENTS_EXIT_CODE);
    }
}
