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
        {"MUSIQUE","Quel symbole abaisse une note d’un demi-ton ?","Le bémol","Le dièse","La clé de sol","Le point"},
        {"MUSIQUE","Quelle famille comprend le saxophone ?","Les bois","Les cordes","Les percussions","Les claviers"},
        {"MUSIQUE","Combien de notes comporte une gamme majeure avant son octave ?","Sept","Cinq","Six","Huit"},
        {"CUISINE","Quel fromage est traditionnel dans une fondue moitié-moitié ?","Gruyère et vacherin fribourgeois","Mozzarella et feta","Parmesan et ricotta","Brie et camembert"},
        {"CUISINE","Quel gaz fait lever une pâte fermentée ?","Le dioxyde de carbone","L'oxygène","L'hélium","L'azote"},
        {"CUISINE","De quel fruit provient le raisin sec ?","Le raisin","La prune","La pomme","La datte"},
        {"VOYAGE","Quelle ville suisse se trouve au bord du Léman ?","Lausanne","Lucerne","Coire","Bâle"},
        {"VOYAGE","Quelle île portugaise est située dans l'Atlantique ?","Madère","Corse","Sardaigne","Crète"},
        {"CIEL","Quelle planète est connue pour ses anneaux ?","Saturne","Mercure","Mars","Vénus"},
        {"CIEL","Quelle étoile est au centre du système solaire ?","Le Soleil","Sirius","Polaris","Véga"},
        {"ALLEMAND","Que signifie « Wasser » ?","Eau","Pain","Lait","Café"},
        {"ALLEMAND","Comment dit-on « bonjour » en allemand ?","Guten Tag","Gute Nacht","Auf Wiedersehen","Danke schön"},
        {"MUSIQUE","Quel accord contient do, mi bémol et sol ?","Do mineur","Do majeur","Sol majeur","Fa majeur"},
        {"MUSIQUE","Combien de demi-tons séparent deux notes à l'octave ?","Douze","Sept","Cinq","Huit"},
        {"MUSIQUE","Dans le jazz, que désigne une improvisation ?","Une mélodie créée sur le moment","Une partition toujours identique","Un silence obligatoire","Un changement d'instrument"},
        {"CUISINE","Quel ingrédient est à la base du pesto traditionnel ?","Le basilic","La menthe","La cannelle","Le cacao"},
        {"CUISINE","Que signifie cuire al dente ?","Laisser une légère fermeté","Cuire jusqu'à dissolution","Servir glacé","Faire frire à sec"},
        {"VOYAGE","Quelle ville se trouve au bord du détroit de Gibraltar ?","Tarifa","Madrid","Grenoble","Milan"},
        {"CIEL","Quel instrument permet d'observer les astres lointains ?","Un télescope","Un microscope","Un stéthoscope","Un baromètre"},
        {"CIEL","Quelle planète porte le nom du dieu romain de la guerre ?","Mars","Vénus","Neptune","Mercure"},
        {"ALLEMAND","Que signifie « Guten Abend » ?","Bonsoir","Bonjour le matin","Bonne nuit","À demain"},
        {"ALLEMAND","Que signifie « Brot » ?","Pain","Beurre","Fromage","Eau"},
        {"TEMPS","Quel objet indique les heures et les minutes ?","Une montre","Une boussole","Un thermomètre","Une balance"},
        {"TEMPS","Combien de secondes compte une minute ?","60","100","24","12"},
        {"TEMPS","Quel mécanisme mesure le temps dans une montre mécanique ?","Un mouvement à engrenages","Une antenne GPS obligatoire","Un écran tactile","Un panneau solaire obligatoire"}
    };
    private static final int ROUND=8;
    private final Random rng=new Random();
    private final ArrayList<Integer> order=new ArrayList<>();
    private int index,score,streak,best,correctCount; private boolean answered, showingHome, jokerUsed;
    private String[] hiddenOptions;
    private String[] options; private String selected; private int earned;
    private LinearLayout root,choices;
    private TextView header,category,question,feedback;
    private android.widget.ProgressBar progress;
    private Button next, joker;
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    private TextView label(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER);return v;}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private void newGame(){
        showingHome=false;
        java.util.Set<String> recent = getPreferences(MODE_PRIVATE).getStringSet("recent", java.util.Collections.emptySet());
        order.clear(); order.addAll(QuizRules.order(BANK.length, ROUND, recent, rng));
        java.util.Set<String> played = new java.util.HashSet<>();
        for(int i=0;i<ROUND;i++) played.add(Integer.toString(order.get(i)));
        getPreferences(MODE_PRIVATE).edit().putStringSet("recent",played).apply();
        index=score=streak=correctCount=0; answered=false; jokerUsed=false; hiddenOptions=null;
        showQuestion();
    }
    private void showHome(){
        showingHome=true; answered=false; selected=null; choices.removeAllViews(); progress.setProgress(0); joker.setVisibility(View.GONE);
        header.setText("✦  BIENVENUE  ✦"); category.setText("UN MOMENT POUR TOI");
        question.setText("Une petite aventure à chaque partie");
        int record=getPreferences(MODE_PRIVATE).getInt("best",0);
        feedback.setText(record>0 ? "Ton record : "+record+" points" : "Huit étapes, des surprises et un peu de chance.");
        feedback.setTextColor(GOLD); next.setText("C’est parti"); next.setVisibility(View.VISIBLE);
    }
    private void showQuestion(){hiddenOptions=null; showQuestion(null);}
    private void showQuestion(String[] restoredOptions){
        answered=false; selected=null; earned=0; choices.removeAllViews(); next.setVisibility(View.GONE); progress.setProgress(index);
        joker.setVisibility(View.VISIBLE); joker.setEnabled(!jokerUsed); joker.setText(jokerUsed ? "Joker utilisé" : "Joker 50 / 50 · une fois");
        String[] q=BANK[order.get(index)]; header.setText("MANCHE  "+(index+1)+" / "+ROUND+"     ✦  "+score);
        category.setText(q[0]); question.setText(q[1]); feedback.setText("");
        ArrayList<String> a=new ArrayList<>();for(int i=2;i<q.length;i++)a.add(q[i]);
        if(restoredOptions!=null && restoredOptions.length==a.size() && a.containsAll(java.util.Arrays.asList(restoredOptions))){
            options=restoredOptions.clone();
        }else{Collections.shuffle(a,rng); options=a.toArray(new String[0]);}
        for(String value:options){
            Button b=new Button(this);b.setText(value);b.setTextColor(WHITE);b.setTextSize(17);b.setAllCaps(false);b.setBackground(shape(CARD,dp(16)));
            b.setMinHeight(dp(56)); b.setPadding(dp(12),dp(10),dp(12),dp(10));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(5),0,dp(5)); choices.addView(b,lp);
            if(hiddenOptions!=null && java.util.Arrays.asList(hiddenOptions).contains(value)) { b.setEnabled(false); b.setAlpha(0.35f); }
            b.setOnClickListener(v->answer(value));
        }
    }
    private void useJoker(){
        if(jokerUsed || answered)return;
        ArrayList<String> wrong=new ArrayList<>();
        String correct=BANK[order.get(index)][2];
        for(String option:options) if(!option.equals(correct)) wrong.add(option);
        Collections.shuffle(wrong,rng);
        hiddenOptions=new String[]{wrong.get(0),wrong.get(1)};
        jokerUsed=true; joker.setEnabled(false); joker.setText("Joker utilisé");
        for(int i=0;i<choices.getChildCount();i++){
            Button button=(Button)choices.getChildAt(i);
            if(java.util.Arrays.asList(hiddenOptions).contains(button.getText().toString())){
                button.setEnabled(false); button.setAlpha(0.35f);
            }
        }
    }
    private void answer(String value){
        if(answered)return;answered=true;joker.setEnabled(false);progress.setProgress(index+1);selected=value;String correct=BANK[order.get(index)][2];boolean ok=correct.equals(value);
        if(ok){streak++;correctCount++;earned=QuizRules.points(streak);score+=earned;feedback.setText("✦ Bravo !  +"+earned+" points");}
        else {streak=0;feedback.setText("La réponse : "+correct);}
        feedback.setTextColor(ok?GOLD:WHITE);
        header.setText("MANCHE  "+(index+1)+" / "+ROUND+"     ✦  "+score);
        for(int i=0;i<choices.getChildCount();i++){
            Button b=(Button)choices.getChildAt(i);b.setEnabled(false);
            if(b.getText().toString().equals(correct))b.setBackground(shape(Color.rgb(36,105,88),dp(16)));
            else if(b.getText().toString().equals(value))b.setBackground(shape(Color.rgb(125,57,70),dp(16)));
        }
        next.setText(index==ROUND-1?"Voir le résultat":"Continuer");next.setVisibility(View.VISIBLE);
    }
    private void finishRound(){
        best=Math.max(score,getPreferences(MODE_PRIVATE).getInt("best",0));getPreferences(MODE_PRIVATE).edit().putInt("best",best).apply();
        choices.removeAllViews();joker.setVisibility(View.GONE);progress.setProgress(ROUND);header.setText("PARTIE TERMINÉE");category.setText("✦  BRAVO  ✦");
        question.setText(score+" points");feedback.setText(correctCount+" bonnes réponses sur "+ROUND+"\nRecord : "+best+" points\nUne nouvelle partie ?");feedback.setTextColor(GOLD);
        next.setText("Rejouer");next.setVisibility(View.VISIBLE);
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
        android.widget.ScrollView scroll=new android.widget.ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(22),dp(24),dp(22),dp(24));scroll.addView(root);
        header=label("",14,GOLD);header.setTypeface(null,Typeface.BOLD);root.addView(header);
        progress=new android.widget.ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setMax(ROUND); progress.setProgressTintList(android.content.res.ColorStateList.valueOf(GOLD));
        LinearLayout.LayoutParams progressParams=new LinearLayout.LayoutParams(-1,dp(6));
        progressParams.setMargins(dp(24),dp(16),dp(24),0); root.addView(progress,progressParams);
        category=label("",14,GOLD);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(24),0,dp(8));root.addView(category,cp);
        question=label("",25,WHITE);question.setTypeface(null,Typeface.BOLD);root.addView(question);
        choices=new LinearLayout(this);choices.setOrientation(LinearLayout.VERTICAL);LinearLayout.LayoutParams choicesP=new LinearLayout.LayoutParams(-1,-2);choicesP.setMargins(0,dp(22),0,0);root.addView(choices,choicesP);
        joker=new Button(this); joker.setAllCaps(false); joker.setTextSize(14); joker.setTextColor(GOLD);
        joker.setBackground(shape(CARD,dp(12))); joker.setMinHeight(dp(48));
        LinearLayout.LayoutParams jp=new LinearLayout.LayoutParams(-1,-2);jp.setMargins(0,dp(8),0,0);root.addView(joker,jp);
        joker.setOnClickListener(v->useJoker());
        feedback=label("",16,GOLD);LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2);fp.setMargins(0,dp(16),0,dp(12));root.addView(feedback,fp);
        next=new Button(this);next.setAllCaps(false);next.setTextColor(BG);next.setTextSize(17);next.setBackground(shape(GOLD,dp(16)));next.setMinHeight(dp(56)); root.addView(next,new LinearLayout.LayoutParams(-1,-2));
        next.setOnClickListener(v->{if(showingHome || index>=ROUND){newGame();}else if(index==ROUND-1){index++;finishRound();}else{index++;showQuestion();}});
        setContentView(scroll);
        if(saved!=null && saved.containsKey("order")){
            int[] arr=saved.getIntArray("order"); if(arr!=null) for(int x:arr)order.add(x);index=saved.getInt("index");score=saved.getInt("score");streak=saved.getInt("streak");correctCount=saved.getInt("correctCount");jokerUsed=saved.getBoolean("jokerUsed");hiddenOptions=saved.getStringArray("hiddenOptions");
            if(saved.getBoolean("home") || order.size()<ROUND) showHome(); else if(index>=ROUND)finishRound();else {showQuestion(saved.getStringArray("options")); String previous=saved.getString("selected"); if(previous!=null){int oldScore=score, oldStreak=streak, oldCorrect=correctCount; answer(previous); score=oldScore; streak=oldStreak; correctCount=oldCorrect; header.setText("MANCHE  "+(index+1)+" / "+ROUND+"     ✦  "+score); earned=saved.getInt("earned"); if(previous.equals(BANK[order.get(index)][2])) feedback.setText("✦ Bravo !  +"+earned+" points");}}
        }else showHome();
    }
    @Override protected void onSaveInstanceState(Bundle out){
        int[] arr=new int[order.size()];for(int i=0;i<arr.length;i++)arr[i]=order.get(i);out.putIntArray("order",arr);out.putInt("index",index);out.putInt("score",score);out.putInt("streak",streak);out.putInt("correctCount",correctCount);out.putBoolean("home",showingHome);out.putBoolean("jokerUsed",jokerUsed);if(hiddenOptions!=null)out.putStringArray("hiddenOptions",hiddenOptions); if(!showingHome && index<ROUND && options!=null)out.putStringArray("options",options); if(answered){out.putString("selected",selected);out.putInt("earned",earned);}
        super.onSaveInstanceState(out);
    }
}
