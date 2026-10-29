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
    private static final String[] CHORD_NAMES={"Do maj7","Fa maj7","Sol 7"};
    private static final double[][] CHORDS={
        {261.63,329.63,392.00,493.88}, {349.23,440.00,523.25,659.25},
        {392.00,493.88,587.33,698.46}
    };
    private LinearLayout chords, listenRow;
    private final ExecutorService audio=Executors.newSingleThreadExecutor();
    private final Random random=new Random();
    private int[] phrase;
    private int position=0, wins=0;
    private volatile int generation=0;
    private volatile boolean playing=false;
    private boolean freeMode=false, slowMode=false, octaveHigh=false;
    private TextView status, soundWarning;
    private Button listenButton, modeButton, tempoButton, octaveButton;

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
        runOnUiThread(()->{if(token==generation && !isFinishing()){
            AudioManager manager=(AudioManager)getSystemService(AUDIO_SERVICE);
            soundWarning.setText(manager!=null && manager.getStreamVolume(AudioManager.STREAM_MUSIC)==0
                ? "Le volume multimédia est coupé." : "Son indisponible : vérifie le volume ou réessaie.");
        }});
    }
    private void playNote(int note,int token){playNote(note,token,false);}
    private void playNote(int note,int token,boolean high){
        double frequency=FREQ[note]*(high ? 2 : 1);
        playSamples(SoundWave.note(frequency),token,"note "+NAMES[note]+(high ? " haute" : ""));
    }
    private void playSamples(short[] data,int token,String description){
        if(token!=generation || Thread.currentThread().isInterrupted())return;
        AudioTrack track=null;
        try {
            int minimum=AudioTrack.getMinBufferSize(SoundWave.RATE,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT);
            if(minimum<=0){android.util.Log.w("SurpriseAudio","unsupported format: "+minimum);warnSound(token);return;}
            int bufferBytes=Math.max(data.length*2,minimum);
            track=new AudioTrack(AudioManager.STREAM_MUSIC,SoundWave.RATE,AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,bufferBytes,AudioTrack.MODE_STATIC);
            int state=track.getState();
            // Static tracks report NO_STATIC_DATA until the first successful write.
            if(state!=AudioTrack.STATE_INITIALIZED && state!=AudioTrack.STATE_NO_STATIC_DATA){
                android.util.Log.w("SurpriseAudio","initialization failed: "+state);warnSound(token);return;
            }
            int written=track.write(data,0,data.length);
            if(written!=data.length || track.getState()!=AudioTrack.STATE_INITIALIZED){
                android.util.Log.w("SurpriseAudio","write failed: "+written+" state="+track.getState());warnSound(token);return;
            }
            track.play();
            if(track.getPlayState()!=AudioTrack.PLAYSTATE_PLAYING){android.util.Log.w("SurpriseAudio","play failed");warnSound(token);return;}
            android.util.Log.i("SurpriseAudio","playback started: "+description);
            // Check cancellation frequently so rapid free play or leaving the
            // screen never leaves a long backlog of stale notes.
            for(int elapsed=0;elapsed<260 && token==generation;elapsed+=10)Thread.sleep(10);
        } catch(InterruptedException interrupted){Thread.currentThread().interrupt();}
        catch(IllegalArgumentException | IllegalStateException failure){android.util.Log.w("SurpriseAudio","playback exception",failure);warnSound(token);}
        finally {if(track!=null){try{track.stop();}catch(IllegalStateException ignored){}track.release();}}
    }
    private void checkVolume(){
        AudioManager manager=(AudioManager)getSystemService(AUDIO_SERVICE);
        soundWarning.setText(manager!=null && manager.getStreamVolume(AudioManager.STREAM_MUSIC)==0 ? "Le volume multimédia est coupé." : "");
    }
    private void listen(){
        if(playing || freeMode)return;
        position=0;playing=true;final int token=++generation;final int[] copy=phrase.clone();final boolean slow=slowMode;
        checkVolume();
        status.setText("Écoute bien…");
        audio.execute(()->{
            android.util.Log.i("SurpriseAudio","melody playback started");
            for(int i=0;i<copy.length;i++){
                if(token!=generation || Thread.currentThread().isInterrupted())break;
                playNote(copy[i],token);
                if(slow && i<copy.length-1){
                    try{
                        for(int ms=0;ms<200 && token==generation;ms+=10)Thread.sleep(10);
                    }catch(InterruptedException interrupted){Thread.currentThread().interrupt();break;}
                }
            }
            runOnUiThread(()->{if(token==generation && !isFinishing()){playing=false;status.setText("À toi de jouer !");}});
        });
    }
    private int[] nextPhrase(int count, int[] previous){
        int[] next=new int[count];
        for(int i=0;i<count;i++){
            int candidate=random.nextInt(NAMES.length);
            if(i>0 && candidate==next[i-1]){
                // Every note except the previous one remains equally likely.
                candidate=(candidate+1+random.nextInt(NAMES.length-1))%NAMES.length;
            }
            next[i]=candidate;
        }
        if(previous!=null && java.util.Arrays.equals(previous,next)){
            int original=next[count-1];
            do{next[count-1]=random.nextInt(NAMES.length);}
            while(next[count-1]==original || next[count-1]==next[count-2]);
        }
        return next;
    }
    private void choose(int note){
        if(playing)return;
        checkVolume();
        final int token=freeMode ? ++generation : generation;
        final boolean high=freeMode && octaveHigh;
        audio.execute(()->{if(token==generation)playNote(note,token,high);});
        if(freeMode){status.setText("Mode libre · "+NAMES[note]+(high ? " ↑" : ""));return;}
        if(note!=phrase[position]){
            position=0;status.setText("Presque ! Réécoute et réessaie.");return;
        }
        position++;
        if(position==phrase.length){
            wins++;
            int best=getPreferences(MODE_PRIVATE).getInt("melodies",0);
            getPreferences(MODE_PRIVATE).edit().putInt("melodies",Math.max(best,wins)).apply();
            int count=Math.min(6,3+wins/2);
            phrase=nextPhrase(count,phrase);
            position=0;status.setText("✦ Bravo ! "+wins+" mélodie"+(wins>1?"s":"")+" réussie"+(wins>1?"s":"")+"\nÉcoute la suivante.");
        }else status.setText("Bien joué : "+position+" / "+phrase.length+" notes");
    }
    private void chooseChord(int chord){
        if(!freeMode || playing)return;
        checkVolume();
        int token=++generation;
        audio.execute(()->{if(token==generation)playSamples(SoundWave.chord(CHORDS[chord]),token,"accord "+CHORD_NAMES[chord]);});
        status.setText("Mode libre · "+CHORD_NAMES[chord]);
    }
    private void switchMode(){
        generation++;playing=false;freeMode=!freeMode;soundWarning.setText("");
        modeButton.setText(freeMode ? "Revenir au défi" : "Jouer librement");
        listenButton.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        tempoButton.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        listenRow.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        chords.setVisibility(freeMode ? android.view.View.VISIBLE : android.view.View.GONE);
        octaveButton.setVisibility(freeMode ? android.view.View.VISIBLE : android.view.View.GONE);
        status.setText(freeMode ? "Mode libre · joue à ton rythme." : position>0 ? "Bien joué : "+position+" / "+phrase.length+" notes" : "Écoute la mélodie, puis joue-la.");
    }
    private void confirmLeaving(){
        if(wins==0 && position==0){finish();return;}
        new android.app.AlertDialog.Builder(this)
            .setTitle("Quitter le défi ?")
            .setMessage("La progression du défi en cours sera perdue.")
            .setNegativeButton("Continuer",(dialog,which)->dialog.dismiss())
            .setPositiveButton("Quitter",(dialog,which)->finish())
            .show();
    }
    @Override public void onBackPressed(){confirmLeaving();}
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);phrase=nextPhrase(3,null);
        slowMode=getPreferences(MODE_PRIVATE).getBoolean("slowMode",false);
        octaveHigh=getPreferences(MODE_PRIVATE).getBoolean("octaveHigh",false);setVolumeControlStream(AudioManager.STREAM_MUSIC);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
        if(saved!=null){
            int[] p=saved.getIntArray("phrase");
            if(p!=null && p.length>=3 && p.length<=6){phrase=p;position=Math.max(0,Math.min(saved.getInt("position"),p.length-1));}
            wins=Math.max(0,saved.getInt("wins"));freeMode=saved.getBoolean("freeMode",false);slowMode=saved.getBoolean("slowMode",slowMode);octaveHigh=saved.getBoolean("octaveHigh",octaveHigh);
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
        listenRow=new LinearLayout(this);listenRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(56));lp.setMargins(0,dp(14),0,dp(12));root.addView(listenRow,lp);
        listenButton=button("Écouter la mélodie");listenButton.setTextSize(15);
        listenRow.addView(listenButton,new LinearLayout.LayoutParams(0,dp(56),3));listenButton.setOnClickListener(v->listen());
        tempoButton=button(slowMode ? "Normal" : "Lent");tempoButton.setTextSize(14);
        LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,dp(56),1);tp.setMargins(dp(6),0,0,0);
        listenRow.addView(tempoButton,tp);
        tempoButton.setContentDescription(slowMode ? "Revenir à la vitesse normale" : "Ralentir la lecture");
        tempoButton.setOnClickListener(v->{slowMode=!slowMode;getPreferences(MODE_PRIVATE).edit().putBoolean("slowMode",slowMode).apply();tempoButton.setText(slowMode ? "Normal" : "Lent");tempoButton.setContentDescription(slowMode ? "Revenir à la vitesse normale" : "Ralentir la lecture");});
        listenButton.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        tempoButton.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        listenRow.setVisibility(freeMode ? android.view.View.GONE : android.view.View.VISIBLE);
        for(int row=0;row<2;row++){
            LinearLayout line=new LinearLayout(this);line.setOrientation(LinearLayout.HORIZONTAL);
            for(int col=0;col<3;col++){
                int i=row*3+col;if(i>=NAMES.length)break;
                Button b=button(NAMES[i]);b.setContentDescription("Note "+NAMES[i]);
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(64),1);p.setMargins(dp(4),dp(5),dp(4),dp(5));line.addView(b,p);b.setOnClickListener(v->choose(i));
            }root.addView(line,new LinearLayout.LayoutParams(-1,-2));
        }
        chords=new LinearLayout(this);chords.setOrientation(LinearLayout.HORIZONTAL);
        for(int i=0;i<CHORD_NAMES.length;i++){
            final int chord=i;
            Button b=button(CHORD_NAMES[i]);b.setTextSize(14);
            b.setContentDescription("Accord "+CHORD_NAMES[i]);
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(56),1);
            p.setMargins(dp(4),dp(8),dp(4),0);chords.addView(b,p);
            b.setOnClickListener(v->chooseChord(chord));
        }
        root.addView(chords,new LinearLayout.LayoutParams(-1,-2));
        chords.setVisibility(freeMode ? android.view.View.VISIBLE : android.view.View.GONE);
        octaveButton=button(octaveHigh ? "Octave normale" : "Octave +");
        LinearLayout.LayoutParams op=new LinearLayout.LayoutParams(-1,dp(52));op.setMargins(0,dp(10),0,0);
        root.addView(octaveButton,op);
        octaveButton.setVisibility(freeMode ? android.view.View.VISIBLE : android.view.View.GONE);
        octaveButton.setOnClickListener(v->{
            octaveHigh=!octaveHigh;
            getPreferences(MODE_PRIVATE).edit().putBoolean("octaveHigh",octaveHigh).apply();
            octaveButton.setText(octaveHigh ? "Octave normale" : "Octave +");
            status.setText(octaveHigh ? "Mode libre · octave haute" : "Mode libre · octave normale");
        });
        modeButton=button(freeMode ? "Revenir au défi" : "Jouer librement");
        LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(-1,dp(56));mp.setMargins(0,dp(12),0,0);root.addView(modeButton,mp);modeButton.setOnClickListener(v->switchMode());
        Button home=button("Retour à l’accueil");LinearLayout.LayoutParams back=new LinearLayout.LayoutParams(-1,dp(56));back.setMargins(0,dp(12),0,0);root.addView(home,back);home.setOnClickListener(v->confirmLeaving());
        setContentView(scroll);
    }
    @Override protected void onStop(){
        generation++;
        if(playing && !freeMode){position=0;status.setText("Lecture interrompue · réécoute la mélodie.");}
        playing=false;super.onStop();
    }
    @Override protected void onDestroy(){generation++;audio.shutdownNow();super.onDestroy();}
    @Override protected void onSaveInstanceState(Bundle out){
        out.putIntArray("phrase",phrase);out.putInt("position",position);out.putInt("wins",wins);out.putBoolean("freeMode",freeMode);out.putBoolean("slowMode",slowMode);out.putBoolean("octaveHigh",octaveHigh);super.onSaveInstanceState(out);
    }
}
