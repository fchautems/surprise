#!/usr/bin/env python3
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

def adb(*args):
    return subprocess.check_output(["adb", *args], text=True)

def dump():
    for _ in range(8):
        time.sleep(1)
        try:
            adb("shell", "uiautomator", "dump", "/sdcard/window.xml")
            return ET.fromstring(adb("shell", "cat", "/sdcard/window.xml"))
        except (subprocess.CalledProcessError, ET.ParseError):
            pass
    raise AssertionError("UI unavailable")

def capture(name):
    dest=Path(__file__).resolve().parents[1] / "test-screens"
    dest.mkdir(exist_ok=True)
    with (dest / (name + ".png")).open("wb") as output:
        subprocess.run(["adb","exec-out","screencap","-p"], stdout=output, check=True)

def texts(root):
    return [n.attrib.get("text", "") for n in root.iter("node")]

def has(root, fragment):
    return any(fragment in text for text in texts(root))

def tap(root, label):
    last_bounds = None
    for attempt in range(5):
        node=next((n for n in root.iter("node") if n.attrib.get("text", "").casefold()==label.casefold()),None)
        if node is not None:
            b=list(map(int,re.findall(r"\d+",node.attrib["bounds"])))
            last_bounds = b
            if len(b)==4 and 50 <= (b[1]+b[3])//2 <= (600 if attempt>=3 else 560):
                adb("shell","input","tap",str((b[0]+b[2])//2),str((b[1]+b[3])//2))
                return
        adb("shell","input","swipe","160","480","160","220","300")
        root=dump()
    raise AssertionError("Control unavailable: "+label+" bounds="+repr(last_bounds)+" "+repr(texts(root)))

def open_game():
    for _ in range(3):
        root=dump()
        if has(root,"PETITES MÉLODIES"):return root
        tap(root,"Jouer en musique")
    raise AssertionError("Music screen not opened")

adb("logcat","-c")
adb("shell","am","force-stop","ch.fred.surprise")
adb("shell","pm","clear","ch.fred.surprise")
adb("shell","am","start","-n","ch.fred.surprise/.MainActivity")
root=open_game()
assert "Fa" not in texts(root) and "Si" not in texts(root), "Extra keys appeared in challenge"
tap(root,"Lent")
root=dump()
assert has(root,"Normal"),texts(root)
# Background during playback, then resume through Android's recent-apps UI.
# Starting the non-exported activity through the shell is prohibited.
tap(root,"Écouter la mélodie")
adb("shell","input","keyevent","KEYCODE_HOME")
# Launcher timing varies on shared CI runners. Retry opening the recent card
# rather than treating a still-visible launcher as an application failure.
root=None
for _ in range(3):
    adb("shell","input","keyevent","KEYCODE_APP_SWITCH")
    time.sleep(2)
    adb("shell","input","tap","160","320")
    root=dump()
    if has(root,"PETITES MÉLODIES"):
        break
assert has(root,"PETITES MÉLODIES"), ("Recent task did not resume",texts(root))
assert has(root,"Lecture interrompue"), ("Interrupted playback not restored",texts(root))
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_DOWN"]*20))
adb("logcat","-c")
tap(root,"Écouter la mélodie")
root=dump()
assert has(root,"volume multimédia est coupé"),texts(root)
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_UP"]*5))
# Give the Android system volume panel time to dismiss before tapping a key.
time.sleep(4)
for _ in range(5):
    if has(root,"À toi de jouer"):break
    root=dump()
assert has(root,"À toi de jouer"),texts(root)
played=adb("logcat","-d","-s","SurpriseAudio:I")
assert "melody playback started" in played, played
notes=re.findall(r"playback started: note (Do|Ré|Mi|Sol|La)(?:\s|$)", played.split("melody playback started")[-1])
assert len(notes)==3 and all(a!=b for a,b in zip(notes,notes[1:])), (notes,played)
tap(root,notes[0])
root=dump()
assert has(root,"1 / 3 notes"),texts(root)
def selected_note(root, name):
    return any(n.attrib.get("content-desc")=="Note "+name+" · dernière jouée" for n in root.iter("node"))
assert selected_note(root,notes[0]), "Last played key not marked"
tap(root,"Indice")
root=dump()
assert has(root,"Indice : prochaine note · "+notes[1]), texts(root)
node=next(n for n in root.iter("node") if n.attrib.get("text")=="Indice utilisé")
assert node.attrib.get("enabled")=="false", node.attrib
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
node=next(n for n in root.iter("node") if n.attrib.get("text")=="Indice utilisé")
assert node.attrib.get("enabled")=="false", "Hint reused after rotation"
assert selected_note(root,notes[0]), "Selected key lost on rotation"
wrong=next(n for n in ("Do","Ré","Mi","Sol","La") if n!=notes[1])
tap(root,wrong)
root=dump()
assert has(root,"Presque"),texts(root)
tap(root,notes[0])
tap(dump(),notes[1])
root=dump()
assert has(root,"2 / 3 notes"),texts(root)
# Both exit paths must protect an unfinished challenge; cancelling preserves progress.
adb("shell","input","keyevent","KEYCODE_BACK")
dialog=dump()
assert has(dialog,"Quitter le défi"),texts(dialog)
tap(dialog,"Continuer")
root=dump()
assert has(root,"2 / 3 notes"),"Back cancellation lost progress"
tap(root,"Retour à l’accueil")
dialog=dump()
assert has(dialog,"Quitter le défi"),texts(dialog)
tap(dialog,"Continuer")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"2 / 3 notes"),"Home cancellation lost progress"
assert has(root,"Normal"),texts(root)
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
assert has(root,"2 / 3 notes"),"Progress message lost after rotation"
assert has(root,"Normal"),"Listening speed lost after rotation"
tap(root,"Normal")
root=dump()
assert has(root,"Lent"),texts(root)
tap(root,notes[2])
root=dump()
assert has(root,"1 mélodie"),texts(root)
hint=next(n for n in root.iter("node") if n.attrib.get("text")=="Indice")
assert hint.attrib.get("enabled")=="true", "New melody did not reset hint"
# The next tune has the same length initially, but never repeats the last tune
# and never uses the same pitch twice in a row.
adb("logcat","-c")
tap(root,"Écouter la mélodie")
for _ in range(8):
    root=dump()
    if has(root,"À toi de jouer"):break
assert has(root,"À toi de jouer"),texts(root)
assert not any("dernière jouée" in n.attrib.get("content-desc","") for n in root.iter("node")), "Old key remained marked"
second_log=adb("logcat","-d","-s","SurpriseAudio:I")
assert "melody playback started" in second_log, second_log
second=re.findall(r"playback started: note (Do|Ré|Mi|Sol|La)(?:\s|$)",second_log.split("melody playback started")[-1])
assert len(second)==3 and second!=notes and all(a!=b for a,b in zip(second,second[1:])), (notes,second,second_log)
tap(root,"Retour à l’accueil")
dialog=dump()
assert has(dialog,"Quitter le défi"),texts(dialog)
tap(dialog,"Quitter")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"BIENVENUE"),texts(root)
adb("shell","am","force-stop","ch.fred.surprise")
adb("shell","am","start","-n","ch.fred.surprise/.MainActivity")
root=open_game()
assert has(root,"Record : 1"),texts(root)
tap(root,"Jouer librement")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"Mode libre") and not has(root,"Écouter la mélodie"),texts(root)
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_DOWN"]*20))
tap(root,"Ré")
root=dump()
assert has(root,"Mode libre · Ré") and has(root,"volume multimédia est coupé"),texts(root)
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_UP"]*5))
tap(root,"Do maj7")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"Mode libre · Do maj7"),texts(root)
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
assert has(root,"Mode libre"),texts(root)
# Rapid taps must not build a backlog; the last requested note must start.
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
positions={}
for label in ("Do","Ré","Mi"):
    node=next(n for n in root.iter("node") if n.attrib.get("text")==label)
    b=list(map(int,re.findall(r"\d+",node.attrib["bounds"])))
    positions[label]=(str((b[0]+b[2])//2),str((b[1]+b[3])//2))
adb("logcat","-c")
for label in ("Do","Ré","Mi"):
    adb("shell","input","tap",*positions[label])
time.sleep(1)
recent=adb("logcat","-d","-s","SurpriseAudio:I")
assert "playback started: note Mi" in recent, recent
assert recent.rfind("playback started: note Mi") > recent.rfind("playback started: note Do"), recent
root=dump()
tap(root,"Fa maj7")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"Mode libre · Fa maj7"),texts(root)
# New chords remain accessible, produce audio and do not alter challenge records.
for chord_name in ("La m7", "Ré m7", "Mi m7"):
    adb("logcat","-c")
    tap(root,chord_name)
    root=dump()
    sound=adb("logcat","-d","-s","SurpriseAudio:I")
    assert "playback started: accord "+chord_name in sound, (chord_name,sound)
    for _ in range(3):
        adb("shell","input","swipe","160","200","160","520","300")
    root=dump()
    assert has(root,"Mode libre · "+chord_name),texts(root)
tap(root,"Octave +")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"octave haute"),texts(root)
tap(root,"Mi")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"Mode libre · Mi ↑"),texts(root)
audio_high=adb("logcat","-d","-s","SurpriseAudio:I")
assert "playback started: note Mi haute" in audio_high, audio_high
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
tap(root,"Octave normale")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"octave normale"),texts(root)
tap(root,"Octave +")
root=dump()
tap(root,"Revenir au défi")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"Écouter la mélodie"),texts(root)
# The listening speed remains a personal choice after leaving and returning.
tap(root,"Lent")
root=dump()
assert has(root,"Normal"),texts(root)
tap(root,"Retour à l’accueil")
root=open_game()
assert has(root,"Record : 1"),"Free play changed the record"
assert has(root,"Normal"),"Listening speed lost after reopening"
tap(root,"Normal")
root=dump()
assert has(root,"Lent"),texts(root)
tap(root,"Jouer librement")
root=dump()
tap(root,"Octave normale")  # Missing control fails if the choice was not restored.
for _ in range(3):
    adb("shell","input","swipe","160","200","160","520","300")
root=dump()
adb("logcat","-c")
tap(root,"Fa")
root=dump()
# Fa must precede Sol and La; all three targets must remain touch-sized.
row_nodes={n.attrib.get("text"): n for n in root.iter("node") if n.attrib.get("text") in ("Fa","Sol","La")}
assert len(row_nodes)==3, ("Free keys missing",texts(root))
bounds={name:list(map(int,re.findall(r"\d+",node.attrib["bounds"]))) for name,node in row_nodes.items()}
assert all(v[2]-v[0]>=48 for v in bounds.values()), ("Targets too narrow",bounds)
assert bounds["Fa"][0]<bounds["Sol"][0]<bounds["La"][0], ("Unexpected key order",bounds)
assert selected_note(root,"Fa"), "Extra key not marked"
assert "playback started: note Fa" in adb("logcat","-d","-s","SurpriseAudio:I")
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
assert selected_note(root,"Fa"), "Extra key selection lost on rotation"
tap(root,"Si")
root=dump()
assert selected_note(root,"Si") and not selected_note(root,"Fa"), "Extra selection not updated"
assert "playback started: note Si" in adb("logcat","-d","-s","SurpriseAudio:I")
tap(root,"Octave +")
for _ in range(3):
    adb("shell","input","swipe","160","200","160","520","300")
root=dump()
tap(root,"Si")
root=dump()
assert "playback started: note Si haute" in adb("logcat","-d","-s","SurpriseAudio:I"), "Extra octave playback missing"
# The octave control also transposes full chords, in both playback styles.
adb("logcat","-c")
tap(root,"Do maj7")
root=dump()
assert "playback started: accord Do maj7 haute" in adb("logcat","-d","-s","SurpriseAudio:I"), "Chord octave missing"
# Optional arpeggio plays each chord note in order and survives a rotation.
tap(root,"Arpège : désactivé")
root=dump()
for _ in range(3):
    adb("shell","input","swipe","160","200","160","520","300")
root=dump()
adb("logcat","-c")
tap(root,"Do maj7")
time.sleep(2)
ordered=adb("logcat","-d","-s","SurpriseAudio:I")
assert re.findall(r"arpège Do maj7 haute · (\d)",ordered)==["1","2","3","4"], ordered
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
tap(root,"Arpège : activé")
root=dump()
assert has(root,"Arpège : désactivé"),texts(root)
audio_log=adb("logcat","-d","-s","SurpriseAudio:I")
print("Audio diagnostic:",audio_log[-3000:])
assert "playback started" in audio_log, "AudioTrack never entered playing state"
# Small-screen and enlarged-text regression: free-play keys remain tappable.
try:
    adb("shell","wm","size","240x640")
    adb("shell","wm","density","160")
    adb("shell","settings","put","system","font_scale","1.3")
    adb("shell","am","force-stop","ch.fred.surprise")
    adb("shell","am","start","-n","ch.fred.surprise/.MainActivity")
    root=open_game()
    tap(root,"Jouer librement")
    for _ in range(3):
        adb("shell","input","swipe","160","200","160","520","300")
    root=dump()
    tap(root,"Fa")
    root=dump()
    row={n.attrib.get("text"):n for n in root.iter("node") if n.attrib.get("text") in ("Fa","Sol","La")}
    assert len(row)==3, ("Narrow keys missing",texts(root))
    bounds={name:list(map(int,re.findall(r"\d+",node.attrib["bounds"]))) for name,node in row.items()}
    assert all(v[2]-v[0]>=48 and v[3]-v[1]>=48 for v in bounds.values()), ("Narrow target",bounds)
    assert selected_note(root,"Fa"), "Narrow-screen selection missing"
    capture("music-small")
    # Long labels remain visible and operable with enlarged text on the narrow display.
    tap(root,"Arpège : désactivé")
    root=dump()
    assert has(root,"Arpège : activé"), ("Narrow control did not toggle",texts(root))
    tap(root,"Octave normale")
    root=dump()
    assert has(root,"Octave +"), ("Narrow control did not update",texts(root))
finally:
    adb("shell","wm","size","reset")
    adb("shell","wm","density","reset")
    adb("shell","settings","put","system","font_scale","1.0")
print("Music UI smoke test passed")
