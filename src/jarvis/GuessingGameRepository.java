package jarvis;

import java.util.HashMap;
import java.util.Map;

public class GuessingGameRepository {
    private final Map<String, GuessingGame> store = new HashMap<>();

    public GuessingGame newGame(String sessionId) {
        GuessingGame game = new GuessingGame();
        store.put(sessionId, game);
        return game;
    }

    public GuessingGame findBySessionId(String sessionId) {
        return store.get(sessionId);
    }
}
