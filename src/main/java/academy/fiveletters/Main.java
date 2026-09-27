package academy.fiveletters;

import academy.fiveletters.console.ConsoleGame;
import academy.fiveletters.console.LaunchOptions;
import academy.fiveletters.game.Dictionary;
import academy.fiveletters.game.GameSeries;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Точка входа в игру «5 букв». */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private Main() {}

    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: {}", String.join(" ", args));
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        var err = new PrintStream(System.err, true, StandardCharsets.UTF_8);

        LaunchOptions options;
        try {
            options = LaunchOptions.parse(args);
        } catch (IllegalArgumentException e) {
            err.println("Ошибка: " + e.getMessage());
            err.println(LaunchOptions.USAGE);
            System.exit(2);
            return;
        }

        var seed = options.seed();
        if (seed == null) {
            seed = new Random().nextLong();
        }
        LOG.info("seed: {}", seed);

        var series = new GameSeries(Dictionary.load(), seed);
        var in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        new ConsoleGame(series::next, in, out, options.showAnswer()).run();
    }
}
