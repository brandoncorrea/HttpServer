package jarvis;

import httpServer.*;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class GuessController implements GetController {
    private final String filePath;
    private final GuessingGameRepository repo;

    public GuessController(String filePath, GuessingGameRepository repo) {
        this.filePath = filePath;
        this.repo = repo;
    }

    public HttpResponse get(HttpRequest request) {
        try {
            return constructResponse(request);
        } catch (Exception ignored) {
            return new HttpResponse(HttpStatusCode.InternalServerError, "Failed to load resource");
        }
    }

    private HttpResponse constructResponse(HttpRequest request) throws IOException {
        String sessionId = getSessionId(request);
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
        int guessesLeft = game.guessLimit - game.guesses();
        String triesMessage;
        if (guessesLeft == 1)
            triesMessage = guessesLeft + " try left";
        else
            triesMessage = guessesLeft + " tries left";

        if (game.guesses() == 0)
            return triesMessage;
        if (game.lastResult() == 1)
            return String.format("Too high! %s", triesMessage);
        else if (game.lastResult() == -1)
            return String.format("Too low! %s", triesMessage);
        return "";
    }

    private String getSessionId(HttpRequest request) {
        String cookieHeader = request.headers.get("Cookie");
        if (cookieHeader != null)
            for (String cookie : cookieHeader.split(";"))
                if (Objects.equals(cookie.substring(0, 11), "session_id="))
                    return cookie.substring(11);
        return UUID.randomUUID().toString();
    }
}
