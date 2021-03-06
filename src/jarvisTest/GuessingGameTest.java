package jarvisTest;

import jarvis.GuessingGame;
import org.junit.Assert;
import org.junit.Test;

public class GuessingGameTest {
    @Test
    public void newGuessingGame() {
        GuessingGame game = new GuessingGame();
        testNewGame(game, 1, 100, 7);
        Assert.assertNotEquals(new GuessingGame().answer, game.answer);
        Assert.assertEquals(1, new GuessingGame(0).guessLimit);
        Assert.assertEquals(1, new GuessingGame(10, 20, 0).guessLimit);
    }

    @Test
    public void newGameWithLimit() {
        for (int limit : new int[] { 1, 2, 3, 4 }) {
            GuessingGame game = new GuessingGame(limit);
            testNewGame(game, 1, 100, limit);
            Assert.assertNotEquals(game.answer, new GuessingGame(limit).answer);
        }
    }

    @Test
    public void newGameWithMinAndMax() {
        GuessingGame game;
        int[][] initializers = {{10, 50}, {-1234, 50213}, {-300, -200}};
        for (int[] minMax : initializers) {
            int min = minMax[0];
            int max = minMax[1];
            game = new GuessingGame(min, max);
            testNewGame(game, min, max, 7);
            Assert.assertNotEquals(new GuessingGame(min, max).answer, game.answer);
        }
    }

    @Test
    public void newGameWithMinMaxAndLimit() {
        int[][] initializers = {{10, 50, 4}, {3, 500, 7}};
        for (int[] options : initializers) {
            int min = options[0];
            int max = options[1];
            int limit = options[2];
            GuessingGame game = new GuessingGame(min, max, limit);
            testNewGame(game, min, max, limit);
            Assert.assertNotEquals(game.answer, new GuessingGame(min, max, limit).answer);
        }
    }

    @Test
    public void newGameWithMinGreaterThanMax() {
        int[][] initializers = new int[][] {{1, 1}, {1, 0}, {1, -1}};
        for (int[] minMax : initializers) {
            int min = minMax[0];
            int max = minMax[1];
            GuessingGame game = new GuessingGame(min, max);
            Assert.assertEquals(min, game.min);
            Assert.assertEquals(max, game.max);
            Assert.assertEquals(7, game.guessLimit);
            Assert.assertEquals(min, game.answer);
        }
    }

    private void testNewGame(GuessingGame game, int expectedMin, int expectedMax, int expectedLimit) {
        Assert.assertEquals(expectedMin, game.min);
        Assert.assertEquals(expectedMax, game.max);
        Assert.assertEquals(expectedLimit, game.guessLimit);
        Assert.assertTrue(expectedMin <= game.answer && game.answer <= expectedMax);
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

    @Test
    public void storesLastGuessResult() {
        GuessingGame game = new GuessingGame();
        Assert.assertEquals(0, game.lastResult());
        game.guess(game.answer + 1);
        Assert.assertEquals(1, game.lastResult());
        game.guess(game.answer - 1);
        Assert.assertEquals(-1, game.lastResult());
        game.guess(game.answer);
        Assert.assertEquals(0, game.lastResult());
        game.guess(game.answer + 1);
        Assert.assertEquals(0, game.lastResult());
    }
}
