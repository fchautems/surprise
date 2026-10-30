package ch.fred.surprise;

/** Scoring is shared by the game and its standalone regression tests. */
final class MemoryRules {
    private MemoryRules() {}
    static int starsFor(int pairs, int attempts) {
        return attempts <= pairs + 1 ? 3 : attempts <= pairs * 2 ? 2 : 1;
    }
}
