#!/usr/bin/env python3
import re
import subprocess
import time
import xml.etree.ElementTree as ET

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
tap(root,"Lent")
root=dump()
assert has(root,"Normal"),texts(root)
# Interrupt a slow playback by backgrounding the app, then resume the same activity.
tap(root,"Écouter la mélodie")
adb("shell","input","keyevent","KEYCODE_HOME")
time.sleep(1)
adb("shell","run-as","ch.fred.surprise","am","start","--activity-reorder-to-front","-n","ch.fred.surprise/.MusicActivity")
root=dump()
assert has(root,"Lecture interrompue"),texts(root)
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_DOWN"]*20))
adb("logcat","-c")
tap(root,"Écouter la mélodie")
root=dump()
assert has(root,"volume multimédia est coupé"),texts(root)
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_UP"]*5))
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
# The next tune has the same length initially, but never repeats the last tune
# and never uses the same pitch twice in a row.
adb("logcat","-c")
tap(root,"Écouter la mélodie")
for _ in range(8):
    root=dump()
    if has(root,"À toi de jouer"):break
assert has(root,"À toi de jouer"),texts(root)
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
tap(root,"Revenir au défi")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"Écouter la mélodie"),texts(root)
tap(root,"Retour à l’accueil")
root=open_game()
assert has(root,"Record : 1"),"Free play changed the record"
audio_log=adb("logcat","-d","-s","SurpriseAudio:I")
print("Audio diagnostic:",audio_log[-3000:])
assert "playback started" in audio_log, "AudioTrack never entered playing state"
print("Music UI smoke test passed")
