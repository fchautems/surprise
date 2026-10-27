package ch.fred.surprise;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends Activity {
  private final Random random = new Random();
  private int score = 0;
  private TextView status, clue;
  private final String[][] questions = {
    {"Quel accord contient do, mi et sol ?", "Do majeur", "Do mineur"},
    {"Quel instrument comporte 88 touches ?", "Piano", "Trompette"},
    {"Quel mouvement décrit une planète autour d'une étoile ?", "Orbite", "Gammes"},
    {"En cuisine, une biga est une…", "Préfermentation", "Sauce"},
    {"Quel mot allemand signifie merci ?", "Danke", "Bitte"}
  };
  private String answer;
  private void next() {
    String[] q = questions[random.nextInt(questions.length)];
    clue.setText(q[0]); answer=q[1];
    boolean swap=random.nextBoolean();
    left.setText(q[swap?2:1]); right.setText(q[swap?1:2]);
  }
  private Button left,right;
  private void choose(Button b) {
    boolean correct=b.getText().toString().equals(answer);
    if(correct) score++;
    status.setText((correct ? "Bravo ! " : "Bien essayé ! ") + "Score : " + score);
    next();
  }
  @Override public void onCreate(Bundle state) {
    super.onCreate(state);
    if(state!=null) score=state.getInt("score",0);
    LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
    root.setGravity(Gravity.CENTER); root.setPadding(32,32,32,32);
    status=new TextView(this); status.setTextSize(22); status.setText("Score : "+score);
    clue=new TextView(this); clue.setTextSize(26); clue.setGravity(Gravity.CENTER);
    left=new Button(this); right=new Button(this);
    root.addView(status); root.addView(clue); root.addView(left); root.addView(right);
    left.setOnClickListener(v->choose(left)); right.setOnClickListener(v->choose(right));
    setContentView(root); next();
  }
  @Override protected void onSaveInstanceState(Bundle state){state.putInt("score",score);super.onSaveInstanceState(state);}
}
