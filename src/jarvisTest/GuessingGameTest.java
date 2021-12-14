package jarvisTest;

import jarvis.GuessingGame;
import org.junit.Assert;
import org.junit.Test;

public class GuessingGameTest {
    @Test
    public void newGuessingGame() {
        GuessingGame game = new GuessingGame();
        Assert.assertEquals(1, game.min);
        Assert.assertEquals(100, game.max);
        Assert.assertTrue(1 <= game.answer && game.answer <= 100);
        Assert.assertNotEquals(new GuessingGame().answer, game.answer);

        int[][] initializers = {{10, 50}, {-1234, 50213}, {-300, -200}};
        for (int[] minMax : initializers) {
            int min = minMax[0];
            int max = minMax[1];
            game = new GuessingGame(min, max);
            Assert.assertEquals(min, game.min);
            Assert.assertEquals(7, game.guessLimit);
            Assert.assertEquals(max, game.max);
            Assert.assertTrue(min <= game.answer && game.answer <= max);
            Assert.assertNotEquals(new GuessingGame(min, max).answer, game.answer);
        }

        initializers = new int[][] {{1, 1}, {1, 0}, {1, -1}};
        for (int[] minMax : initializers) {
            int min = minMax[0];
            int max = minMax[1];
            game = new GuessingGame(min, max);
            Assert.assertEquals(min, game.min);
            Assert.assertEquals(max, game.max);
            Assert.assertEquals(7, game.guessLimit);
            Assert.assertEquals(min, game.answer);
        }

        for (int limit : new int[] { 1, 2, 3, 4 }) {
            game = new GuessingGame(limit);
            Assert.assertEquals(limit, game.guessLimit);
            Assert.assertEquals(1, game.min);
            Assert.assertEquals(100, game.max);
            Assert.assertTrue(1 <= game.answer && game.answer <= 100);
            Assert.assertNotEquals(game.answer, new GuessingGame(limit).answer);
        }

        game = new GuessingGame(0);
        Assert.assertEquals(1, game.guessLimit);

        for (int[] options : new int[][] {{10, 50, 4}, {3, 500, 7}}) {
            int min = options[0];
            int max = options[1];
            int limit = options[2];
            game = new GuessingGame(min, max, limit);
            Assert.assertEquals(min, game.min);
            Assert.assertEquals(max, game.max);
            Assert.assertEquals(limit, game.guessLimit);
            Assert.assertTrue(min <= game.answer && game.answer <= max);
            Assert.assertNotEquals(game.answer, new GuessingGame(limit).answer);
        }

        game = new GuessingGame(10, 20, 0);
        Assert.assertEquals(1, game.guessLimit);
    }

    @Test
    public void guessAnswer() {
        GuessingGame game = new GuessingGame(10);
        Assert.assertEquals(0, game.guesses());
        Assert.assertFalse(game.gameOver());
        Assert.assertFalse(game.won());

        Assert.assertEquals(1, game.guess(game.answer + 1));
        Assert.assertEquals(1, game.guesses());
        Assert.assertFalse(game.gameOver());
        Assert.assertFalse(game.won());

        Assert.assertEquals(-1, game.guess(game.answer - 1));
        Assert.assertEquals(2, game.guesses());
        Assert.assertFalse(game.gameOver());
        Assert.assertFalse(game.won());

        Assert.assertEquals(0, game.guess(game.answer));
        Assert.assertEquals(3, game.guesses());
        Assert.assertTrue(game.gameOver());
        Assert.assertTrue(game.won());

        Assert.assertEquals(-1, game.guess(game.answer - 1));
        Assert.assertEquals(3, game.guesses());
        Assert.assertTrue(game.gameOver());
        Assert.assertTrue(game.won());
    }

    @Test
    public void reachGuessLimit() {
        GuessingGame game = new GuessingGame(4);
        for (int i = 0; i < game.guessLimit; i++)
            game.guess(game.answer + 1);
        Assert.assertEquals(4, game.guesses());
        Assert.assertTrue(game.gameOver());
        Assert.assertEquals(0, game.guess(game.answer));
        Assert.assertEquals(4, game.guesses());
        Assert.assertFalse(game.won());
    }
}
