package jarvisTest;

import httpServer.HttpRequest;
import httpServer.HttpResponse;
import httpServer.HttpStatusCode;
import jarvis.Configuration;
import jarvis.GuessController;
import jarvis.GuessingGame;
import jarvis.GuessingGameRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class GuessControllerTest {

    @Test
    public void generatesRandomSessionIdGuids() {
        GuessingGameRepository repo = new GuessingGameRepository();
        Configuration config = new Configuration();
        config.set("GuessPage", "src/resources/guess.html");
        GuessController[] controllers = {
            new GuessController(config, repo),
            new GuessController("src/resources/guess.html", repo)
        };

        for (GuessController controller : controllers) {
            HttpRequest req = new HttpRequest("GET / HTTP/1.1");
            HttpResponse res = controller.get(req);
            String guid1 = res.headers.get("Set-Cookie").substring(11);
            res = controller.get(req);
            String guid2 = res.headers.get("Set-Cookie").substring(11);
            Assert.assertTrue(res.content.contains("Guess a number between 1 and 100"));
            Assert.assertTrue(res.content.contains("7 tries left"));
            Assert.assertNotEquals(guid1, guid2);
            Assert.assertFalse(guid1.isEmpty());
            Assert.assertFalse(guid2.isEmpty());

            UUID.fromString(guid1);
            UUID.fromString(guid2);
        }
    }

    @Test
    public void displaysProperMessagesForGame() {
        GuessingGameRepository repo = new GuessingGameRepository();
        GuessController handler = new GuessController("src/resources/guess.html", repo);
        HttpRequest req = new HttpRequest("GET / HTTP/1.1");
        HttpResponse res = handler.get(req);
        String sessionId = res.headers.get("Set-Cookie").substring(11);
        req.headers.put("Cookie", "session_id=" + sessionId);
        GuessingGame game = repo.newGame(sessionId);
        game.guess(game.answer + 1);
        game.guess(game.answer + 1);
        game.guess(game.answer + 1);
        game.guess(game.answer + 1);
        res = handler.get(req);
        Assert.assertTrue(res.content.contains("Guess a number between 1 and 100"));
        Assert.assertTrue(res.content.contains("Too high! 3 tries left"));

        game.guess(game.answer - 1);
        game.guess(game.answer - 1);
        res = handler.get(req);
        Assert.assertTrue(res.content.contains("Guess a number between 1 and 100"));
        Assert.assertTrue(res.content.contains("Too low! 1 try left"));

        game.guess(game.answer - 1);
        res = handler.get(req);
        Assert.assertTrue(res.content.contains("Game over! The number was " + game.answer));
        Assert.assertTrue(res.content.contains("Too low! 0 tries left"));

        game = repo.newGame(sessionId);
        game.guess(game.answer);
        res = handler.get(req);
        Assert.assertTrue(res.content.contains("Winner! You guessed the number " + game.answer));
        Assert.assertFalse(res.content.contains("tries left"));
    }

    @Test
    public void doesNotSetCookieIfAlreadySet() {
        GuessingGameRepository repo = new GuessingGameRepository();
        GuessController handler = new GuessController("src/resources/guess.html", repo);
        HttpRequest req = new HttpRequest("GET / HTTP/1.1");
        HttpResponse res = handler.get(req);
        String sessionId = res.headers.get("Set-Cookie").substring(11);

        req = new HttpRequest("GET / HTTP/1.1\r\nCookie: session_id=" + sessionId);
        res = handler.get(req);
        Assert.assertEquals("session_id=" + sessionId, res.headers.get("Set-Cookie"));

        req = new HttpRequest("GET / HTTP/1.1\r\nCookie: bleh=" + sessionId);
        res = handler.get(req);
        Assert.assertNotNull(res.headers.get("Set-Cookie"));
        Assert.assertNotEquals(sessionId, res.headers.get("Set-Cookie").substring(11));
    }

    @Test
    public void serverErrorWhenFileNotFound() {
        GuessingGameRepository repo = new GuessingGameRepository();
        Configuration config = new Configuration();
        config.set("GuessPage", "not/a/path.html");
        GuessController[] controllers = {
                new GuessController("not/a/path.html", repo),
                new GuessController(config, repo),

        };

        for (GuessController controller : controllers) {
            HttpResponse res = controller.get(new HttpRequest("GET / HTTP/1.1"));
            Assert.assertEquals(HttpStatusCode.InternalServerError, res.statusCode);
            Assert.assertEquals("Failed to load resource", res.content);
        }
    }

    @Test
    public void postGuessesNumberForGame() {
        GuessingGameRepository repo = new GuessingGameRepository();
        GuessController controller = new GuessController("src/resources/guess.html", repo);
        GuessingGame game = repo.newGame("123");

        int guesses = game.guessLimit;
        for (int guess : new int[] { game.answer + 1, game.answer - 1}) {
            HttpRequest req = new HttpRequest("POST / HTTP/1.1\r\n\r\nnumber=" + guess + "&guess=Guess");
            req.headers.put("Cookie", "session_id=123");
            HttpResponse res = controller.post(req);
            Assert.assertEquals(HttpStatusCode.OK, res.statusCode);
            Assert.assertTrue(res.content.contains("Guess a number between 1 and 100"));
            Assert.assertTrue(res.content.contains("Too high!") || res.content.contains("Too low!"));
            Assert.assertTrue(res.content.contains(--guesses + " tries left"));
        }
    }

    @Test
    public void postCreatesNewGame() {
        GuessingGameRepository repo = new GuessingGameRepository();
        GuessController controller = new GuessController("src/resources/guess.html", repo);
        GuessingGame game = repo.newGame("123");

        HttpRequest req = new HttpRequest("POST / HTTP/1.1\r\n\r\nnumber=3&newGame=New+Game");
        req.headers.put("Cookie", "session_id=123");
        HttpResponse res = controller.post(req);
        Assert.assertEquals(HttpStatusCode.OK, res.statusCode);

        GuessingGame newGame = repo.findBySessionId("123");
        Assert.assertNotEquals(game.answer, newGame.answer);
        Assert.assertEquals(0, game.guesses());
        Assert.assertEquals(0, newGame.guesses());
    }
}
