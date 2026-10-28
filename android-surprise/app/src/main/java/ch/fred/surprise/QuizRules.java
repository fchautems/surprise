package ch.fred.surprise;

/** Scoring is kept independent of Android for fast, repeatable verification. */
final class QuizRules {
    private QuizRules() {}
    static int points(int streak) {
        if (streak < 1) throw new IllegalArgumentException("streak must be positive");
        return 10 + Math.min(streak - 1, 5) * 2;
    }
}
