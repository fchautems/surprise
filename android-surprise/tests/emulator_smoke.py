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
    if not find_text(root, label):
        adb("shell", "input", "swipe", "160", "540", "160", "180", "300")
        root = wait_dump()
    for node in root.iter("node"):
        if node.attrib.get("text") == label:
            b = list(map(int, re.findall(r"\d+", node.attrib["bounds"])))
            assert len(b) == 4, node.attrib
            adb("shell", "input", "tap", str((b[0]+b[2])//2), str((b[1]+b[3])//2))
            return
    raise AssertionError("Missing control: " + label)

def find_text(root, fragment):
    return next((v for v in texts(root) if fragment in v), None)

adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "pm", "clear", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = wait_dump()
assert find_text(root, "BIENVENUE"), texts(root)
capture("accueil")
tap_text(root, "C’est parti")
source = (Path(__file__).resolve().parents[1] / "app/src/main/java/ch/fred/surprise/MainActivity.java").read_text(encoding="utf-8")
bank = {m.group(1) for m in re.finditer(r'\{"[^"]+","([^"]+)","[^"]+","[^"]+","[^"]+","[^"]+"\}', source)}
seen = set()
for index in range(8):
    adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
    root = wait_dump()
    assert find_text(root, "MANCHE  " + str(index+1)), texts(root)
    displayed = set(texts(root)) & bank
    assert len(displayed) == 1 and not (displayed & seen), displayed
    if index == 0: capture("partie")
    seen.update(displayed)
    buttons = [n for n in root.iter("node") if n.attrib.get("class") == "android.widget.Button" and n.attrib.get("clickable") == "true" and n.attrib.get("enabled") == "true" and not n.attrib.get("text", "").startswith("Joker")]
    assert len(buttons) >= 2, texts(root)
    original_order = [button.attrib["text"] for button in buttons]
    chosen = buttons[0].attrib["text"]
    if index == 0:
        tap_text(root, "Joker 50 / 50 · une fois")
        adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
        root = wait_dump()
        buttons = [n for n in root.iter("node") if n.attrib.get("class") == "android.widget.Button" and n.attrib.get("clickable") == "true" and n.attrib.get("enabled") == "true" and not n.attrib.get("text", "").startswith("Joker")]
        assert len(buttons) == 2, texts(root)
        chosen = buttons[0].attrib["text"]
    tap_text(root, chosen)
    adb("shell", "input", "swipe", "160", "540", "160", "180", "300")
    root = wait_dump()
    assert find_text(root, "Continuer" if index < 7 else "Voir le résultat"), texts(root)
    if index == 0:
        before = find_text(root, "Bravo") or find_text(root, "La réponse")
        adb("shell", "settings", "put", "system", "accelerometer_rotation", "0")
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        # The compact landscape viewport may hide feedback. Rotate back before
        # checking it, so this verifies restoration across both recreations.
        wait_dump()
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        adb("shell", "input", "swipe", "160", "540", "160", "180", "300")
        root = wait_dump()
        after = find_text(root, "Bravo") or find_text(root, "La réponse")
        assert before == after, (before, after, texts(root))
        adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
        root = wait_dump()
        rotated = [n.attrib.get("text") for n in root.iter("node") if n.attrib.get("class") == "android.widget.Button" and n.attrib.get("text") in original_order]
        assert rotated == original_order[:len(rotated)] and len(rotated) >= 2, (original_order, rotated)
    tap_text(root, "Continuer" if index < 7 else "Voir le résultat")
root = wait_dump()
assert find_text(root, "PARTIE TERMINÉE"), texts(root)
capture("resultat")
assert find_text(root, "Record"), texts(root)
result_before = find_text(root, "bonnes réponses")
assert result_before, texts(root)
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert find_text(root, "bonnes réponses") == result_before, texts(root)
tap_text(root, "Rejouer")
replay = wait_dump()
assert find_text(replay, "MANCHE  1"), "Replay failed"
displayed = set(texts(replay)) & bank
assert len(displayed) == 1 and not (displayed & seen), ("repeated across games", displayed)
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = wait_dump()
assert find_text(root, "Parties : 1"), texts(root)
# Repeat entry navigation with larger system text; scrolling must remain usable.
adb("shell", "settings", "put", "system", "font_scale", "1.3")
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = wait_dump()
assert find_text(root, "BIENVENUE"), texts(root)
tap_text(root, "C’est parti")
root = wait_dump()
assert find_text(root, "MANCHE  1"), texts(root)
buttons = [n for n in root.iter("node") if n.attrib.get("class") == "android.widget.Button" and n.attrib.get("enabled") == "true" and not n.attrib.get("text", "").startswith("Joker")]
assert buttons, texts(root)
tap_text(root, buttons[0].attrib["text"])
tap_text(wait_dump(), "Continuer")
assert find_text(wait_dump(), "MANCHE  2"), "Large-font continuation failed"
adb("shell", "settings", "put", "system", "font_scale", "1.0")
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = wait_dump()
tap_text(root, "Choisir un thème")
root = wait_dump()
assert find_text(root, "CHOISIS TON THÈME"), texts(root)
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert find_text(root, "CHOISIS TON THÈME"), texts(root)
tap_text(root, "MUSIQUE")
root = wait_dump()
assert find_text(root, "MANCHE  1") and find_text(root, "MUSIQUE"), texts(root)
# Verify that every thematic entry opens a question from the chosen category.
category_bank = {}
for match in re.finditer(r'\{"([^"]+)","([^"]+)","[^"]+","[^"]+","[^"]+","[^"]+"\}', source):
    category_bank.setdefault(match.group(1), set()).add(match.group(2))
for theme in ("MUSIQUE", "CUISINE", "VOYAGE", "CIEL", "ALLEMAND", "TEMPS"):
    adb("shell", "am", "force-stop", "ch.fred.surprise")
    adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
    tap_text(wait_dump(), "Choisir un thème")
    tap_text(wait_dump(), theme)
    root = wait_dump()
    assert find_text(root, "MANCHE  1"), (theme, texts(root))
    displayed = set(texts(root)) & bank
    assert len(displayed) == 1 and displayed <= category_bank[theme], (theme, displayed)
print("Android UI smoke test passed")
