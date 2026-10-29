package ch.fred.surprise;

public final class SoundWaveTest {
    public static void main(String[] args){
        for(double hz:new double[]{261.63,293.66,329.63,392,440}){
            short[] wave=SoundWave.note(hz);
            if(wave.length!=5292 || wave[0]!=0 || wave[wave.length-1]!=0)throw new AssertionError("envelope");
            int crossings=0,peak=0;
            for(int i=1;i<wave.length;i++){
                if(wave[i-1]<0 && wave[i]>=0)crossings++;
                peak=Math.max(peak,Math.abs((int)wave[i]));
            }
            if(peak<1000 || peak>10000)throw new AssertionError("amplitude "+peak);
            if(Math.abs(crossings-hz*wave.length/SoundWave.RATE)>2)throw new AssertionError("pitch "+crossings);
        }
        double[] frequencies={261.63,329.63,392.00,493.88};
        short[] chord=SoundWave.chord(frequencies);
        if(chord.length!=5292 || chord[0]!=0 || chord[chord.length-1]!=0)throw new AssertionError("chord envelope");
        int peak=0;
        for(short sample:chord)peak=Math.max(peak,Math.abs((int)sample));
        if(peak<500 || peak>10000)throw new AssertionError("chord amplitude "+peak);
        for(double hz:frequencies){
            double sin=0,cos=0;
            for(int i=0;i<chord.length;i++){
                double phase=2*Math.PI*hz*i/SoundWave.RATE;
                sin+=chord[i]*Math.sin(phase);cos+=chord[i]*Math.cos(phase);
            }
            if(Math.hypot(sin,cos)<100000)throw new AssertionError("missing chord pitch "+hz);
        }
        try{SoundWave.chord();throw new AssertionError("empty chord accepted");}
        catch(IllegalArgumentException expected){}
        try{SoundWave.chord(261,329,392,440,523);throw new AssertionError("too many voices accepted");}
        catch(IllegalArgumentException expected){}
        try{SoundWave.chord(261,Double.NaN);throw new AssertionError("invalid pitch accepted");}
        catch(IllegalArgumentException expected){}
        try{SoundWave.note(Double.NaN);throw new AssertionError("NaN accepted");}
        catch(IllegalArgumentException expected){}
        try{SoundWave.note(0);throw new AssertionError("zero accepted");}
        catch(IllegalArgumentException expected){}
        System.out.println("Sound wave tests passed");
    }
}
