package jarvis;

import httpServer.*;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Function;

public class GuessController implements GetController, PostController {
    private final String filePath;
    private final GuessingGameRepository repo;

    public GuessController(String filePath, GuessingGameRepository repo) {
        this.filePath = filePath;
        this.repo = repo;
    }

    public GuessController(Configuration config, GuessingGameRepository repo) {
        filePath = config.getString("GuessPage");
        this.repo = repo;
    }

    public HttpResponse get(HttpRequest request) {
        return renderPage(getSessionId(request));
    }

    public HttpResponse post(HttpRequest request) {
        String[] params = parseParameters(request);
        String sessionId = getSessionId(request);
        if (newGameClicked(params))
            repo.newGame(sessionId);
        else
            guessNumber(sessionId, params);
        return renderPage(sessionId);
    }

    private String[] parseParameters(HttpRequest request) {
        if (request.body.length == 0) return new String[0];
        return request.body[0].split("&");
    }

    private void guessNumber(String sessionId, String[] params) {
        try {
            GuessingGame game = repo.findBySessionId(sessionId);
            String number = getNumberParameter(params);
            game.guess(Integer.parseInt(number));
        } catch (Exception ignored) { }
    }

    private boolean newGameClicked(String[] params) {
        return find(params, s -> s.startsWith("newGame=")) != null;
    }

    private String getNumberParameter(String[] params) {
        return find(params, s -> s.startsWith("number=")).substring(7);
    }

    private String find(String[] items, Function<String, Boolean> predicate) {
        for (String item : items)
            if (predicate.apply(item))
                return item;
        return null;
    }

    private HttpResponse renderPage(String sessionId) {
        try {
            return constructResponse(sessionId);
        } catch (Exception ignored) {
            return new HttpResponse(HttpStatusCode.InternalServerError, "Failed to load resource");
        }
    }

    private HttpResponse constructResponse(String sessionId) throws IOException {
        HttpResponse res = new HttpResponse(HttpStatusCode.OK, constructResponseContent(sessionId));
        res.headers.put("Content-Type", "text/html");
        res.headers.put("Set-Cookie", "session_id=" + sessionId);
        return res;
    }

    private String constructResponseContent(String sessionId) throws IOException {
        GuessingGame game = repo.findBySessionId(sessionId);
        return FileHelper.readFile(filePath)
                .replace("{{instructions}}", constructInstructionMessage(game))
                .replace("{{status}}", constructStatusMessage(game));
    }

    private String constructInstructionMessage(GuessingGame game) {
        if (game.won())
            return "Winner! You guessed the number " + game.answer;
        if (game.gameOver())
            return "Game over! The number was " + game.answer;
        return String.format("Guess a number between %s and %s", game.min, game.max);
    }

    private String constructStatusMessage(GuessingGame game) {
        if (game.guesses() == 0)
            return triesMessage(game);
        if (game.lastResult() == 1)
            return String.format("Too high! %s", triesMessage(game));
        else if (game.lastResult() == -1)
            return String.format("Too low! %s", triesMessage(game));
        return "";
    }

    private String triesMessage(GuessingGame game) {
        int guessesLeft = game.guessLimit - game.guesses();
        if (guessesLeft == 1)
            return guessesLeft + " try left";
        else
            return guessesLeft + " tries left";
    }

    private String getSessionId(HttpRequest request) {
        try {
            for (String cookie : request.headers.get("Cookie").split(";")) {
                cookie = cookie.trim();
                if (cookie.startsWith("session_id="))
                    return cookie.substring(11);
            }
        } catch (Exception ignored) { }
        return UUID.randomUUID().toString();
    }
}
