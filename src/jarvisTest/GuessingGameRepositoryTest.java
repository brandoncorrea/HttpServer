package jarvisTest;

import jarvis.GuessingGame;
import jarvis.GuessingGameRepository;
import org.junit.Assert;
import org.junit.Test;

public class GuessingGameRepositoryTest {
    GuessingGameRepository repo = new GuessingGameRepository();

    @Test
    public void newGuessingGameRepository() {
        new GuessingGameRepository();
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
}
