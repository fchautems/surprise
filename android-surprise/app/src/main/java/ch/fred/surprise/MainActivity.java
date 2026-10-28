package ch.fred.surprise;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends Activity {
    private static final int BG=Color.rgb(17,22,42), CARD=Color.rgb(31,42,68), GOLD=Color.rgb(255,195,107), WHITE=Color.rgb(245,242,235);
    private static final String[][] BANK={
        {"MUSIQUE","Quelles notes composent l'accord de do majeur ?","Do · Mi · Sol","Do · Mi♭ · Sol","Do · Fa · La","Ré · Fa · La"},
        {"MUSIQUE","Combien de touches possède généralement un piano moderne ?","88","76","61","96"},
        {"MUSIQUE","Quel intervalle sépare do et sol ?","Une quinte","Une tierce","Une octave","Une seconde"},
        {"MUSIQUE","Dans un blues traditionnel, combien de mesures compte une grille ?","12","8","16","24"},
        {"MUSIQUE","Quel rythme est caractéristique du jazz ?","Le swing","La marche militaire","La valse lente uniquement","Le plain-chant"},
        {"CUISINE","Qu'est-ce qu'une biga ?","Une préfermentation","Une sauce tomate","Un fromage","Une farine complète"},
        {"CUISINE","Quel ingrédient permet à une pâte levée de gonfler ?","La levure","Le sel seul","L'huile seule","Le poivre"},
        {"CUISINE","De quel fruit tire-t-on le pruneau ?","La prune","La figue","L'abricot","La cerise"},
        {"VOYAGE","Quel lac borde Nyon ?","Le Léman","Le lac de Neuchâtel","Le lac de Thoune","Le lac Majeur"},
        {"VOYAGE","Dans quel canton se trouve Nyon ?","Vaud","Valais","Fribourg","Berne"},
        {"CIEL","Comment s'appelle la trajectoire d'une planète ?","Une orbite","Une méridienne","Une portée","Une marée"},
        {"CIEL","Lors d'une éclipse solaire totale, que cache la Lune ?","Le Soleil","Mars","La Terre","Vénus"},
        {"CIEL","Quel est le satellite naturel de la Terre ?","La Lune","Titan","Europe","Phobos"},
        {"ALLEMAND","Que signifie « Danke » ?","Merci","Bonjour","Au revoir","S'il te plaît"},
        {"ALLEMAND","Quel mot signifie « musique » ?","Musik","Wasser","Garten","Fenster"},
        {"TEMPS","Quel mécanisme mesure le temps dans une montre mécanique ?","Un mouvement à engrenages","Une antenne GPS obligatoire","Un écran tactile","Un panneau solaire obligatoire"}
    };
    private static final int ROUND=8;
    private final Random rng=new Random();
    private final ArrayList<Integer> order=new ArrayList<>();
    private int index,score,streak,best; private boolean answered;
    private String[] options; private String selected;
    private LinearLayout root,choices;
    private TextView header,category,question,feedback;
    private Button next;
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    private TextView label(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER);return v;}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private void newGame(){order.clear();for(int i=0;i<BANK.length;i++)order.add(i);Collections.shuffle(order,rng);index=score=streak=0;answered=false;showQuestion();}
    private void showQuestion(){
        answered=false; selected=null; choices.removeAllViews(); next.setVisibility(View.GONE);
        String[] q=BANK[order.get(index)]; header.setText("MANCHE  "+(index+1)+" / "+ROUND+"     ✦  "+score);
        category.setText(q[0]); question.setText(q[1]); feedback.setText("");
        ArrayList<String> a=new ArrayList<>();for(int i=2;i<q.length;i++)a.add(q[i]);Collections.shuffle(a,rng);options=a.toArray(new String[0]);
        for(String value:options){
            Button b=new Button(this);b.setText(value);b.setTextColor(WHITE);b.setTextSize(17);b.setAllCaps(false);b.setBackground(shape(CARD,dp(16)));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(54));lp.setMargins(0,dp(5),0,dp(5)); choices.addView(b,lp);
            b.setOnClickListener(v->answer(value));
        }
    }
    private void answer(String value){
        if(answered)return;answered=true;selected=value;String correct=BANK[order.get(index)][2];boolean ok=correct.equals(value);
        if(ok){streak++;score+=10+Math.min(streak-1,5)*2;feedback.setText("✦ Bravo !  +"+(10+Math.min(streak-1,5)*2)+" points");}
        else {streak=0;feedback.setText("La réponse : "+correct);}
        feedback.setTextColor(ok?GOLD:WHITE);
        for(int i=0;i<choices.getChildCount();i++){
            Button b=(Button)choices.getChildAt(i);b.setEnabled(false);
            if(b.getText().toString().equals(correct))b.setBackground(shape(Color.rgb(36,105,88),dp(16)));
            else if(b.getText().toString().equals(value))b.setBackground(shape(Color.rgb(125,57,70),dp(16)));
        }
        next.setText(index==ROUND-1?"Voir le résultat":"Continuer");next.setVisibility(View.VISIBLE);
    }
    private void finishRound(){
        best=Math.max(score,getPreferences(MODE_PRIVATE).getInt("best",0));getPreferences(MODE_PRIVATE).edit().putInt("best",best).apply();
        choices.removeAllViews();header.setText("PARTIE TERMINÉE");category.setText("✦  BRAVO  ✦");
        question.setText(score+" points");feedback.setText("Record : "+best+" points\nUne nouvelle partie ?");feedback.setTextColor(GOLD);
        next.setText("Rejouer");next.setVisibility(View.VISIBLE);
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
        android.widget.ScrollView scroll=new android.widget.ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(22),dp(24),dp(22),dp(24));scroll.addView(root);
        header=label("",14,GOLD);header.setTypeface(null,Typeface.BOLD);root.addView(header);
        category=label("",14,GOLD);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(24),0,dp(8));root.addView(category,cp);
        question=label("",25,WHITE);question.setTypeface(null,Typeface.BOLD);root.addView(question);
        choices=new LinearLayout(this);choices.setOrientation(LinearLayout.VERTICAL);LinearLayout.LayoutParams choicesP=new LinearLayout.LayoutParams(-1,-2);choicesP.setMargins(0,dp(22),0,0);root.addView(choices,choicesP);
        feedback=label("",16,GOLD);LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2);fp.setMargins(0,dp(16),0,dp(12));root.addView(feedback,fp);
        next=new Button(this);next.setAllCaps(false);next.setTextColor(BG);next.setTextSize(17);next.setBackground(shape(GOLD,dp(16)));root.addView(next,new LinearLayout.LayoutParams(-1,dp(54)));
        next.setOnClickListener(v->{if(index>=ROUND){newGame();}else if(index==ROUND-1){index++;finishRound();}else{index++;showQuestion();}});
        setContentView(scroll);
        if(saved!=null && saved.containsKey("order")){
            int[] arr=saved.getIntArray("order");for(int x:arr)order.add(x);index=saved.getInt("index");score=saved.getInt("score");streak=saved.getInt("streak");
            if(index>=ROUND)finishRound();else {showQuestion(); String previous=saved.getString("selected"); if(previous!=null){int oldScore=score, oldStreak=streak; answer(previous); score=oldScore; streak=oldStreak;}}
        }else newGame();
    }
    @Override protected void onSaveInstanceState(Bundle out){
        int[] arr=new int[order.size()];for(int i=0;i<arr.length;i++)arr[i]=order.get(i);out.putIntArray("order",arr);out.putInt("index",index);out.putInt("score",score);out.putInt("streak",streak); if(answered)out.putString("selected",selected);
        super.onSaveInstanceState(out);
    }
}
