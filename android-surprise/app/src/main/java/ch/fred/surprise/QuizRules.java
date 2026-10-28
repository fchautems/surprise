package ch.fred.surprise;

/** Scoring is kept independent of Android for fast, repeatable verification. */
final class QuizRules {
    private QuizRules() {}
    /** Prefer unseen questions, then recycle older ones if the bank is small. */
    static java.util.ArrayList<Integer> order(int count, int round, java.util.Set<String> recent, java.util.Random random) {
        if (count < 1 || round < 1 || round > count) throw new IllegalArgumentException("invalid round");
        java.util.ArrayList<Integer> fresh = new java.util.ArrayList<>(), old = new java.util.ArrayList<>();
        for (int i=0; i<count; i++) (recent.contains(Integer.toString(i)) ? old : fresh).add(i);
        java.util.Collections.shuffle(fresh, random);
        java.util.Collections.shuffle(old, random);
        fresh.addAll(old);
        return fresh;
    }
    static int points(int streak) {
        if (streak < 1) throw new IllegalArgumentException("streak must be positive");
        return 10 + Math.min(streak - 1, 5) * 2;
    }
}
