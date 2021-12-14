package jarvis;

import java.util.Random;

public class GuessingGame {
    public final int min;
    public final int max;
    public final int answer;
    public final int guessLimit;
    private int guesses = 0;
    private boolean win = false;
    private int lastResult = 0;

    public GuessingGame() {
        this(1, 100, 7);
    }

    public GuessingGame(int guessLimit) {
        this(1, 100, guessLimit);
    }

    public GuessingGame(int min, int max) {
        this(min, max, 7);
    }

    public GuessingGame(int min, int max, int guessLimit) {
        this.min = min;
        this.max = max;
        answer = generateAnswer();
        if (guessLimit < 1) guessLimit = 1;
        this.guessLimit = guessLimit;
    }

    private int generateAnswer() {
        if (min >= max) return min;
        return new Random().nextInt(max - min) + min;
    }

    public int guess(int choice) {
        int result = Integer.compare(choice, answer);
        if (gameOver()) return result;
        guesses++;
        if (result == 0) win = true;
        lastResult = result;
        return result;
    }

    public int guesses() { return guesses; }
    public boolean gameOver() { return win || guesses >= guessLimit; }
    public boolean won() { return win; }
    public int lastResult() { return lastResult; }
}
