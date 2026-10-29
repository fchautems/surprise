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
            center_y = (b[1] + b[3]) // 2
            if 50 <= center_y <= 560 or (attempt == 3 and 50 <= center_y <= 600):
                print("Tap", label, "bounds", b, flush=True)
                adb("shell", "input", "tap", str((b[0]+b[2])//2), str(center_y))
                return
            direction = ("160", "480", "160", "240") if b[1] > 500 else ("160", "240", "160", "480")
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
root = wait_dump()
assert find_text(root, "BIENVENUE"), texts(root)
capture("accueil")
tap_text(root, "C’est parti")
source = (Path(__file__).resolve().parents[1] / "app/src/main/java/ch/fred/surprise/MainActivity.java").read_text(encoding="utf-8")
bank = {m.group(1) for m in re.finditer(r'\{"[^"]+","([^"]+)","[^"]+","[^"]+","[^"]+","[^"]+"\}', source)}
correct_by_question = {
    m.group(1): m.group(2)
    for m in re.finditer(r'\{"[^"]+","([^"]+)","([^"]+)","[^"]+","[^"]+","[^"]+"\}', source)
}
options_by_question = {
    m.group(1): set(m.groups()[1:])
    for m in re.finditer(r'\{"[^"]+","([^"]+)","([^"]+)","([^"]+)","([^"]+)","([^"]+)"\}', source)
}
seen = set()
missed_questions = set()
first_missed = None
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
        first_missed = next(iter(displayed))
        # Accidental back presses offer a way to stay in the current game.
        adb("shell", "input", "keyevent", "4")
        dialog = wait_dump()
        assert find_text(dialog, "Quitter la partie"), texts(dialog)
        tap_text(dialog, "Rester")
        root = wait_dump()
        assert find_text(root, "MANCHE  1"), texts(root)
        tap_text(root, "Joker 50 / 50 · une fois")
        adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
        root = wait_dump()
        buttons = [n for n in root.iter("node") if n.attrib.get("class") == "android.widget.Button" and n.attrib.get("clickable") == "true" and n.attrib.get("enabled") == "true" and not n.attrib.get("text", "").startswith("Joker")]
        assert len(buttons) == 2, texts(root)
        wrong = [button.attrib["text"] for button in buttons if button.attrib["text"] != correct_by_question[first_missed]]
        assert len(wrong) == 1, (first_missed, buttons)
        chosen = wrong[0]
    if chosen != correct_by_question[next(iter(displayed))]:
        missed_questions.update(displayed)
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
tap_text(root, "Revoir mes réponses")
dialog = wait_dump()
recap = find_text(dialog, first_missed)
assert recap and correct_by_question[first_missed] in recap, texts(dialog)
assert recap.count(first_missed) == 1, "Rotation duplicated the missed answer"
tap_text(dialog, "Fermer")
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert find_text(root, "bonnes réponses") == result_before, texts(root)
tap_text(root, "Revoir mes réponses")
dialog = wait_dump()
recap = find_text(dialog, first_missed)
assert recap and recap.count(first_missed) == 1, "Review lost or duplicated after result rotation"
tap_text(dialog, "Fermer")
tap_text(wait_dump(), "Prendre ma revanche")
retry = wait_dump()
assert (set(texts(retry)) & bank) <= missed_questions and (set(texts(retry)) & bank), texts(retry)
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
retry_rotated = wait_dump()
assert (set(texts(retry_rotated)) & bank) == (set(texts(retry)) & bank), "Retry lost on rotation"
retry_seen = set()
for index in range(8):
    adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
    root = wait_dump()
    shown = set(texts(root)) & bank
    assert len(shown) == 1 and not (shown & retry_seen), (shown, retry_seen)
    if index < len(missed_questions):
        assert shown <= missed_questions, (index, shown, missed_questions)
    else:
        assert not (shown & seen), ("filler question repeated", shown)
    retry_seen.update(shown)
    tap_text(root, correct_by_question[next(iter(shown))])
    tap_text(wait_dump(), "Continuer" if index < 7 else "Voir le résultat")
root = wait_dump()
assert find_text(root, "130 points") and find_text(root, "Sans-faute"), texts(root)
assert not find_text(root, "Prendre ma revanche"), "Perfect retry offered another retry"
tap_text(root, "Rejouer")
replay = wait_dump()
assert find_text(replay, "MANCHE  1"), "Replay failed"
displayed = set(texts(replay)) & bank
assert len(displayed) == 1 and not (displayed & seen), ("repeated across games", displayed)
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = wait_dump()
assert find_text(root, "Parties : 2"), texts(root)
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
# At large font sizes the header may be above the visible viewport.
adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
root = wait_dump()
assert find_text(root, "MANCHE  2"), ("Large-font continuation failed", texts(root))
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
for theme in ("MUSIQUE", "CUISINE", "VOYAGE", "CIEL", "ALLEMAND", "TEMPS", "CRYPTO"):
    adb("shell", "am", "force-stop", "ch.fred.surprise")
    adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
    tap_text(wait_dump(), "Choisir un thème")
    tap_text(wait_dump(), theme)
    root = wait_dump()
    assert find_text(root, "MANCHE  1"), (theme, texts(root))
    displayed = set(texts(root)) & bank
    assert len(displayed) == 1 and displayed <= category_bank[theme], (theme, displayed)
# Confirm abandoning a game returns to the welcome screen.
adb("shell", "input", "keyevent", "4")
dialog = wait_dump()
assert find_text(dialog, "Quitter la partie"), texts(dialog)
tap_text(dialog, "Quitter")
root = wait_dump()
assert find_text(root, "BIENVENUE"), "Leaving the game failed"

# Play the newly added theme end to end, not just its first question.
tap_text(root, "Choisir un thème")
tap_text(wait_dump(), "CRYPTO")
themed_seen = set()
themed_missed = None
for index in range(8):
    adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
    root = wait_dump()
    displayed = set(texts(root)) & bank
    assert len(displayed) == 1 and displayed <= category_bank["CRYPTO"], texts(root)
    assert not (displayed & themed_seen), displayed
    themed_seen.update(displayed)
    correct = correct_by_question[next(iter(displayed))]
    if index == 0:
        themed_missed = next(iter(displayed))
        buttons = [n.attrib["text"] for n in root.iter("node")
                   if n.attrib.get("class") == "android.widget.Button"
                   and n.attrib.get("enabled") == "true"
                   and n.attrib.get("text") in options_by_question[themed_missed]]
        chosen = next(value for value in buttons if value != correct)
    else:
        chosen = correct
    tap_text(root, chosen)
    tap_text(wait_dump(), "Continuer" if index < 7 else "Voir le résultat")
root = wait_dump()
assert find_text(root, "110 points"), texts(root)
assert find_text(root, "Record CRYPTO : 110 points"), texts(root)
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert find_text(root, "Record CRYPTO : 110 points"), "Thematic record lost on rotation"
tap_text(root, "Prendre ma revanche")
retry_seen = set()
for index in range(8):
    adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
    retry = wait_dump()
    shown = set(texts(retry)) & bank
    assert len(shown)==1 and shown <= category_bank["CRYPTO"], (index,shown,texts(retry))
    if index == 0:
        assert shown == {themed_missed}, (shown,themed_missed)
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        wait_dump()
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        retry = wait_dump()
        assert (set(texts(retry)) & bank) == shown, "Thematic retry lost on rotation"
    assert not (shown & retry_seen), shown
    retry_seen.update(shown)
    tap_text(retry, correct_by_question[next(iter(shown))])
    tap_text(wait_dump(), "Continuer" if index < 7 else "Voir le résultat")
root = wait_dump()
assert find_text(root, "130 points"), texts(root)
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert find_text(root, "130 points"), "Retry result lost on rotation"
adb("shell", "input", "keyevent", "4")
root = wait_dump()
assert find_text(root, "BIENVENUE"), "Back from thematic result failed"
tap_text(root, "Mes records")
dialog = wait_dump()
assert find_text(dialog, "CRYPTO  ·  110 points"), texts(dialog)
assert find_text(dialog, "À découvrir"), texts(dialog)
tap_text(dialog, "Fermer")
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
tap_text(wait_dump(), "Mes records")
dialog = wait_dump()
assert find_text(dialog, "CRYPTO  ·  110 points"), "Thematic record lost after restart"
tap_text(dialog, "Fermer")
root = wait_dump()

# Today's challenge must be reproducible and its record must survive a restart.
tap_text(root, "Défi du jour")
root = wait_dump()
daily_first = set(texts(root)) & bank
assert len(daily_first) == 1, texts(root)
adb("shell", "input", "keyevent", "4")
tap_text(wait_dump(), "Quitter")
tap_text(wait_dump(), "Défi du jour")
root = wait_dump()
assert (set(texts(root)) & bank) == daily_first, "Daily challenge changed on replay"
daily_seen = set()
for index in range(8):
    adb("shell", "input", "swipe", "160", "180", "160", "550", "300")
    root = wait_dump()
    shown = set(texts(root)) & bank
    assert len(shown) == 1 and not (shown & daily_seen), (shown, daily_seen)
    daily_seen.update(shown)
    if index == 0:
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        wait_dump()
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        root = wait_dump()
        assert (set(texts(root)) & bank) == daily_first, "Daily question lost on rotation"
    correct = correct_by_question[next(iter(shown))]
    tap_text(root, correct)
    tap_text(wait_dump(), "Continuer" if index < 7 else "Voir le résultat")
root = wait_dump()
assert find_text(root, "130 points"), texts(root)
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert find_text(root, "130 points"), "Daily result lost on rotation"
tap_text(root, "Rejouer")
root = wait_dump()
assert (set(texts(root)) & bank) == daily_first, "Daily replay changed"
adb("shell", "input", "keyevent", "4")
tap_text(wait_dump(), "Quitter")
root = wait_dump()
assert find_text(root, "Défi du jour · record 130"), texts(root)
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
assert find_text(wait_dump(), "Défi du jour · record 130"), "Daily record lost on restart"
print("Android UI smoke test passed")
