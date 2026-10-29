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
        try{SoundWave.note(Double.NaN);throw new AssertionError("NaN accepted");}
        catch(IllegalArgumentException expected){}
        try{SoundWave.note(0);throw new AssertionError("zero accepted");}
        catch(IllegalArgumentException expected){}
        System.out.println("Sound wave tests passed");
    }
}
