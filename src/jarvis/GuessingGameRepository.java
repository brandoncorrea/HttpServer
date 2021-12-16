package jarvis;

import java.util.HashMap;
import java.util.Map;

public class GuessingGameRepository {
    protected final Map<String, GuessingGame> store = new HashMap<>();
    private final int min;
    private final int max;
    private final int limit;

    public GuessingGameRepository() {
        min = 1;
        max = 100;
        limit = 7;
    }

    public GuessingGameRepository(Configuration config) {
        min = config.getInt("GameMinGuess");
        max = config.getInt("GameMaxGuess");
        limit = config.getInt("GameGuessLimit");
    }

    public GuessingGame newGame(String sessionId) {
        GuessingGame game = new GuessingGame(min, max, limit);
        store.put(sessionId, game);
        return game;
    }

    public GuessingGame findBySessionId(String sessionId) {
        GuessingGame game = store.get(sessionId);
        if (game != null) return game;
        return newGame(sessionId);
    }
}
