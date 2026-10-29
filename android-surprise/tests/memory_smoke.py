#!/usr/bin/env python3
"""Black-box smoke test on the CI Android emulator."""
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

def adb(*args):
    return subprocess.check_output(["adb", *args], text=True)

def wait_dump():
    for _ in range(8):
        time.sleep(1)
        try:
            adb("shell", "uiautomator", "dump", "/sdcard/window.xml")
            xml = adb("shell", "cat", "/sdcard/window.xml")
            return ET.fromstring(xml)
        except (subprocess.CalledProcessError, ET.ParseError):
            continue
    raise AssertionError("Unable to read UI hierarchy")

def capture(name):
    dest = Path(__file__).resolve().parents[1] / "test-screens"
    dest.mkdir(exist_ok=True)
    with (dest / (name + ".png")).open("wb") as output:
        subprocess.run(["adb", "exec-out", "screencap", "-p"], stdout=output, check=True)

def texts(root):
    return [n.attrib.get("text", "") for n in root.iter("node")]

def tap_text(root, label):
    # Controls close to the system gesture area can send the user to the
    # launcher on newer Android versions. Bring them well inside the viewport.
    for attempt in range(4):
        node = next((n for n in root.iter("node")
                     if n.attrib.get("text", "").casefold() == label.casefold()), None)
        if node is not None:
            b = list(map(int, re.findall(r"\d+", node.attrib["bounds"])))
            assert len(b) == 4, node.attrib
            if 50 <= (b[1] + b[3]) // 2 <= 600:
                adb("shell", "input", "tap", str((b[0]+b[2])//2), str((b[1]+b[3])//2))
                return
            direction = ("160", "480", "160", "240") if b[1] > 560 else ("160", "240", "160", "480")
        else:
            direction = ("160", "480", "160", "240")
        adb("shell", "input", "swipe", *direction, "300")
        root = wait_dump()
    raise AssertionError("Missing or unsafe control: " + label + " in " + repr(texts(root)))

def find_text(root, fragment):
    return next((v for v in texts(root) if fragment in v), None)

adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "pm", "clear", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
# A separate matching game: discover every card, rotate, then solve the board.
root = wait_dump()
tap_text(root, "Un jeu de mémoire")
root = wait_dump()
assert find_text(root, "MÉMO DES ÉTOILES"), texts(root)

def card(root, index):
    prefix = "Carte " + str(index + 1) + " : "
    for _ in range(10):
        node = next((n for n in root.iter("node") if n.attrib.get("content-desc", "").startswith(prefix)), None)
        if node is not None:
            bounds = list(map(int, re.findall(r"\\d+", node.attrib["bounds"])))
            if len(bounds) == 4 and 60 <= (bounds[1] + bounds[3]) // 2 <= 560:
                return root, node
            down = len(bounds) == 4 and (bounds[1] + bounds[3]) // 2 > 560
        else:
            visible = [int(m.group(1))-1 for n in root.iter("node")
                       if (m := re.match(r"Carte (\\d+) :", n.attrib.get("content-desc", "")))]
            down = not visible or index > max(visible)
        if down:
            adb("shell", "input", "swipe", "160", "480", "160", "220", "300")
        else:
            adb("shell", "input", "swipe", "160", "220", "160", "480", "300")
        root = wait_dump()
    raise AssertionError("Memory card missing or outside safe area: " + str(index + 1))

def tap_card(root, index):
    root, node = card(root, index)
    bounds = list(map(int, re.findall(r"\d+", node.attrib["bounds"])))
    assert len(bounds) == 4, node.attrib
    adb("shell", "input", "tap", str((bounds[0]+bounds[2])//2), str((bounds[1]+bounds[3])//2))
    return wait_dump()

known = {}
for index in range(8):
    root = tap_card(root, index)
    root, node = card(root, index)
    desc = node.attrib["content-desc"]
    assert "visible " in desc or "trouvée " in desc, desc
    known[index] = desc.split()[-1]
    if index == 0:
        tap_text(root, "Nouvelle partie")
        dialog = wait_dump()
        assert find_text(dialog, "La partie en cours sera perdue."), texts(dialog)
        tap_text(dialog, "Continuer")
        root = wait_dump()
        root, node = card(root, index)
        assert node.attrib["content-desc"] == desc, "Cancel reset lost the card"
        adb("shell", "input", "keyevent", "KEYCODE_BACK")
        dialog = wait_dump()
        assert find_text(dialog, "Quitter la partie"), texts(dialog)
        tap_text(dialog, "Continuer")
        root = wait_dump()
        root, node = card(root, index)
        assert node.attrib["content-desc"] == desc, "Cancel exit lost the card"
    if index == 1:
        before = desc
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        wait_dump()
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        root = wait_dump()
        root, node = card(root, index)
        assert node.attrib["content-desc"] == before, "Memory state lost on rotation"
assert all(list(known.values()).count(value) == 2 for value in known.values()), known

for _ in range(20):
    states = {}
    for index in range(8):
        root, node = card(root, index)
        states[index] = node.attrib["content-desc"]
    remaining = [i for i in range(8) if "trouvée " not in states[i]]
    if not remaining:
        break
    visible = [i for i in remaining if "visible " in states[i]]
    if len(visible) == 2:
        target = next(i for i in remaining if i not in visible)
    elif len(visible) == 1:
        target = next(i for i in remaining if i != visible[0] and known[i] == known[visible[0]])
    else:
        target = remaining[0]
    root = tap_card(root, target)
else:
    raise AssertionError("Memory game did not finish")
root = wait_dump()
result = find_text(root, "Bravo !")
assert result and "Record" in result, texts(root)
assert "Nouveau record !" in result, result
record = result.split("Record : ")[1].splitlines()[0]
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert "Nouveau record !" in find_text(root, "Bravo !"), "Record message lost on rotation"
tap_text(root, "Nouvelle partie")
root = wait_dump()
assert find_text(root, "0 / 4 paires") and find_text(root, "Record : " + record), texts(root)
for index in range(8):
    root, node = card(root, index)
    assert "cachée " in node.attrib["content-desc"], node.attrib
root = tap_card(root, 0)
tap_text(root, "Nouvelle partie")
dialog = wait_dump()
assert find_text(dialog, "La partie en cours sera perdue."), texts(dialog)
tap_text(dialog, "Recommencer")
root = wait_dump()
for index in range(8):
    root, node = card(root, index)
    assert "cachée " in node.attrib["content-desc"], node.attrib
assert find_text(root, "Record : " + record), "Reset erased record"
tap_text(root, "Retour à l’accueil")
assert find_text(wait_dump(), "BIENVENUE"), "Memory return failed"
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
tap_text(wait_dump(), "Un jeu de mémoire")
root = wait_dump()
assert find_text(root, "Record : " + record), "Memory record lost after restart"
# The longer mode has its own board, saved orientation and separate record.
tap_text(root, "Défi : 6 paires")
root = wait_dump()
assert find_text(root, "0 / 6 paires"), texts(root)
assert not find_text(root, "Record : "), "Four-pair record leaked into six-pair mode"
known6 = {}
for index in range(12):
    root = tap_card(root, index)
    root, node = card(root, index)
    desc = node.attrib["content-desc"]
    assert "visible " in desc or "trouvée " in desc, desc
    known6[index] = desc.split()[-1]
    if index == 0:
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        wait_dump()
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        root = wait_dump()
        root, node = card(root, index)
        assert node.attrib["content-desc"] == desc, "Long mode lost its state on rotation"
assert all(list(known6.values()).count(value) == 2 for value in known6.values()), known6
states6 = {}
for index in range(12):
    root, node = card(root, index)
    states6[index] = node.attrib["content-desc"]
solved6 = {i for i in range(12) if "trouvée " in states6[i]}
visible6 = [i for i in range(12) if "visible " in states6[i]]
if len(visible6) == 2:
    target = next(i for i in range(12) if i not in solved6 and i not in visible6)
    root = tap_card(root, target)
    visible6 = [target]
while len(solved6) < 12:
    first6 = visible6[0] if visible6 else next(i for i in range(12) if i not in solved6)
    if not visible6:
        root = tap_card(root, first6)
    second6 = next(i for i in range(12) if i != first6 and i not in solved6 and known6[i] == known6[first6])
    root = tap_card(root, second6)
    root, first_node = card(root, first6)
    root, second_node = card(root, second6)
    assert "trouvée " in first_node.attrib["content-desc"]
    assert "trouvée " in second_node.attrib["content-desc"]
    solved6.update((first6, second6))
    visible6 = []
result6 = find_text(wait_dump(), "Bravo !")
assert result6 and "Nouveau record !" in result6, result6
record6 = result6.split("Record : ")[1].splitlines()[0]
tap_text(wait_dump(), "Mode : 4 paires")
root = wait_dump()
assert find_text(root, "0 / 4 paires") and find_text(root, "Record : " + record), texts(root)
tap_text(root, "Défi : 6 paires")
root = wait_dump()
assert find_text(root, "0 / 6 paires") and find_text(root, "Record : " + record6), texts(root)
print("Memory game smoke test passed")
