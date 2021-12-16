package jarvisTest;

import jarvis.Configuration;
import jarvis.GuessingGame;
import jarvis.GuessingGameRepository;
import org.junit.Assert;
import org.junit.Test;

public class GuessingGameRepositoryTest {
    GuessingGameRepository repo = new GuessingGameRepository();

    @Test
    public void createsGameUsingConfiguredLimits() {
        int[][] settings = {{1, 100, 7}, {50, 50000, 99}};
        for (int[] setting : settings) {
            int min = setting[0];
            int max = setting[1];
            int limit = setting[2];
            Configuration config = new Configuration();
            config.set("GameMinGuess", String.valueOf(min));
            config.set("GameMaxGuess", String.valueOf(max));
            config.set("GameGuessLimit", String.valueOf(limit));
            GuessingGameRepository repo = new GuessingGameRepository(config);
            GuessingGame game = repo.newGame("123");
            Assert.assertEquals(min, game.min);
            Assert.assertEquals(max, game.max);
            Assert.assertEquals(limit, game.guessLimit);
        }
    }

    @Test
    public void newGameUsingSessionId() {
        GuessingGame sessionGame = repo.newGame("123");
        GuessingGame newGame = new GuessingGame();
        Assert.assertEquals(newGame.min, sessionGame.min);
        Assert.assertEquals(newGame.max, sessionGame.max);
        Assert.assertEquals(newGame.guessLimit, sessionGame.guessLimit);
        Assert.assertEquals(0, sessionGame.guesses());
    }

    @Test
    public void findBySessionId() {
        GuessingGame game1 = repo.newGame("123");
        Assert.assertEquals(game1, repo.findBySessionId("123"));
        GuessingGame game2 = repo.newGame("456");
        Assert.assertEquals(game2, repo.findBySessionId("456"));
        Assert.assertNotEquals(game1, game2);
        Assert.assertEquals(game1, repo.findBySessionId("123"));

        game1.guess(3);
        Assert.assertEquals(1, repo.findBySessionId("123").guesses());
    }

    @Test
    public void findByIdCreatesGameIfNotFound() {
        GuessingGameRepository repo = new GuessingGameRepository();
        GuessingGame sessionGame = repo.findBySessionId("123");
        GuessingGame newGame = new GuessingGame();
        Assert.assertEquals(newGame.min, sessionGame.min);
        Assert.assertEquals(newGame.max, sessionGame.max);
        Assert.assertEquals(newGame.guessLimit, sessionGame.guessLimit);
        Assert.assertEquals(0, sessionGame.guesses());
    }
}
