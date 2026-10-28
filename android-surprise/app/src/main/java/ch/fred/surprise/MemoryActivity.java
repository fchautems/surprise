package ch.fred.surprise;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

/** A quiet, offline matching game: no timer, flashes or network access. */
public final class MemoryActivity extends Activity {
    private static final int BG=Color.rgb(17,22,42), CARD=Color.rgb(31,42,68);
    private static final int GOLD=Color.rgb(255,195,107), WHITE=Color.rgb(245,242,235);
    private static final String[] SYMBOLS={"♪","☕","✦","♛","☀","♥","♣","◆"};
    private static final int PAIRS=4;
    private final int[] deck=new int[8];
    private final boolean[] matched=new boolean[8];
    private final Button[] cards=new Button[8];
    private int first=-1,second=-1,moves=0;
    private TextView status;
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private GradientDrawable shape(int color,int border){
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(16));
        if(border!=0)d.setStroke(dp(2),border); return d;
    }
    private TextView label(String text,int size,int color){
        TextView t=new TextView(this); t.setText(text); t.setTextSize(size);
        t.setTextColor(color); t.setGravity(Gravity.CENTER); return t;
    }
    private Button button(String text){
        Button b=new Button(this); b.setText(text); b.setAllCaps(false);
        b.setTextColor(WHITE); b.setTextSize(16); b.setBackgroundTintList(null);
        b.setBackground(shape(CARD,GOLD)); b.setMinHeight(dp(52)); return b;
    }
    private void newGame(){
        ArrayList<Integer> available=new ArrayList<>(), values=new ArrayList<>();
        for(int i=0;i<SYMBOLS.length;i++)available.add(i);
        Collections.shuffle(available);
        for(int i=0;i<PAIRS;i++){int value=available.get(i);values.add(value);values.add(value);}
        Collections.shuffle(values);
        for(int i=0;i<deck.length;i++){deck[i]=values.get(i);matched[i]=false;}
        first=second=-1; moves=0; render();
    }
    private int pairsFound(){
        int count=0; for(boolean b:matched)if(b)count++; return count/2;
    }
    private void select(int i){
        if(matched[i] || i==first || i==second)return;
        if(second!=-1){first=second=-1;}
        if(first==-1){first=i;}else{
            second=i; moves++;
            if(deck[first]==deck[second]){
                matched[first]=matched[second]=true; first=second=-1;
                if(pairsFound()==PAIRS){
                    android.content.SharedPreferences p=getPreferences(MODE_PRIVATE);
                    int best=p.getInt("best",Integer.MAX_VALUE);
                    p.edit().putInt("best",Math.min(best,moves)).apply();
                }
            }
        }
        render();
    }
    private void render(){
        int pairs=pairsFound();
        int best=getPreferences(MODE_PRIVATE).getInt("best",0);
        status.setText(pairs==PAIRS
            ? "✦ Bravo ! "+moves+" essais\nRecord : "+best
            : pairs+" / 4 paires  ·  "+moves+" essais"+(best>0 ? "\nRecord : "+best : "")+(second!=-1 ? "\nChoisis une autre carte." : ""));
        for(int i=0;i<cards.length;i++){
            boolean visible=matched[i] || i==first || i==second;
            String value=visible ? SYMBOLS[deck[i]] : "?";
            cards[i].setText(value); cards[i].setEnabled(!matched[i]);
            cards[i].setContentDescription("Carte "+(i+1)+" : "+(matched[i] ? "trouvée " : visible ? "visible " : "cachée ")+value);
            cards[i].setBackground(shape(matched[i] ? Color.rgb(36,105,88) : CARD,visible ? GOLD : Color.rgb(100,115,145)));
        }
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved); getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.setBackgroundColor(BG);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER); root.setPadding(dp(18),dp(20),dp(18),dp(20)); scroll.addView(root);
        scroll.setOnApplyWindowInsetsListener((view,insets)->{root.setPadding(dp(18),dp(20)+insets.getSystemWindowInsetTop(),dp(18),dp(20)+insets.getSystemWindowInsetBottom());return insets;});
        TextView title=label("✦  MÉMO DES ÉTOILES  ✦",21,GOLD);
        title.setTypeface(null,Typeface.BOLD); root.addView(title);
        TextView hint=label("Retrouve les quatre paires, à ton rythme.",15,WHITE);
        LinearLayout.LayoutParams hintP=new LinearLayout.LayoutParams(-1,-2); hintP.setMargins(0,dp(10),0,dp(12)); root.addView(hint,hintP);
        status=label("",16,GOLD); root.addView(status);
        for(int row=0;row<4;row++){
            LinearLayout line=new LinearLayout(this); line.setOrientation(LinearLayout.HORIZONTAL);
            for(int col=0;col<2;col++){
                int i=row*2+col; Button card=button("?"); card.setTextSize(28); cards[i]=card;
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(58),1); p.setMargins(dp(5),dp(5),dp(5),dp(5));
                line.addView(card,p); card.setOnClickListener(v->select(i));
            }
            root.addView(line,new LinearLayout.LayoutParams(-1,-2));
        }
        Button reset=button("Nouvelle partie"); LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(52));
        rp.setMargins(dp(5),dp(10),dp(5),0); root.addView(reset,rp); reset.setOnClickListener(v->newGame());
        Button home=button("Retour à l’accueil"); LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,dp(52));
        hp.setMargins(dp(5),dp(8),dp(5),0); root.addView(home,hp); home.setOnClickListener(v->finish());
        setContentView(scroll);
        if(saved!=null && saved.getIntArray("deck")!=null && saved.getBooleanArray("matched")!=null){
            int[] d=saved.getIntArray("deck"); boolean[] m=saved.getBooleanArray("matched");
            if(d.length==8 && m.length==8){
                System.arraycopy(d,0,deck,0,8);System.arraycopy(m,0,matched,0,8);
                first=saved.getInt("first",-1);second=saved.getInt("second",-1);moves=saved.getInt("moves",0);render();
            }else newGame();
        }else newGame();
    }
    @Override protected void onSaveInstanceState(Bundle out){
        out.putIntArray("deck",deck);out.putBooleanArray("matched",matched);
        out.putInt("first",first);out.putInt("second",second);out.putInt("moves",moves);
        super.onSaveInstanceState(out);
    }
}
