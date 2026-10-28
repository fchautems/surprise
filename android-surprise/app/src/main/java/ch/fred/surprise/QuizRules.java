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
    static String award(int games, int correct, int longest) {
        if (games < 0 || correct < 0 || longest < 0) throw new IllegalArgumentException("negative statistics");
        if (correct >= 60 && games >= 10) return "Explorateur légendaire";
        if (longest >= 8) return "Série parfaite";
        if (correct >= 25) return "Grand curieux";
        if (games >= 3) return "Fidèle aventurier";
        if (games >= 1) return "Première escale";
        return "À découvrir";
    }
    static int points(int streak) {
        if (streak < 1) throw new IllegalArgumentException("streak must be positive");
        return 10 + Math.min(streak - 1, 5) * 2;
    }
}
