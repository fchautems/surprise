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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

/** A quiet, offline matching game: no timer, flashes or network access. */
public final class MemoryActivity extends Activity {
    private static final int BG=Color.rgb(17,22,42), CARD=Color.rgb(31,42,68);
    private static final int GOLD=Color.rgb(255,195,107), WHITE=Color.rgb(245,242,235);
    private static final String[] SYMBOLS={"♪","☕","✦","♛","☀","♥","♣","◆","♫","★","♠","☁","♬","◉","☂","⌛"};
    private static final String[] ESCAPADE={"🎹","🍕","⌚","☕","🌙","🎷","🍇","🏔","🎸","🚂","⭐","🍋","🌻","🎺","🐈","🎲"};
    private static final String[] NATURE={"🌸","🌿","🍀","🌵","🍄","🌼","🍁","🌳","🐝","🦋","🐞","🐬","🦉","🐢","🐟","🌷"};
    private static final int[] INKS={0xffffd27d,0xffaee9e0,0xffffb9b9,0xffcbbdff,
        0xffa6d7ff,0xffffc6e5,0xffcce8a4,0xffffd9a0};
    private static final int MAX_PAIRS=10;
    private final int[] deck=new int[MAX_PAIRS*2];
    private final boolean[] matched=new boolean[MAX_PAIRS*2];
    private final Button[] cards=new Button[MAX_PAIRS*2];
    private static final int MAX_ROWS=10;
    private final LinearLayout[] rows=new LinearLayout[MAX_ROWS];
    private int columns=4;
    private int pairCount=4;
    private Button difficulty, hintButton, paletteButton;
    private int palette=0;
    private boolean hintUsed=false, hintVisible=false;
    private ScrollView gameScroll;
    private int first=-1,second=-1,moves=0,streak=0,bestStreak=0;
    private TextView status;
    private ProgressBar progress;
    private boolean newRecord=false;
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private GradientDrawable shape(int color,int border){
        GradientDrawable d=color==CARD
            ? new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.rgb(39,53,80),CARD})
            : new GradientDrawable();if(color!=CARD)d.setColor(color);d.setCornerRadius(dp(16));
        if(border!=0)d.setStroke(dp(2),border); return d;
    }
    private android.graphics.drawable.Drawable touch(GradientDrawable base){
        return new android.graphics.drawable.RippleDrawable(android.content.res.ColorStateList.valueOf(Color.argb(40,255,255,255)),base,null);
    }
    private TextView label(String text,int size,int color){
        TextView t=new TextView(this); t.setText(text); t.setTextSize(size);
        t.setTextColor(color); t.setGravity(Gravity.CENTER); return t;
    }
    private Button button(String text){
        Button b=new Button(this); b.setText(text); b.setAllCaps(false);
        b.setTextColor(WHITE); b.setTextSize(16); b.setBackgroundTintList(null);
        b.setBackground(touch(shape(CARD,GOLD))); b.setMinHeight(dp(52)); return b;
    }
    private void newGame(){
        boolean[] previous=new boolean[SYMBOLS.length]; int distinct=0;
        for(int i=0;i<pairCount*2;i++)if(!previous[deck[i]]){previous[deck[i]]=true;distinct++;}
        ArrayList<Integer> available=new ArrayList<>(), values=new ArrayList<>();
        for(int i=0;i<SYMBOLS.length;i++)available.add(i);
        Collections.shuffle(available);
        boolean repeated=distinct==pairCount;
        for(int i=0;i<pairCount;i++)repeated &= previous[available.get(i)];
        if(repeated)Collections.swap(available,pairCount-1,pairCount);
        for(int i=0;i<pairCount;i++){int value=available.get(i);values.add(value);values.add(value);}
        Collections.shuffle(values);
        for(int i=0;i<deck.length;i++){deck[i]=i<values.size()?values.get(i):0;matched[i]=false;}
        first=second=-1; moves=0; streak=bestStreak=0; newRecord=false; hintUsed=false; hintVisible=false; render();
        gameScroll.post(()->gameScroll.fullScroll(View.FOCUS_UP));
    }
    private String recordKey(){return pairCount==4 ? "best" : pairCount==6 ? "best6" : pairCount==8 ? "best8" : "best10";}
    private String streakKey(){return "streak_"+pairCount;}
    private int nextPairCount(){return pairCount==4 ? 6 : pairCount==6 ? 8 : pairCount==8 ? 10 : 4;}
    private int pairsFound(){
        int count=0; for(boolean b:matched)if(b)count++; return count/2;
    }
    private void select(int i){
        if(matched[i])return;
        if(hintVisible){first=second=-1;hintVisible=false;}
        if(i==first || i==second)return;
        if(second!=-1){first=second=-1;}
        if(first==-1){first=i;}else{
            second=i; moves++;
            if(deck[first]==deck[second]){
                streak++; bestStreak=Math.max(bestStreak,streak);
                matched[first]=matched[second]=true; first=second=-1;
                if(pairsFound()==pairCount){
                    android.content.SharedPreferences p=getPreferences(MODE_PRIVATE);
                    int best=p.getInt(recordKey(),Integer.MAX_VALUE);
                    newRecord=moves<best;
                    int previousStreak=p.getInt(streakKey(),0);
                    p.edit().putInt(recordKey(),Math.min(best,moves))
                        .putInt(streakKey(),Math.max(previousStreak,bestStreak)).apply();
                }
            }else streak=0;
        }
        render();
    }
    private void showHint(){
        if(hintUsed || pairsFound()==pairCount)return;
        for(int i=0;i<pairCount*2;i++){
            if(matched[i])continue;
            for(int j=i+1;j<pairCount*2;j++){
                if(!matched[j] && deck[i]==deck[j]){
                    first=i;second=j;hintUsed=true;hintVisible=true;moves+=2;streak=0;
                    render();return;
                }
            }
        }
    }
    private boolean hasUnfinishedGame(){
        return pairsFound()<pairCount && (moves>0 || first!=-1);
    }
    private void confirmLeaving(){
        if(!hasUnfinishedGame()){finish();return;}
        new android.app.AlertDialog.Builder(this)
            .setTitle("Quitter la partie ?")
            .setMessage("La partie en cours sera perdue.")
            .setNegativeButton("Continuer", (dialog, which)->dialog.dismiss())
            .setPositiveButton("Quitter", (dialog, which)->finish())
            .show();
    }
    @Override public void onBackPressed(){confirmLeaving();}
    private void changePalette(){
        if(hasUnfinishedGame()){
            new android.app.AlertDialog.Builder(this)
                .setTitle("Changer les cartes ?")
                .setMessage("La partie en cours sera perdue.")
                .setNegativeButton("Continuer",(dialog,which)->dialog.dismiss())
                .setPositiveButton("Changer",(dialog,which)->applyPalette())
                .show();
        }else applyPalette();
    }
    private void applyPalette(){
        palette=(palette+1)%3;
        getPreferences(MODE_PRIVATE).edit().putInt("palette",palette).apply();
        newGame();
    }
    private void applyDifficulty(){
        pairCount=nextPairCount();
        getPreferences(MODE_PRIVATE).edit().putInt("pairCount",pairCount).apply();
        newGame();
    }
    private void switchDifficulty(){
        if(hasUnfinishedGame()){
            new android.app.AlertDialog.Builder(this)
                .setTitle("Changer de défi ?")
                .setMessage("La partie en cours sera perdue.")
                .setNegativeButton("Continuer", (dialog, which)->dialog.dismiss())
                .setPositiveButton("Changer", (dialog, which)->applyDifficulty())
                .show();
        }else applyDifficulty();
    }
    private void render(){
        int pairs=pairsFound();
        int best=getPreferences(MODE_PRIVATE).getInt(recordKey(),0);
        int streakRecord=getPreferences(MODE_PRIVATE).getInt(streakKey(),0);
        int stars=MemoryRules.starsFor(pairCount,moves), bestStars=best>0 ? MemoryRules.starsFor(pairCount,best) : 0;
        String reward=stars==3 ? "✦ ✦ ✦" : stars==2 ? "✦ ✦" : "✦";
        status.setText(pairs==pairCount
            ? "✦ Bravo ! "+moves+" essais\nRecord : "+best+(newRecord ? "\nNouveau record !" : "")+"\nMeilleure série : "+bestStreak+"\nSérie record : "+streakRecord+" · Étoiles : "+bestStars+"/3\n"+reward
            : pairs+" / "+pairCount+" paires  ·  "+moves+" essais"+(streak>=2 ? "\nSérie : "+streak+" paires !" : "")+(best>0 ? "\nRecord : "+best+"\nÉtoiles : "+bestStars+"/3" : "")+(streakRecord>0 ? "\nSérie record : "+streakRecord : "")+(hintVisible ? "\nUne paire dévoilée · à toi de jouer !" : second!=-1 ? "\nChoisis une autre carte." : ""));
        progress.setMax(pairCount); progress.setProgress(pairs);
        progress.setContentDescription(pairs+" paires trouvées sur "+pairCount);
        hintButton.setEnabled(!hintUsed && pairs<pairCount);
        hintButton.setText(hintUsed ? "Indice utilisé" : "Indice · une fois (+2 essais)");
        paletteButton.setText(palette==0 ? "Cartes : escapade" : palette==1 ? "Cartes : nature" : "Cartes : étoiles");
        difficulty.setText(pairCount==4 ? "Défi : 6 paires" : pairCount==6 ? "Défi : 8 paires" : pairCount==8 ? "Défi : 10 paires" : "Mode : 4 paires");
        for(int row=0;row<rows.length;row++)rows[row].setVisibility(row*columns<pairCount*2 ? View.VISIBLE : View.GONE);
        for(int i=0;i<cards.length;i++){
            cards[i].setVisibility(i<pairCount*2 ? View.VISIBLE : View.INVISIBLE);
            boolean visible=matched[i] || i==first || i==second;
            String value=visible ? (palette==0 ? SYMBOLS : palette==1 ? ESCAPADE : NATURE)[deck[i]] : "?";
            cards[i].setText(value); cards[i].setEnabled(!matched[i]);
            cards[i].setTextColor(visible ? INKS[deck[i]%INKS.length] : WHITE);
            cards[i].setContentDescription("Carte "+(i+1)+" : "+(matched[i] ? "trouvée " : visible ? "visible " : "cachée ")+value);
            cards[i].setBackground(touch(shape(matched[i] ? Color.rgb(27,82,70) : CARD,visible ? GOLD : Color.rgb(100,115,145))));
        }
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        pairCount=getPreferences(MODE_PRIVATE).getInt("pairCount",4);
        if(pairCount!=4 && pairCount!=6 && pairCount!=8 && pairCount!=10)pairCount=4;
        android.content.SharedPreferences prefs=getPreferences(MODE_PRIVATE);
        palette=prefs.getInt("palette",prefs.getBoolean("escapade",false) ? 1 : 0);
        if(palette<0 || palette>2)palette=0;
        getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
        ScrollView scroll=new ScrollView(this); gameScroll=scroll; scroll.setFillViewport(true); scroll.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.rgb(20,31,53),BG,Color.rgb(34,25,44)}));
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER); root.setPadding(dp(18),dp(20),dp(18),dp(20)); scroll.addView(root);
        scroll.setOnApplyWindowInsetsListener((view,insets)->{root.setPadding(dp(18),dp(20)+insets.getSystemWindowInsetTop(),dp(18),dp(20)+insets.getSystemWindowInsetBottom());return insets;});
        TextView title=label("✦  MÉMO DES ÉTOILES  ✦",21,GOLD);
        title.setTypeface(null,Typeface.BOLD); root.addView(title);
        TextView hint=label("Retrouve les paires, à ton rythme.",15,WHITE);
        LinearLayout.LayoutParams hintP=new LinearLayout.LayoutParams(-1,-2); hintP.setMargins(0,dp(10),0,dp(12)); root.addView(hint,hintP);
        status=label("",16,GOLD); status.setLineSpacing(dp(2),1f); root.addView(status);
        progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(GOLD));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,dp(6)); pp.setMargins(dp(5),dp(8),dp(5),dp(4)); root.addView(progress,pp);
        int width=getResources().getConfiguration().screenWidthDp;
        columns=width>=268 ? 4 : width>=210 ? 3 : 2;
        for(int row=0;row<MAX_ROWS;row++){
            LinearLayout line=new LinearLayout(this); line.setOrientation(LinearLayout.HORIZONTAL); rows[row]=line;
            for(int col=0;col<columns;col++){
                int i=row*columns+col;
                if(i>=cards.length){
                    android.widget.Space spacer=new android.widget.Space(this);
                    LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(0,dp(58),1);
                    sp.setMargins(dp(5),dp(5),dp(5),dp(5)); line.addView(spacer,sp); continue;
                }
                Button card=button("?"); card.setTextSize(28); cards[i]=card;
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(58),1); p.setMargins(dp(5),dp(5),dp(5),dp(5));
                line.addView(card,p); card.setOnClickListener(v->select(i));
            }
            root.addView(line,new LinearLayout.LayoutParams(-1,-2));
        }
        hintButton=button(""); LinearLayout.LayoutParams ih=new LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT);
        ih.setMargins(dp(5),dp(10),dp(5),0);root.addView(hintButton,ih);
        hintButton.setOnClickListener(v->showHint());
        difficulty=button(""); LinearLayout.LayoutParams dp=new LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT);
        dp.setMargins(dp(5),dp(10),dp(5),0); root.addView(difficulty,dp); difficulty.setOnClickListener(v->switchDifficulty());
        paletteButton=button("");
        LinearLayout.LayoutParams paletteParams=new LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT);
        paletteParams.setMargins(dp(5),dp(10),dp(5),0);root.addView(paletteButton,paletteParams);
        paletteButton.setOnClickListener(v->changePalette());
        Button reset=button("Nouvelle partie"); LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.setMargins(dp(5),dp(10),dp(5),0); root.addView(reset,rp); reset.setOnClickListener(v->{
            if(hasUnfinishedGame()){
                new android.app.AlertDialog.Builder(this)
                    .setTitle("Nouvelle partie ?")
                    .setMessage("La partie en cours sera perdue.")
                    .setNegativeButton("Continuer", (dialog, which)->dialog.dismiss())
                    .setPositiveButton("Recommencer", (dialog, which)->newGame())
                    .show();
            }else newGame();
        });
        Button home=button("Retour à l’accueil"); LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,LinearLayout.LayoutParams.WRAP_CONTENT);
        hp.setMargins(dp(5),dp(8),dp(5),0); root.addView(home,hp); home.setOnClickListener(v->confirmLeaving());
        setContentView(scroll);
        if(saved!=null && saved.getIntArray("deck")!=null && saved.getBooleanArray("matched")!=null){
            int[] d=saved.getIntArray("deck"); boolean[] m=saved.getBooleanArray("matched");
            if(d.length==m.length && (d.length==deck.length || d.length==16)){
                pairCount=saved.getInt("pairCount",4); if(pairCount!=4 && pairCount!=6 && pairCount!=8 && pairCount!=10)pairCount=4;
                System.arraycopy(d,0,deck,0,d.length);System.arraycopy(m,0,matched,0,m.length);
                first=saved.getInt("first",-1);second=saved.getInt("second",-1);moves=saved.getInt("moves",0);streak=saved.getInt("streak",0);bestStreak=saved.getInt("bestStreak",0);
                newRecord=saved.getBoolean("newRecord",false);hintUsed=saved.getBoolean("hintUsed",false);hintVisible=saved.getBoolean("hintVisible",false);render();
            }else newGame();
        }else newGame();
    }
    @Override protected void onSaveInstanceState(Bundle out){
        out.putIntArray("deck",deck);out.putBooleanArray("matched",matched);out.putInt("pairCount",pairCount);
        out.putInt("first",first);out.putInt("second",second);out.putInt("moves",moves);out.putInt("streak",streak);out.putInt("bestStreak",bestStreak);out.putBoolean("newRecord",newRecord);out.putBoolean("hintUsed",hintUsed);out.putBoolean("hintVisible",hintVisible);
        super.onSaveInstanceState(out);
    }
}
