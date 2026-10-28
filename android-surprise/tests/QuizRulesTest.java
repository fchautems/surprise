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
        System.out.println("Scoring tests passed");
    }
}
