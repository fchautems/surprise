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
    for _ in range(5):
        node=next((n for n in root.iter("node") if n.attrib.get("text", "").casefold()==label.casefold()),None)
        if node is not None:
            b=list(map(int,re.findall(r"\d+",node.attrib["bounds"])))
            if len(b)==4 and 50 <= (b[1]+b[3])//2 <= 560:
                adb("shell","input","tap",str((b[0]+b[2])//2),str((b[1]+b[3])//2))
                return
        adb("shell","input","swipe","160","480","160","220","300")
        root=dump()
    raise AssertionError("Control unavailable: "+label+" "+repr(texts(root)))

def open_game():
    for _ in range(3):
        root=dump()
        if has(root,"PETITES MÉLODIES"):return root
        tap(root,"Jouer en musique")
    raise AssertionError("Music screen not opened")

adb("shell","am","force-stop","ch.fred.surprise")
adb("shell","pm","clear","ch.fred.surprise")
adb("shell","am","start","-n","ch.fred.surprise/.MainActivity")
root=open_game()
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_DOWN"]*20))
tap(root,"Écouter la mélodie")
root=dump()
assert has(root,"volume multimédia est coupé"),texts(root)
adb("shell","input","keyevent",*(["KEYCODE_VOLUME_UP"]*5))
for _ in range(5):
    if has(root,"À toi de jouer"):break
    root=dump()
assert has(root,"À toi de jouer"),texts(root)
tap(root,"Do")
root=dump()
assert has(root,"1 / 3 notes"),texts(root)
tap(root,"Ré")
root=dump()
assert has(root,"Presque"),texts(root)
tap(root,"Do")
tap(dump(),"Mi")
root=dump()
assert has(root,"2 / 3 notes"),texts(root)
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
assert has(root,"2 / 3 notes"),"Progress message lost after rotation"
tap(root,"La")
root=dump()
assert has(root,"1 mélodie"),texts(root)
tap(root,"Retour à l’accueil")
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
adb("shell","settings","put","system","user_rotation","1")
dump()
adb("shell","settings","put","system","user_rotation","0")
root=dump()
assert has(root,"Mode libre"),texts(root)
tap(root,"Revenir au défi")
adb("shell","input","swipe","160","200","160","520","300")
root=dump()
assert has(root,"Écouter la mélodie"),texts(root)
tap(root,"Retour à l’accueil")
root=open_game()
assert has(root,"Record : 1"),"Free play changed the record"
print("Music UI smoke test passed")
