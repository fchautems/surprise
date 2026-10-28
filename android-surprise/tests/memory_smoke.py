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
    for _ in range(4):
        node = next((n for n in root.iter("node") if n.attrib.get("content-desc", "").startswith(prefix)), None)
        if node is not None:
            return root, node
        if index < 4:
            adb("shell", "input", "swipe", "160", "220", "160", "480", "300")
        else:
            adb("shell", "input", "swipe", "160", "480", "160", "220", "300")
        root = wait_dump()
    raise AssertionError("Memory card missing: " + str(index + 1))

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
assert find_text(root, "Bravo !") and find_text(root, "Record"), texts(root)
tap_text(root, "Retour à l’accueil")
assert find_text(wait_dump(), "BIENVENUE"), "Memory return failed"
print("Memory game smoke test passed")
