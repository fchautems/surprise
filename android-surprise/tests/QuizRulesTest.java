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
            // At the minimum thematic size, all eight previous questions must be
            // avoided in the next round, regardless of the shuffle seed.
            java.util.ArrayList<Integer> themed = QuizRules.order(16, 8, previous, new java.util.Random(seed));
            if (themed.size()!=16 || new java.util.HashSet<>(themed).size()!=16) throw new AssertionError("themed duplicates");
            for (int i=0; i<8; i++) if (previous.contains(themed.get(i).toString())) throw new AssertionError("themed repetition");
            java.util.Set<String> earlier = new java.util.HashSet<>();
            for (int i=8; i<16; i++) earlier.add(Integer.toString(i));
            java.util.ArrayList<Integer> third = QuizRules.order(24,8,previous,earlier,new java.util.Random(seed));
            for (int i=0;i<8;i++) if (third.get(i)<16) throw new AssertionError("two-round repeat");
            java.util.ArrayList<Integer> limited = QuizRules.order(16,8,previous,earlier,new java.util.Random(seed));
            for (int i=0;i<8;i++) if (previous.contains(limited.get(i).toString())) throw new AssertionError("newest repeat");
            earlier.add("0"); // An ID in both sets must retain newest priority.
            java.util.ArrayList<Integer> overlap = QuizRules.order(16,8,previous,earlier,new java.util.Random(seed));
            for (int i=0;i<8;i++) if (previous.contains(overlap.get(i).toString())) throw new AssertionError("overlap priority");
            java.util.ArrayList<Integer> small = QuizRules.order(8, 8, previous, new java.util.Random(seed));
            if (new java.util.HashSet<>(small).size()!=8) throw new AssertionError("small bank");
        }
        try {
            QuizRules.order(3, 4, java.util.Collections.emptySet(), new java.util.Random(1));
            throw new AssertionError("invalid round accepted");
        } catch (IllegalArgumentException expected) { }
        if (!QuizRules.award(0,0,0).equals("À découvrir")) throw new AssertionError("initial award");
        if (!QuizRules.award(1,4,2).equals("Première escale")) throw new AssertionError("first award");
        if (!QuizRules.award(3,8,3).equals("Fidèle aventurier")) throw new AssertionError("loyalty award");
        if (!QuizRules.award(4,25,4).equals("Grand curieux")) throw new AssertionError("curiosity award");
        if (!QuizRules.award(5,30,8).equals("Série parfaite")) throw new AssertionError("streak award");
        if (!QuizRules.award(10,60,8).equals("Explorateur légendaire")) throw new AssertionError("legendary award");
        try { QuizRules.award(-1,0,0); throw new AssertionError("negative accepted"); }
        catch (IllegalArgumentException expected) { }
        System.out.println("Scoring, selection and award tests passed");
    }
}
