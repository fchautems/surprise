package ch.fred.surprise;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** A small listening game, offline and without flashing or a timer. */
public final class MusicActivity extends Activity {
    private static final int BG=Color.rgb(17,22,42), CARD=Color.rgb(31,42,68);
    private static final int GOLD=Color.rgb(255,195,107), WHITE=Color.rgb(245,242,235);
    private static final String[] NAMES={"Do","Ré","Mi","Sol","La"};
    private static final double[] FREQ={261.63,293.66,329.63,392.00,440.00};
    private final ExecutorService audio=Executors.newSingleThreadExecutor();
    private final Random random=new Random();
    private int[] phrase={0,2,4};
    private int position=0, wins=0;
    private volatile int generation=0;
    private volatile boolean playing=false;
    private boolean freeMode=false;
    private TextView status, soundWarning;
    private Button listenButton, modeButton;

    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private GradientDrawable shape(int color,int border){
        GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(16));
        if(border!=0)d.setStroke(dp(2),border);return d;
    }
    private TextView label(String text,int size,int color){
        TextView t=new TextView(this);t.setText(text);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);return t;
    }
    private Button button(String text){
        Button b=new Button(this);b.setAllCaps(false);b.setText(text);b.setTextSize(17);b.setTextColor(WHITE);
        b.setBackgroundTintList(null);b.setBackground(shape(CARD,GOLD));b.setMinHeight(dp(56));return b;
    }
    private void warnSound(int token){
        runOnUiThread(()->{if(token==generation && !isFinishing())soundWarning.setText("Son indisponible : vérifie le volume ou réessaie.");});
    }
    private void playNote(int note,int token){
        short[] data=SoundWave.note(FREQ[note]);
        AudioTrack track=null;
        try {
            track=new AudioTrack(AudioManager.STREAM_MUSIC,SoundWave.RATE,AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,data.length*2,AudioTrack.MODE_STATIC);
            if(track.getState()!=AudioTrack.STATE_INITIALIZED){warnSound(token);return;}
            if(track.write(data,0,data.length)!=data.length){warnSound(token);return;}
            track.play();Thread.sleep(260);
        } catch(InterruptedException interrupted){Thread.currentThread().interrupt();}
        catch(IllegalArgumentException | IllegalStateException failure){warnSound(token);}
        finally {if(track!=null){try{track.stop();}catch(IllegalStateException ignored){}track.release();}}
    }
    private void checkVolume(){
        AudioManager manager=(AudioManager)getSystemService(AUDIO_SERVICE);
        soundWarning.setText(manager!=null && manager.getStreamVolume(AudioManager.STREAM_MUSIC)==0 ? "Le volume multimédia est coupé." : "");
    }
    private void listen(){
        if(playing || freeMode)return;
        position=0;playing=true;final int token=++generation;final int[] copy=phrase.clone();
        checkVolume();
        status.setText("Écoute bien…");
        audio.execute(()->{
            for(int note:copy){if(token!=generation || Thread.currentThread().isInterrupted())break;playNote(note,token);}
            runOnUiThread(()->{if(token==generation && !isFinishing()){playing=false;status.setText("À toi de jouer !");}});
        });
    }
    private void choose(int note){
        if(playing)return;
        checkVolume();
        final int token=generation;
        audio.execute(()->{if(token==generation)playNote(note,token);});
        if(freeMode){status.setText("Mode libre · "+NAMES[note]);return;}
        if(note!=phrase[position]){
            position=0;status.setText("Presque ! Réécoute et réessaie.");return;
        }
        position++;
        if(position==phrase.length){
            wins++;
            int best=getPreferences(MODE_PRIVATE).getInt("melodies",0);
            getPreferences(MODE_PRIVATE).edit().putInt("melodies",Math.max(best,wins)).apply();
            int count=Math.min(6,3+wins/2);
            phrase=new int[count];for(int i=0;i<count;i++)phrase[i]=random.nextInt(NAMES.length);
            position=0;status.setText("✦ Bravo ! "+wins+" mélodie"+(wins>1?"s":"")+" réussie"+(wins>1?"s":"")+"\nÉcoute la suivante.");
        }else status.setText("Bien joué : "+position+" / "+phrase.length+" notes");
    }
    private void switchMode(){
        generation++;playing=false;freeMode=!freeMode;soundWarning.setText("");
        modeButton.setText(freeMode ? "Revenir au défi" : "Jouer librement");
        listenButton.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        status.setText(freeMode ? "Mode libre · joue à ton rythme." : "Écoute la mélodie, puis joue-la.");
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);setVolumeControlStream(AudioManager.STREAM_MUSIC);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
        if(saved!=null){
            int[] p=saved.getIntArray("phrase");
            if(p!=null && p.length>=3 && p.length<=6){phrase=p;position=Math.max(0,Math.min(saved.getInt("position"),p.length-1));}
            wins=Math.max(0,saved.getInt("wins"));freeMode=saved.getBoolean("freeMode",false);
        }
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(BG);
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);
        root.setPadding(dp(18),dp(24),dp(18),dp(24));scroll.addView(root);
        scroll.setOnApplyWindowInsetsListener((view,insets)->{root.setPadding(dp(18),dp(24)+insets.getSystemWindowInsetTop(),dp(18),dp(24)+insets.getSystemWindowInsetBottom());return insets;});
        TextView title=label("♫  PETITES MÉLODIES  ♫",21,GOLD);title.setTypeface(null,Typeface.BOLD);root.addView(title);
        TextView hint=label("Écoute, puis retrouve les notes. Monte le son si tu veux !",15,WHITE);
        LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.setMargins(0,dp(12),0,dp(10));root.addView(hint,hp);
        int best=getPreferences(MODE_PRIVATE).getInt("melodies",0);
        status=label(freeMode ? "Mode libre · joue à ton rythme." : position>0 ? "Bien joué : "+position+" / "+phrase.length+" notes" : best>0?"Record : "+best+" · Écoute la mélodie.":"Écoute la mélodie, puis joue-la.",16,GOLD);root.addView(status);
        soundWarning=label("",14,WHITE);root.addView(soundWarning);
        listenButton=button("Écouter la mélodie");LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(56));lp.setMargins(0,dp(14),0,dp(12));root.addView(listenButton,lp);listenButton.setOnClickListener(v->listen());
        listenButton.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        for(int row=0;row<2;row++){
            LinearLayout line=new LinearLayout(this);line.setOrientation(LinearLayout.HORIZONTAL);
            for(int col=0;col<3;col++){
                int i=row*3+col;if(i>=NAMES.length)break;
                Button b=button(NAMES[i]);b.setContentDescription("Note "+NAMES[i]);
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(64),1);p.setMargins(dp(4),dp(5),dp(4),dp(5));line.addView(b,p);b.setOnClickListener(v->choose(i));
            }root.addView(line,new LinearLayout.LayoutParams(-1,-2));
        }
        modeButton=button(freeMode ? "Revenir au défi" : "Jouer librement");
        LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,dp(56));mp.setMargins(0,dp(12),0,0);root.addView(modeButton,mp);modeButton.setOnClickListener(v->switchMode());
        Button home=button("Retour à l’accueil");LinearLayout.LayoutParams back=new LinearLayout.LayoutParams(-1,dp(56));back.setMargins(0,dp(12),0,0);root.addView(home,back);home.setOnClickListener(v->finish());
        setContentView(scroll);
    }
    @Override protected void onStop(){generation++;playing=false;super.onStop();}
    @Override protected void onDestroy(){generation++;audio.shutdownNow();super.onDestroy();}
    @Override protected void onSaveInstanceState(Bundle out){
        out.putIntArray("phrase",phrase);out.putInt("position",position);out.putInt("wins",wins);out.putBoolean("freeMode",freeMode);super.onSaveInstanceState(out);
    }
}
