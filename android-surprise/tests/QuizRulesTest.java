package ch.fred.surprise;

public final class QuizRulesTest {
    public static void main(String[] args) {
        for (int streak=1; streak<=20; streak++) {
            int expected = streak <= 6 ? 8 + 2 * streak : 20;
            if (QuizRules.points(streak) != expected) throw new AssertionError("streak " + streak);
        }
        for (int invalid : new int[]{0, -1}) {
            try { QuizRules.points(invalid); throw new AssertionError("accepted invalid streak"); }
            catch (IllegalArgumentException expected) { }
        }
        for (int seed=0; seed<100; seed++) {
            java.util.Set<String> previous = new java.util.HashSet<>();
            for (int i=0; i<8; i++) previous.add(Integer.toString(i));
            java.util.ArrayList<Integer> order = QuizRules.order(40, 8, previous, new java.util.Random(seed));
            if (order.size()!=40 || new java.util.HashSet<>(order).size()!=40) throw new AssertionError("duplicates");
            for (int i=0; i<8; i++) if (previous.contains(order.get(i).toString())) throw new AssertionError("repetition");
            java.util.ArrayList<Integer> small = QuizRules.order(8, 8, previous, new java.util.Random(seed));
            if (new java.util.HashSet<>(small).size()!=8) throw new AssertionError("small bank");
        }
        try {
            QuizRules.order(3, 4, java.util.Collections.emptySet(), new java.util.Random(1));
            throw new AssertionError("invalid round accepted");
        } catch (IllegalArgumentException expected) { }
        System.out.println("Scoring and selection tests passed");
    }
}
