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
        {"MUSIQUE","Quel pianiste est surnommé le Duke ?","Duke Ellington","Louis Armstrong","Miles Davis","Django Reinhardt"},
        {"MUSIQUE","Quel instrument joue habituellement les notes graves dans un trio jazz ?","La contrebasse","La flûte piccolo","Le violon","Le triangle"},
        {"MUSIQUE","En musique, que désigne le tempo ?","La vitesse","La tonalité","Le volume uniquement","Le nom de l'accord"},
        {"MUSIQUE","Quel accord ajoute une septième mineure à une triade majeure ?","Un accord de septième de dominante","Un accord mineur simple","Un unisson","Une octave"},
        {"MUSIQUE","Qui a popularisé What a Wonderful World en 1967 ?","Louis Armstrong","Frédéric Chopin","Claude Debussy","Wolfgang Mozart"},
        {"CUISINE","Quel ingrédient donne sa couleur verte au guacamole classique ?","L'avocat","La fraise","La pomme de terre","Le chocolat"},
        {"CUISINE","Quelle plante aromatique est associée à la salade caprese ?","Le basilic","La sauge","La menthe","L'aneth"},
        {"CUISINE","À quoi sert principalement le pétrissage d'une pâte de blé ?","À développer le réseau de gluten","À remplacer la levure","À refroidir le four","À supprimer toute l'eau"},
        {"CUISINE","Quelle huile est issue du fruit de l'olivier ?","L'huile d'olive","L'huile de noix","L'huile de colza","L'huile de coco"},
        {"VOYAGE","Quelle monnaie utilise la Suisse ?","Le franc suisse","L'euro exclusivement","Le dollar canadien","La livre sterling"},
        {"VOYAGE","Quel pays partage avec la Suisse le lac Léman ?","La France","La Belgique","Le Portugal","La Grèce"},
        {"VOYAGE","Dans quelle ville suisse se trouve le Musée olympique ?","Lausanne","Sion","Bienne","Aarau"},
        {"CIEL","Comment appelle-t-on notre galaxie ?","La Voie lactée","Andromède","La Grande Ourse","Orion"},
        {"CIEL","Quelle planète est la plus proche du Soleil ?","Mercure","Jupiter","Saturne","Neptune"},
        {"CIEL","Quel phénomène produit les marées sur Terre en grande partie ?","L'attraction de la Lune","Le vent solaire seulement","La couleur des océans","Les volcans"},
        {"ALLEMAND","Que veut dire « das Haus » ?","La maison","Le jardin","La voiture","Le livre"},
        {"ALLEMAND","Que signifie « Ich bin müde » ?","Je suis fatigué","J'ai faim","Je suis en avance","Il pleut"},
        {"ALLEMAND","Quel est le mot allemand pour « le chat » ?","Die Katze","Der Hund","Der Fisch","Die Maus"},
        {"TEMPS","À quoi sert généralement une lunette tournante de plongée ?","À suivre un temps écoulé","À mesurer la température","À accorder un piano","À faire fonctionner la couronne"},
        {"TEMPS","Quel organe d'une montre mécanique oscille régulièrement ?","Le balancier","Le bracelet","La boucle","Le cadran"},
        {"MUSIQUE","Quel saxophoniste a enregistré l'album A Love Supreme ?","John Coltrane","Benny Goodman","Chet Baker","Thelonious Monk"},
        {"MUSIQUE","Quel saxophoniste a composé Take Five pour le quartet de Dave Brubeck ?","Paul Desmond","Duke Ellington","Herbie Hancock","Bill Evans"},
        {"MUSIQUE","Combien de temps compte une mesure à quatre-quatre ?","Quatre","Trois","Cinq","Six"},
        {"MUSIQUE","Quelle touche se trouve entre do et ré sur un piano ?","Do dièse","Mi","Sol","Si bémol"},
        {"CUISINE","Quelle variété de pâte à pizza se prépare avec une préfermentation ferme ?","La pâte à la biga","Une meringue","Une génoise","Une pâte sablée"},
        {"CUISINE","Quel ingrédient se transforme en caramel lorsqu'on le chauffe ?","Le sucre","Le sel","La farine seule","Le vinaigre"},
        {"CUISINE","Quel fruit entre dans la composition de la choucroute ?","Aucun : c'est du chou fermenté","La pomme","La prune","L'orange"},
        {"VOYAGE","Quelle chaîne de montagnes traverse le sud de la Suisse ?","Les Alpes","Les Pyrénées","Les Carpates","Les Andes"},
        {"VOYAGE","Quel océan borde le Maroc à l'ouest ?","L'Atlantique","L'Indien","Le Pacifique","L'Arctique"},
        {"VOYAGE","Quelle est la capitale du Portugal ?","Lisbonne","Porto","Funchal","Faro"},
        {"CIEL","De quoi une comète est-elle notamment constituée ?","De glace et de poussières","Uniquement de métal liquide","De lumière pure","Seulement d'air"},
        {"CIEL","Quelle planète est souvent appelée la planète rouge ?","Mars","Jupiter","Vénus","Uranus"},
        {"ALLEMAND","Que veut dire « der Bahnhof » ?","La gare","Le jardin","Le restaurant","La cuisine"},
        {"ALLEMAND","Que signifie « Wie viel kostet das ? » ?","Combien cela coûte-t-il ?","Quelle heure est-il ?","Où habites-tu ?","Comment vas-tu ?"},
        {"TEMPS","Que signifie généralement « automatique » pour une montre mécanique ?","Elle se remonte grâce aux mouvements du poignet","Elle fonctionne uniquement au soleil","Elle se connecte obligatoirement au téléphone","Elle ne possède aucun ressort"},
        {"TEMPS","Dans une montre mécanique, où l'énergie est-elle stockée ?","Dans un ressort","Dans un réservoir d'eau","Dans une antenne","Dans le verre"},
        {"TEMPS","Quelle unité correspond à un millième de seconde ?","Une milliseconde","Une minute","Une heure","Un jour"},
        {"TEMPS","Quel objet indique les heures et les minutes ?","Une montre","Une boussole","Un thermomètre","Une balance"},
        {"TEMPS","Combien de secondes compte une minute ?","60","100","24","12"},
        {"TEMPS","Quel mécanisme mesure le temps dans une montre mécanique ?","Un mouvement à engrenages","Une antenne GPS obligatoire","Un écran tactile","Un panneau solaire obligatoire"},
        {"TEMPS","Que signifie GMT sur une montre à deux fuseaux horaires ?","Greenwich Mean Time","Global Mechanical Timing","General Minute Timer","Green Mountain Time"},
        {"TEMPS","Que mesure un chronographe ?","Une durée déclenchée et arrêtée","La profondeur de l'océan","La pression atmosphérique","La distance à la Lune"},
        {"TEMPS","Quel matériau est souvent utilisé pour le verre d'une montre résistante aux rayures ?","Le saphir synthétique","Le papier","Le caoutchouc","Le bois de sapin"},
        {"TEMPS","Comment appelle-t-on l'indication de la date sur un cadran ?","Un quantième","Un diapason","Une couronne solaire","Une clef de sol"},
        {"TEMPS","Sur un cadran classique, combien de minutes séparent deux chiffres voisins ?","Cinq","Deux","Dix","Quinze"},
        {"TEMPS","Quel composant transmet généralement l'énergie entre les rouages et le balancier ?","L'échappement","Le bracelet","La lunette décorative","Le fermoir"},
        {"TEMPS","Une montre à quartz utilise généralement quel élément pour réguler le temps ?","Un cristal de quartz","Un pendule en bois","Un ressort de stylo","Une roue hydraulique"},
        {"TEMPS","Que désigne la réserve de marche d'une montre mécanique ?","Son autonomie après remontage","La longueur de son bracelet","La profondeur de son cadran","Sa résistance aux rayures"},
        {"VOYAGE","Quel fleuve traverse Genève après sa sortie du Léman ?","Le Rhône","La Seine","Le Pô","Le Danube"},
        {"VOYAGE","Quel pays a pour capitale Berne ?","La Suisse","L'Autriche","La Belgique","Le Portugal"},
        {"VOYAGE","Quelle ville se situe au bord du lac de Zurich ?","Zurich","Sion","Coire","Bellinzone"},
        {"VOYAGE","Quelle langue est principalement parlée au Tessin ?","L'italien","Le néerlandais","Le suédois","Le portugais"},
        {"VOYAGE","Quel massif se trouve entre la Suisse et la France ?","Le Jura","Les Andes","L'Himalaya","L'Oural"},
        {"CIEL","Comment nomme-t-on une étoile filante ?","Un météore","Une comète immobile","Une planète naine","Un satellite naturel"},
        {"CIEL","Quel corps céleste tourne autour d'une planète ?","Un satellite","Une constellation","Une galaxie","Un horizon"},
        {"CIEL","Quel phénomène explique l'alternance du jour et de la nuit ?","La rotation de la Terre","Les phases de la Lune","La rotation de Mars","Les marées"},
        {"CIEL","Quelle planète du système solaire est la plus grande ?","Jupiter","Mars","Mercure","Vénus"},
        {"ALLEMAND","Que signifie « der Kaffee » ?","Le café","Le thé","Le lait","Le sucre"},
        {"ALLEMAND","Comment dit-on « bonne nuit » en allemand ?","Gute Nacht","Guten Morgen","Vielen Dank","Bis bald"},
        {"ALLEMAND","Que signifie « der Hund » ?","Le chien","Le chat","L'oiseau","Le cheval"},
        {"ALLEMAND","Quel mot allemand signifie « aujourd'hui » ?","Heute","Morgen","Gestern","Immer"},
        {"ALLEMAND","Que signifie « bitte » dans une demande polie ?","S'il te plaît","Jamais","Hier","Peut-être"},
        {"CUISINE","Quel ingrédient principal entre dans une meringue classique ?","Des blancs d'œufs et du sucre","Des pommes de terre","Du riz et du bouillon","Du beurre et du sel"}
    };
    private static final int ROUND=8;
    private final Random rng=new Random();
    private final ArrayList<Integer> order=new ArrayList<>();
    private int index,score,streak,best,correctCount,longestStreak; private boolean answered, showingHome, jokerUsed, choosingTheme;
    private String gameMode="MÉLANGE";
    private String dailyDate="";
    private final ArrayList<String> review = new ArrayList<>();
    private final ArrayList<Integer> missed = new ArrayList<>();
    private boolean restoringAnswer;
    private String[] hiddenOptions;
    private String[] options; private String selected; private int earned;
    private LinearLayout root,choices;
    private android.widget.ScrollView scroll;
    private TextView header,category,question,feedback;
    private android.widget.ProgressBar progress;
    private Button next, joker;
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    private GradientDrawable outlinedShape(int color,int accent){
        GradientDrawable d=shape(color,dp(16)); d.setStroke(dp(2),accent); return d;
    }
    private TextView label(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER);return v;}
    private int categoryColor(String name){
        switch(name){
            case "MUSIQUE": return Color.rgb(241,179,122);
            case "CUISINE": return Color.rgb(244,168,141);
            case "VOYAGE": return Color.rgb(147,207,201);
            case "CIEL": return Color.rgb(179,185,255);
            case "ALLEMAND": return Color.rgb(218,184,234);
            default: return GOLD;
        }
    }
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    private void newGame(){
        showingHome=false; choosingTheme=false;
        if(gameMode.equals("RATTRAPAGE")) gameMode="MÉLANGE";
        boolean daily=gameMode.equals("DÉFI");
        if(daily) dailyDate=java.time.LocalDate.now().toString();
        java.util.Set<String> recent = getPreferences(MODE_PRIVATE).getStringSet("recent", java.util.Collections.emptySet());
        ArrayList<Integer> pool=new ArrayList<>(); java.util.Set<String> localRecent=new java.util.HashSet<>();
        for(int i=0;i<BANK.length;i++){
            if(daily || gameMode.equals("MÉLANGE") || BANK[i][0].equals(gameMode)){
                if(recent.contains(Integer.toString(i))) localRecent.add(Integer.toString(pool.size()));
                pool.add(i);
            }
        }
        ArrayList<Integer> picked=QuizRules.order(pool.size(),ROUND,
            daily ? java.util.Collections.emptySet() : localRecent,
            daily ? new Random(dailyDate.hashCode()) : rng);
        order.clear(); for(int i:picked) order.add(pool.get(i));
        java.util.Set<String> played = new java.util.HashSet<>();
        for(int i=0;i<ROUND;i++) played.add(Integer.toString(order.get(i)));
        if(!daily) getPreferences(MODE_PRIVATE).edit().putStringSet("recent",played).apply();
        index=score=streak=correctCount=longestStreak=0; answered=false; jokerUsed=false; hiddenOptions=null; review.clear(); missed.clear();
        showQuestion();
    }
    private void retryMissed(){
        ArrayList<Integer> retry=new ArrayList<>(missed);
        Collections.shuffle(retry,rng);
        // Complete the round with new questions, not ones just answered correctly.
        java.util.Set<Integer> previous=new java.util.HashSet<>(order.subList(0,ROUND));
        ArrayList<Integer> rest=new ArrayList<>();
        for(int i=0;i<BANK.length;i++) if(!previous.contains(i)) rest.add(i);
        Collections.shuffle(rest,rng);
        for(int i=0;retry.size()<ROUND;i++) retry.add(rest.get(i));
        order.clear(); order.addAll(retry);
        gameMode="RATTRAPAGE"; showingHome=false; choosingTheme=false;
        index=score=streak=correctCount=longestStreak=0;
        answered=false; jokerUsed=false; hiddenOptions=null; review.clear(); missed.clear();
        showQuestion();
    }
    private String currentAward(){
        android.content.SharedPreferences prefs=getPreferences(MODE_PRIVATE);
        return QuizRules.award(prefs.getInt("games",0),prefs.getInt("totalCorrect",0),prefs.getInt("longest",0));
    }
    private void showHome(){
        showingHome=true; choosingTheme=false; answered=false; selected=null; choices.removeAllViews(); progress.setProgress(0); joker.setVisibility(View.GONE);
        header.setText("✦  BIENVENUE  ✦"); category.setText("UN MOMENT POUR TOI"); category.setTextColor(GOLD);
        question.setText("Une petite aventure à chaque partie");
        int record=getPreferences(MODE_PRIVATE).getInt("best",0);
        int games=getPreferences(MODE_PRIVATE).getInt("games",0);
        feedback.setText(games>0 ? "✦  "+currentAward()+"\nRecord : "+record+" points  ·  Parties : "+games : "Huit étapes, des surprises et un peu de chance.");
        feedback.setTextColor(GOLD); next.setText("C’est parti"); next.setVisibility(View.VISIBLE);
        String today=java.time.LocalDate.now().toString();
        android.content.SharedPreferences prefs=getPreferences(MODE_PRIVATE);
        int dailyBest=prefs.getString("dailyDate","").equals(today) ? prefs.getInt("dailyBest",0) : 0;
        Button daily=makeMenuButton(dailyBest>0 ? "Défi du jour · record "+dailyBest : "Défi du jour");
        daily.setBackground(outlinedShape(CARD,GOLD)); daily.setTextColor(GOLD);
        choices.addView(daily); daily.setOnClickListener(v->{gameMode="DÉFI";newGame();});
        Button theme=makeMenuButton("Choisir un thème");
        choices.addView(theme); theme.setOnClickListener(v->showThemes());
    }
    private Button makeMenuButton(String text){
        Button b=new Button(this); b.setText(text); b.setAllCaps(false); b.setTextSize(16);
        b.setTextColor(WHITE); b.setBackgroundTintList(null); b.setBackground(shape(CARD,dp(16)));
        b.setMinHeight(dp(52)); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.setMargins(0,dp(5),0,dp(5)); b.setLayoutParams(lp); return b;
    }
    private void showThemes(){
        showingHome=false; choosingTheme=true; answered=false; selected=null;
        choices.removeAllViews(); joker.setVisibility(View.GONE); progress.setProgress(0);
        header.setText("CHOISIS TON THÈME"); category.setText("✦  EXPLORATION  ✦"); category.setTextColor(GOLD);
        question.setText("Quelle escale te tente ?"); feedback.setText(""); next.setText("Retour"); next.setVisibility(View.VISIBLE);
        for(String theme:new String[]{"MUSIQUE","CUISINE","VOYAGE","CIEL","ALLEMAND","TEMPS"}){
            Button b=makeMenuButton(theme); b.setTextColor(categoryColor(theme));
            b.setBackground(outlinedShape(CARD,categoryColor(theme))); choices.addView(b);
            b.setOnClickListener(v->{gameMode=theme;newGame();});
        }
        scroll.post(()->scroll.scrollTo(0,0));
    }
    private void showQuestion(){hiddenOptions=null; showQuestion(null);}
    private void showQuestion(String[] restoredOptions){
        answered=false; selected=null; earned=0; choices.removeAllViews(); next.setVisibility(View.GONE); progress.setProgress(index);
        scroll.post(()->scroll.scrollTo(0,0));
        joker.setVisibility(View.VISIBLE); joker.setEnabled(!jokerUsed); joker.setText(jokerUsed ? "Joker utilisé" : "Joker 50 / 50 · une fois");
        String[] q=BANK[order.get(index)]; header.setText("MANCHE  "+(index+1)+" / "+ROUND+"     ✦  "+score);
        category.setText("✦  "+q[0]+"  ✦"); category.setTextColor(categoryColor(q[0])); question.setText(q[1]); feedback.setText("");
        ArrayList<String> a=new ArrayList<>();for(int i=2;i<q.length;i++)a.add(q[i]);
        if(restoredOptions!=null && restoredOptions.length==a.size() && a.containsAll(java.util.Arrays.asList(restoredOptions))){
            options=restoredOptions.clone();
        }else{Collections.shuffle(a,gameMode.equals("DÉFI") ? new Random((long)dailyDate.hashCode()*31+index) : rng); options=a.toArray(new String[0]);}
        for(String value:options){
            Button b=new Button(this);b.setText(value);b.setTextColor(WHITE);b.setTextSize(17);b.setAllCaps(false);b.setBackgroundTintList(null);b.setBackground(outlinedShape(CARD,categoryColor(q[0])));
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
        if(ok){streak++;longestStreak=Math.max(streak,longestStreak);correctCount++;earned=QuizRules.points(streak);score+=earned;feedback.setText("✦ Bravo !  +"+earned+" points"+(streak>1 ? "  ·  Série : "+streak : ""));}
        else {streak=0;feedback.setText("La réponse : "+correct);
            if(!restoringAnswer){
                review.add(BANK[order.get(index)][1]+"\n→ "+correct);
                missed.add(order.get(index));
            }
        }
        feedback.setTextColor(ok?GOLD:WHITE);
        header.setText("MANCHE  "+(index+1)+" / "+ROUND+"     ✦  "+score);
        for(int i=0;i<choices.getChildCount();i++){
            Button b=(Button)choices.getChildAt(i);b.setEnabled(false);
            if(b.getText().toString().equals(correct))b.setBackground(shape(Color.rgb(36,105,88),dp(16)));
            else if(b.getText().toString().equals(value))b.setBackground(shape(Color.rgb(125,57,70),dp(16)));
        }
        next.setText(index==ROUND-1?"Voir le résultat":"Continuer");next.setVisibility(View.VISIBLE);
    }
    private void recordRound(){
        android.content.SharedPreferences prefs=getPreferences(MODE_PRIVATE);
        prefs.edit().putInt("games",prefs.getInt("games",0)+1)
            .putInt("totalCorrect",prefs.getInt("totalCorrect",0)+correctCount)
            .putInt("longest",Math.max(prefs.getInt("longest",0),longestStreak)).apply();
    }
    private void finishRound(){
        if(gameMode.equals("DÉFI")){
            android.content.SharedPreferences prefs=getPreferences(MODE_PRIVATE);
            int old=prefs.getString("dailyDate","").equals(dailyDate) ? prefs.getInt("dailyBest",0) : 0;
            prefs.edit().putString("dailyDate",dailyDate).putInt("dailyBest",Math.max(old,score)).apply();
        }
        best=Math.max(score,getPreferences(MODE_PRIVATE).getInt("best",0));getPreferences(MODE_PRIVATE).edit().putInt("best",best).apply();
        choices.removeAllViews();joker.setVisibility(View.GONE);progress.setProgress(ROUND);header.setText("PARTIE TERMINÉE");category.setText(gameMode.equals("DÉFI") ? "✦  DÉFI DU JOUR  ✦" : gameMode.equals("RATTRAPAGE") ? "✦  REVANCHE  ✦" : "✦  BRAVO  ✦");category.setTextColor(GOLD);
        question.setText(score+" points");feedback.setText((correctCount==ROUND ? "✦ Sans-faute !\n" : "")+correctCount+" bonnes réponses sur "+ROUND+"\nMeilleure série : "+longestStreak+"\nRecord : "+best+" points\n✦  "+currentAward());feedback.setTextColor(GOLD);
        next.setText("Rejouer");next.setVisibility(View.VISIBLE);
        if(!review.isEmpty()){
            Button retry=makeMenuButton("Prendre ma revanche"); choices.addView(retry);
            retry.setBackground(outlinedShape(CARD,GOLD)); retry.setTextColor(GOLD);
            retry.setOnClickListener(v->retryMissed());
            Button recap=makeMenuButton("Revoir mes réponses"); choices.addView(recap);
            recap.setOnClickListener(v->new android.app.AlertDialog.Builder(this)
                .setTitle("À retenir").setMessage(android.text.TextUtils.join("\n\n",review))
                .setPositiveButton("Fermer",(dialog,which)->dialog.dismiss()).show());
        }
        Button home=makeMenuButton("Accueil"); choices.addView(home); home.setOnClickListener(v->showHome());
    }
    @Override public void onBackPressed(){
        if(choosingTheme || (!showingHome && index>=ROUND)){showHome(); return;}
        if(showingHome){super.onBackPressed(); return;}
        new android.app.AlertDialog.Builder(this).setTitle("Quitter la partie ?")
            .setMessage("Ta partie en cours sera perdue.")
            .setNegativeButton("Rester",(dialog,which)->dialog.dismiss())
            .setPositiveButton("Quitter",(dialog,which)->showHome()).show();
    }
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);
        scroll=new android.widget.ScrollView(this);scroll.setFillViewport(true);scroll.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.rgb(20,31,53),BG,Color.rgb(34,25,44)}));
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setGravity(Gravity.CENTER);root.setPadding(dp(22),dp(24),dp(22),dp(24));scroll.addView(root);
        header=label("",14,GOLD);header.setTypeface(null,Typeface.BOLD);root.addView(header);
        progress=new android.widget.ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setMax(ROUND); progress.setProgressTintList(android.content.res.ColorStateList.valueOf(GOLD));
        LinearLayout.LayoutParams progressParams=new LinearLayout.LayoutParams(-1,dp(6));
        progressParams.setMargins(dp(24),dp(16),dp(24),0); root.addView(progress,progressParams);
        category=label("",14,GOLD);category.setTypeface(null,Typeface.BOLD);category.setLetterSpacing(0.08f);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(24),0,dp(8));root.addView(category,cp);
        question=label("",25,WHITE);question.setTypeface(null,Typeface.BOLD);root.addView(question);
        choices=new LinearLayout(this);choices.setOrientation(LinearLayout.VERTICAL);LinearLayout.LayoutParams choicesP=new LinearLayout.LayoutParams(-1,-2);choicesP.setMargins(0,dp(22),0,0);root.addView(choices,choicesP);
        joker=new Button(this); joker.setAllCaps(false); joker.setTextSize(14); joker.setTextColor(GOLD);
        joker.setBackgroundTintList(null); joker.setBackground(shape(CARD,dp(12))); joker.setMinHeight(dp(48));
        LinearLayout.LayoutParams jp=new LinearLayout.LayoutParams(-1,-2);jp.setMargins(0,dp(8),0,0);root.addView(joker,jp);
        joker.setOnClickListener(v->useJoker());
        feedback=label("",16,GOLD);LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2);fp.setMargins(0,dp(16),0,dp(12));root.addView(feedback,fp);
        next=new Button(this);next.setAllCaps(false);next.setTextColor(BG);next.setTextSize(17);next.setBackgroundTintList(null);next.setBackground(shape(GOLD,dp(16)));next.setMinHeight(dp(56)); root.addView(next,new LinearLayout.LayoutParams(-1,-2));
        next.setOnClickListener(v->{if(choosingTheme){showHome();}else if(showingHome){gameMode="MÉLANGE";newGame();}else if(index>=ROUND){newGame();}else if(index==ROUND-1){index++;recordRound();finishRound();}else{index++;showQuestion();}});
        setContentView(scroll);
        if(saved!=null && saved.containsKey("order")){
            ArrayList<String> savedReview=saved.getStringArrayList("review"); if(savedReview!=null) review.addAll(savedReview);
            ArrayList<Integer> savedMissed=saved.getIntegerArrayList("missed"); if(savedMissed!=null) missed.addAll(savedMissed);
            int[] arr=saved.getIntArray("order"); if(arr!=null) for(int x:arr)order.add(x);index=saved.getInt("index");score=saved.getInt("score");streak=saved.getInt("streak");correctCount=saved.getInt("correctCount");longestStreak=saved.getInt("longestStreak");jokerUsed=saved.getBoolean("jokerUsed");hiddenOptions=saved.getStringArray("hiddenOptions");gameMode=saved.getString("gameMode","MÉLANGE"); dailyDate=saved.getString("dailyDate","");
            if(saved.getBoolean("choosingTheme")) showThemes(); else if(saved.getBoolean("home") || order.size()<ROUND) showHome(); else if(index>=ROUND)finishRound();else {showQuestion(saved.getStringArray("options")); String previous=saved.getString("selected"); if(previous!=null){int oldScore=score, oldStreak=streak, oldCorrect=correctCount, oldLongest=longestStreak; restoringAnswer=true; answer(previous); restoringAnswer=false; score=oldScore; streak=oldStreak; correctCount=oldCorrect; longestStreak=oldLongest; header.setText("MANCHE  "+(index+1)+" / "+ROUND+"     ✦  "+score); earned=saved.getInt("earned"); if(previous.equals(BANK[order.get(index)][2])) feedback.setText("✦ Bravo !  +"+earned+" points"+(streak>1 ? "  ·  Série : "+streak : ""));}}
        }else showHome();
    }
    @Override protected void onSaveInstanceState(Bundle out){
        out.putStringArrayList("review",new ArrayList<>(review));
        out.putIntegerArrayList("missed",new ArrayList<>(missed));
        int[] arr=new int[order.size()];for(int i=0;i<arr.length;i++)arr[i]=order.get(i);out.putIntArray("order",arr);out.putInt("index",index);out.putInt("score",score);out.putInt("streak",streak);out.putInt("correctCount",correctCount);out.putInt("longestStreak",longestStreak);out.putBoolean("home",showingHome);out.putBoolean("choosingTheme",choosingTheme);out.putString("gameMode",gameMode);out.putString("dailyDate",dailyDate);out.putBoolean("jokerUsed",jokerUsed);if(hiddenOptions!=null)out.putStringArray("hiddenOptions",hiddenOptions); if(!showingHome && index<ROUND && options!=null)out.putStringArray("options",options); if(answered){out.putString("selected",selected);out.putInt("earned",earned);}
        super.onSaveInstanceState(out);
    }
}
