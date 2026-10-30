package ch.fred.surprise;

/** Exercises reward boundaries and guarantees that a better record cannot reduce stars. */
public final class MemoryRulesTest {
    public static void main(String[] args) {
        for (int pairs : new int[]{4, 6, 8, 10}) {
            check(MemoryRules.starsFor(pairs, pairs + 1) == 3, "three-star boundary");
            check(MemoryRules.starsFor(pairs, pairs + 2) == 2, "two-star start");
            check(MemoryRules.starsFor(pairs, 2 * pairs) == 2, "two-star boundary");
            check(MemoryRules.starsFor(pairs, 2 * pairs + 1) == 1, "one-star start");
            for (int old = pairs; old < 100; old++)
                for (int current = pairs; current < 100; current++)
                    check(MemoryRules.starsFor(pairs, Math.min(old, current))
                            == Math.max(MemoryRules.starsFor(pairs, old),
                                        MemoryRules.starsFor(pairs, current)), "record monotonicity");
        }
        System.out.println("Memory reward boundaries and monotonicity passed.");
    }
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }
}
