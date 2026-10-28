#!/usr/bin/env python3
"""Black-box smoke test on the CI Android emulator."""
import re
import subprocess
import time
import xml.etree.ElementTree as ET

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
tap_text(root, "C’est parti")
for index in range(8):
    adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
    root = wait_dump()
    assert find_text(root, "MANCHE  " + str(index+1)), texts(root)
    buttons = [n for n in root.iter("node") if n.attrib.get("class") == "android.widget.Button" and n.attrib.get("clickable") == "true" and not n.attrib.get("text", "").startswith("Joker")]
    assert len(buttons) >= 2, texts(root)
    original_order = [button.attrib["text"] for button in buttons]
    chosen = buttons[0].attrib["text"]
    if index == 0:
        tap_text(root, "Joker 50 / 50 · une fois")
        adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
        root = wait_dump()
        buttons = [n for n in root.iter("node") if n.attrib.get("class") == "android.widget.Button" and n.attrib.get("clickable") == "true" and not n.attrib.get("text", "").startswith("Joker")]
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
assert find_text(root, "Record"), texts(root)
tap_text(root, "Rejouer")
assert find_text(wait_dump(), "MANCHE  1"), "Replay failed"
print("Android UI smoke test passed")
