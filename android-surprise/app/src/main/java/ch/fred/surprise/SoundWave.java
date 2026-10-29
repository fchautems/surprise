package ch.fred.surprise;

/** Short, smooth synthesized notes. Pure math, independent of Android. */
public final class SoundWave {
    private SoundWave() {}
    public static final int RATE=22050;
    public static short[] note(double frequency) {
        if (!(frequency>=100 && frequency<=2000)) throw new IllegalArgumentException("frequency");
        int count=RATE*240/1000;
        short[] samples=new short[count];
        for(int i=0;i<count;i++){
            double t=(double)i/RATE;
            double attack=Math.min(1.0,i/(RATE*0.015));
            double release=Math.min(1.0,(count-1-i)/(RATE*0.05));
            double envelope=attack*release*Math.exp(-2.5*t);
            double wave=Math.sin(2*Math.PI*frequency*t)+0.22*Math.sin(4*Math.PI*frequency*t);
            samples[i]=(short)(wave*envelope*7000);
        }
        return samples;
    }
}
