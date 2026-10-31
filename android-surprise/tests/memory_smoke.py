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
            if 50 <= (b[1] + b[3]) // 2 <= (600 if attempt >= 2 else 560):
                adb("shell", "input", "tap", str((b[0]+b[2])//2), str((b[1]+b[3])//2))
                return
            direction = ("160", "480", "160", "240") if (b[1] + b[3]) // 2 > 560 else ("160", "240", "160", "480")
        else:
            direction = ("160", "480", "160", "240")
        adb("shell", "input", "swipe", *direction, "300")
        root = wait_dump()
    raise AssertionError("Missing or unsafe control: " + label + " in " + repr(texts(root)))

def find_text(root, fragment):
    return next((v for v in texts(root) if fragment in v), None)

def description(root, text):
    return any(n.attrib.get("content-desc", "") == text for n in root.iter("node"))

adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "pm", "clear", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
# A separate matching game: discover every card, rotate, then solve the board.
def open_memory():
    root = wait_dump()
    for _ in range(3):
        if find_text(root, "MÉMO DES ÉTOILES"):
            return root
        tap_text(root, "Un jeu de mémoire")
        root = wait_dump()
    raise AssertionError("Memory screen did not open: " + repr(texts(root)))

root = open_memory()
assert description(root, "0 paires trouvées sur 4"), "Initial progress missing"

def card(root, index):
    prefix = "Carte " + str(index + 1) + " : "
    for _ in range(10):
        node = next((n for n in root.iter("node") if n.attrib.get("content-desc", "").startswith(prefix)), None)
        if node is not None:
            bounds = list(map(int, re.findall(r"\d+", node.attrib["bounds"])))
            if len(bounds) == 4 and 60 <= (bounds[1] + bounds[3]) // 2 <= 560:
                return root, node
            down = len(bounds) == 4 and (bounds[1] + bounds[3]) // 2 > 560
        else:
            visible = [int(m.group(1))-1 for n in root.iter("node")
                       if (m := re.match(r"Carte (\d+) :", n.attrib.get("content-desc", "")))]
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

# Change the visual set, preserve it on rotation, and return without losing records.
tap_text(root, "Passer aux cartes escapade")
root = wait_dump()
root = tap_card(root, 0)
root, node = card(root, 0)
pack_desc = node.attrib["content-desc"]
assert "visible " in pack_desc and pack_desc.split()[-1] in {"🎹","🍕","⌚","☕","🌙","🎷","🍇","🏔","🎸","🚂","⭐","🍋","🌻","🎺","🐈","🎲"}, pack_desc
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
root, node = card(root, 0)
assert node.attrib["content-desc"] == pack_desc, "Visual set lost after rotation"
tap_text(root, "Passer aux cartes nature")
dialog = wait_dump()
assert find_text(dialog, "Changer les cartes"), texts(dialog)
tap_text(dialog, "Changer")
root = wait_dump()
assert description(root, "0 paires trouvées sur 4"), texts(root)
root = tap_card(root, 0)
root, node = card(root, 0)
nature_desc = node.attrib["content-desc"]
assert "visible " in nature_desc and nature_desc.split()[-1] in {"🌸","🌿","🍀","🌵","🍄","🌼","🍁","🌳","🐝","🦋","🐞","🐬","🦉","🐢","🐟","🌷"}, nature_desc
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
root, node = card(root, 0)
assert node.attrib["content-desc"] == nature_desc, "Third visual set lost on rotation"
# A cancelled switch must not change the selected pack across process restart.
tap_text(root, "Passer aux cartes étoiles")
dialog = wait_dump()
tap_text(dialog, "Continuer")
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = open_memory()
tap_text(root, "Passer aux cartes étoiles")
root = wait_dump()
assert description(root, "0 paires trouvées sur 4"), texts(root)

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
        tap_text(root, "Défi : 6 paires")
        dialog = wait_dump()
        assert find_text(dialog, "Changer de défi"), texts(dialog)
        tap_text(dialog, "Continuer")
        root = wait_dump()
        root, node = card(root, index)
        assert node.attrib["content-desc"] == desc, "Cancel difficulty switch lost the card"
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
assert result and "Record" in result and "Meilleure série : " in result, texts(root)
assert description(root, "4 paires trouvées sur 4"), "Completed progress missing"
assert "Nouveau record !" in result, result
moves = int(re.search(r"Bravo ! (\d+) essais", result).group(1))
expected_stars = 3 if moves <= 5 else 2 if moves <= 8 else 1
assert result.splitlines()[-1].count("✦") == expected_stars, result
assert "Étoiles : "+str(expected_stars)+"/3" in result, result
record = result.split("Record : ")[1].splitlines()[0]
series = int(re.search(r"Série record : (\d+)",result).group(1))
assert series == int(re.search(r"Meilleure série : (\d+)",result).group(1))
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
assert find_text(root, "Bravo !") == result, "Result or streak lost on rotation"
tap_text(root, "Nouvelle partie")
root = wait_dump()
assert find_text(root, "0 / 4 paires") and find_text(root, "Record : " + record), texts(root)
for index in range(8):
    root, node = card(root, index)
    assert "cachée " in node.attrib["content-desc"], node.attrib
# A replay must not reuse exactly the same set of symbols.
replay = {}
for index in range(8):
    root = tap_card(root, index)
    root, node = card(root, index)
    replay[index] = node.attrib["content-desc"].split()[-1]
assert set(replay.values()) != set(known.values()), "Replay reused the same symbol set"
tap_text(root, "Nouvelle partie")
root = wait_dump()
if find_text(root, "La partie en cours sera perdue."):
    tap_text(root, "Recommencer")
    root = wait_dump()
current_record = find_text(root, "Record : ")
assert current_record, texts(root)
updated_record = current_record.split("Record : ")[1].splitlines()[0]
assert int(updated_record) <= int(record), "Record became worse after replay"
record = updated_record
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
# The optional hint reveals one matching pair, survives rotation and costs two moves.
tap_text(root, "Indice · une fois (+2 essais)")
root = wait_dump()
revealed = []
for index in range(8):
    root, node = card(root, index)
    desc = node.attrib["content-desc"]
    if "visible " in desc:
        revealed.append((index, desc.split()[-1]))
assert len(revealed) == 2 and revealed[0][1] == revealed[1][1], revealed
adb("shell", "settings", "put", "system", "user_rotation", "1")
wait_dump()
adb("shell", "settings", "put", "system", "user_rotation", "0")
root = wait_dump()
for index, symbol in revealed:
    root, node = card(root, index)
    assert "visible " in node.attrib["content-desc"] and node.attrib["content-desc"].endswith(symbol)
root = tap_card(root, revealed[0][0])
root = tap_card(root, revealed[1][0])
for index, _ in revealed:
    root, node = card(root, index)
    assert "trouvée " in node.attrib["content-desc"], node.attrib
for _ in range(3):
    adb("shell", "input", "swipe", "160", "220", "160", "480", "300")
root = wait_dump()
assert find_text(root, "3 essais"), texts(root)
tap_text(root, "Nouvelle partie")
dialog = wait_dump()
tap_text(dialog, "Recommencer")
root = wait_dump()
tap_text(root, "Retour à l’accueil")
root = wait_dump()
# The previous screen can retain its scroll offset, especially with system insets.
for _ in range(3):
    if find_text(root, "BIENVENUE"):
        break
    adb("shell", "input", "swipe", "160", "200", "160", "520", "300")
    root = wait_dump()
assert find_text(root, "BIENVENUE"), ("Memory return failed", texts(root))
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = open_memory()
assert find_text(root, "Record : " + record), "Memory record lost after restart"
series_text=find_text(root,"Série record : ")
assert series_text and int(re.search(r"Série record : (\d+)",series_text).group(1)) >= series, "Streak record lost after restart"
best_moves=int(record)
best_stars=3 if best_moves<=5 else 2 if best_moves<=8 else 1
assert "Étoiles : "+str(best_stars)+"/3" in series_text, "Best stars lost after restart"
# The longer mode has its own board, saved orientation and separate record.
tap_text(root, "Défi : 6 paires")
root = wait_dump()
assert find_text(root, "0 / 6 paires"), texts(root)
assert description(root, "0 paires trouvées sur 6"), "Long-mode progress missing"
assert not find_text(root, "Record : "), "Four-pair record leaked into six-pair mode"
assert not find_text(root, "Série record : "), "Streak record leaked into six-pair mode"
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
initial_solved6 = len(solved6) // 2
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
for _ in range(4):
    adb("shell", "input", "swipe", "160", "220", "160", "480", "300")
result6 = find_text(wait_dump(), "Bravo !")
assert result6 and "Nouveau record !" in result6, result6
streak6 = int(re.search(r"Meilleure série : (\d+)", result6).group(1))
assert 6 - initial_solved6 <= streak6 <= 6, (initial_solved6, result6)
assert description(wait_dump(), "6 paires trouvées sur 6"), "Long-mode progress incomplete"
moves6 = int(re.search(r"Bravo ! (\d+) essais", result6).group(1))
expected_stars6 = 3 if moves6 <= 7 else 2 if moves6 <= 12 else 1
assert result6.splitlines()[-1].count("✦") == expected_stars6, result6
record6 = result6.split("Record : ")[1].splitlines()[0]
series6 = int(re.search(r"Série record : (\d+)",result6).group(1))
assert series6 == int(re.search(r"Meilleure série : (\d+)",result6).group(1))
tap_text(wait_dump(), "Défi : 8 paires")
root = wait_dump()
assert find_text(root, "0 / 8 paires") and not find_text(root, "Record : "), texts(root)
assert description(root, "0 paires trouvées sur 8"), "Expert progress missing"
known8 = {}
for index in range(16):
    root = tap_card(root, index)
    root, node = card(root, index)
    known8[index] = node.attrib["content-desc"].split()[-1]
    if index == 0:
        before = node.attrib["content-desc"]
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        wait_dump()
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        root = wait_dump()
        root, node = card(root, index)
        assert node.attrib["content-desc"] == before, "Expert mode lost state on rotation"
assert all(list(known8.values()).count(value) == 2 for value in known8.values()), known8
states8 = {}
for index in range(16):
    root, node = card(root, index)
    states8[index] = node.attrib["content-desc"]
solved8 = {i for i in range(16) if "trouvée " in states8[i]}
visible8 = [i for i in range(16) if "visible " in states8[i]]
if len(visible8) == 2:
    target = next(i for i in range(16) if i not in solved8 and i not in visible8)
    root = tap_card(root, target)
    visible8 = [target]
while len(solved8) < 16:
    first8 = visible8[0] if visible8 else next(i for i in range(16) if i not in solved8)
    if not visible8:
        root = tap_card(root, first8)
    second8 = next(i for i in range(16) if i != first8 and i not in solved8 and known8[i] == known8[first8])
    root = tap_card(root, second8)
    root, first_node = card(root, first8)
    root, second_node = card(root, second8)
    assert "trouvée " in first_node.attrib["content-desc"]
    assert "trouvée " in second_node.attrib["content-desc"]
    solved8.update((first8, second8))
    visible8 = []
for _ in range(4):
    adb("shell", "input", "swipe", "160", "220", "160", "480", "300")
root = wait_dump()
result8 = find_text(root, "Bravo !")
assert result8 and "Nouveau record !" in result8, result8
assert description(root, "8 paires trouvées sur 8"), "Expert progress incomplete"
record8 = result8.split("Record : ")[1].splitlines()[0]
series8 = int(re.search(r"Série record : (\d+)",result8).group(1))
assert series8 == int(re.search(r"Meilleure série : (\d+)",result8).group(1))
# The longest mode has its own 20-card deck and record, including rotation.
tap_text(root, "Défi : 10 paires")
root = wait_dump()
assert find_text(root, "0 / 10 paires") and not find_text(root, "Record : "), texts(root)
known10 = {}
for index in range(20):
    root = tap_card(root, index)
    root, node = card(root, index)
    known10[index] = node.attrib["content-desc"].split()[-1]
    if index == 0:
        before = node.attrib["content-desc"]
        adb("shell", "settings", "put", "system", "user_rotation", "1")
        wait_dump()
        adb("shell", "settings", "put", "system", "user_rotation", "0")
        root = wait_dump()
        root, node = card(root, index)
        assert node.attrib["content-desc"] == before, "Longer mode lost state on rotation"
assert all(list(known10.values()).count(value) == 2 for value in known10.values()), known10
states10 = {}
for index in range(20):
    root, node = card(root, index)
    states10[index] = node.attrib["content-desc"]
solved10 = {i for i in range(20) if "trouvée " in states10[i]}
visible10 = [i for i in range(20) if "visible " in states10[i]]
if len(visible10) == 2:
    target = next(i for i in range(20) if i not in solved10 and i not in visible10)
    root = tap_card(root, target)
    visible10 = [target]
while len(solved10) < 20:
    first10 = visible10[0] if visible10 else next(i for i in range(20) if i not in solved10)
    if not visible10:
        root = tap_card(root, first10)
    second10 = next(i for i in range(20) if i != first10 and i not in solved10 and known10[i] == known10[first10])
    root = tap_card(root, second10)
    root, first_node = card(root, first10)
    root, second_node = card(root, second10)
    assert "trouvée " in first_node.attrib["content-desc"]
    assert "trouvée " in second_node.attrib["content-desc"]
    solved10.update((first10, second10))
    visible10 = []
for _ in range(6):
    adb("shell", "input", "swipe", "160", "220", "160", "480", "300")
root = wait_dump()
result10 = find_text(root, "Bravo !")
assert result10 and "Nouveau record !" in result10, result10
assert description(root, "10 paires trouvées sur 10"), texts(root)
record10 = result10.split("Record : ")[1].splitlines()[0]
series10 = int(re.search(r"Série record : (\d+)",result10).group(1))
assert series10 == int(re.search(r"Meilleure série : (\d+)",result10).group(1))
tap_text(root, "Mode : 4 paires")
root = wait_dump()
assert find_text(root, "0 / 4 paires") and find_text(root, "Record : " + record), texts(root)
tap_text(root, "Défi : 6 paires")
root = wait_dump()
assert find_text(root, "0 / 6 paires") and find_text(root, "Record : " + record6), texts(root)
assert find_text(root, "Série record : " + str(series6)), "Streak record not isolated for 6 pairs"
assert "Étoiles : " in find_text(root,"Série record : "), "Best stars missing for 6 pairs"
tap_text(root, "Défi : 8 paires")
root = wait_dump()
assert find_text(root, "0 / 8 paires") and find_text(root, "Record : " + record8), texts(root)
assert find_text(root, "Série record : " + str(series8)), "Streak record not isolated for 8 pairs"
assert "Étoiles : " in find_text(root,"Série record : "), "Best stars missing for 8 pairs"
tap_text(root, "Défi : 10 paires")
root = wait_dump()
assert find_text(root, "0 / 10 paires") and find_text(root, "Record : " + record10), texts(root)
assert find_text(root, "Série record : " + str(series10)), "Streak record not isolated for 10 pairs"
assert "Étoiles : " in find_text(root,"Série record : "), "Best stars missing for 10 pairs"
# A cancelled change must not overwrite the chosen difficulty, even after restart.
root = tap_card(root, 0)
tap_text(root, "Mode : 4 paires")
dialog = wait_dump()
assert find_text(dialog, "Changer de défi"), texts(dialog)
tap_text(dialog, "Continuer")
adb("shell", "am", "force-stop", "ch.fred.surprise")
adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
root = open_memory()
assert find_text(root, "0 / 10 paires") and find_text(root, "Record : " + record10), ("Difficulty lost after restart", texts(root))
tap_text(root, "Mode : 4 paires")
root = wait_dump()
assert find_text(root, "0 / 4 paires") and find_text(root, "Record : " + record), texts(root)
# On a narrow 240dp display the cards must remain easy to touch.
try:
    adb("shell", "wm", "size", "240x640")
    adb("shell", "wm", "density", "160")
    adb("shell", "settings", "put", "system", "font_scale", "1.3")
    adb("shell", "am", "force-stop", "ch.fred.surprise")
    adb("shell", "am", "start", "-n", "ch.fred.surprise/.MainActivity")
    root = open_memory()
    positions = []
    for index in range(4):
        root, node = card(root, index)
        bounds = list(map(int, re.findall(r"\d+", node.attrib["bounds"])))
        assert bounds[2] - bounds[0] >= 48 and bounds[3] - bounds[1] >= 48, ("Narrow touch target", index, bounds)
        positions.append(bounds)
    assert positions[0][1] == positions[1][1] == positions[2][1], positions
    assert positions[3][1] > positions[0][1], ("Expected three-column layout", positions)
    assert all(positions[i+1][0]-positions[i][2]>=8 for i in range(2)), ("Narrow targets crowded",positions)
    root, first=card(root,0)
    before_outside=first.attrib["content-desc"]
    first_bounds=list(map(int,re.findall(r"\d+",first.attrib["bounds"])))
    adb("shell","input","tap","2",str((first_bounds[1]+first_bounds[3])//2))
    root=wait_dump()
    root, first=card(root,0)
    assert first.attrib["content-desc"]==before_outside, "Outside tap selected a card"
    capture("memory-small")
    root, node = card(root, 0)
    edge = list(map(int, re.findall(r"\d+", node.attrib["bounds"])))
    adb("shell", "input", "tap", str(edge[0] + 4), str(edge[1] + 4))
    root = wait_dump()
    root, node = card(root, 0)
    before = node.attrib["content-desc"]
    assert "visible " in before, "Touch near target edge was not registered"
    adb("shell", "settings", "put", "system", "user_rotation", "1")
    wait_dump()
    adb("shell", "settings", "put", "system", "user_rotation", "0")
    root = wait_dump()
    root, node = card(root, 0)
    assert node.attrib["content-desc"] == before, "Narrow-screen rotation lost state"
    tap_text(root, "Passer aux cartes escapade")
    dialog = wait_dump()
    assert find_text(dialog, "Changer les cartes"), texts(dialog)
    tap_text(dialog, "Continuer")
    root = wait_dump()
    root, node = card(root, 0)
    assert node.attrib["content-desc"] == before, "Cancel on narrow screen lost state"
finally:
    adb("shell", "wm", "size", "reset")
    adb("shell", "wm", "density", "reset")
    adb("shell", "settings", "delete", "system", "font_scale")
# Simulate an older installation: it has a move record but no streak record.
# Only the disposable emulator's private debug preferences are edited.
adb("shell","am","force-stop","ch.fred.surprise")
prefs_path="shared_prefs/MemoryActivity.xml"
prefs=ET.fromstring(adb("shell","run-as","ch.fred.surprise","cat",prefs_path))
old_streak=next((n for n in prefs if n.attrib.get("name")=="streak_4"),None)
assert old_streak is not None and any(n.attrib.get("name")=="best" for n in prefs), "Legacy fixture missing records"
prefs.remove(old_streak)
updated=ET.tostring(prefs,encoding="unicode")
subprocess.run(["adb","shell","run-as","ch.fred.surprise","tee",prefs_path],
               input=updated,text=True,stdout=subprocess.DEVNULL,check=True)
adb("shell","am","start","-n","ch.fred.surprise/.MainActivity")
root=open_memory()
legacy=find_text(root,"Record : ")
assert legacy and "Étoiles : " in legacy and "Série record :" not in legacy, ("Legacy award missing",texts(root))
# Invalid stored configuration safely falls back to the original default.
adb("shell","am","force-stop","ch.fred.surprise")
prefs=ET.fromstring(adb("shell","run-as","ch.fred.surprise","cat",prefs_path))
choice=next((n for n in prefs if n.attrib.get("name")=="pairCount"),None)
assert choice is not None, "Persisted difficulty missing"
choice.set("value","999")
subprocess.run(["adb","shell","run-as","ch.fred.surprise","tee",prefs_path],
               input=ET.tostring(prefs,encoding="unicode"),text=True,stdout=subprocess.DEVNULL,check=True)
adb("shell","am","start","-n","ch.fred.surprise/.MainActivity")
root=open_memory()
assert find_text(root,"0 / 4 paires") and find_text(root,"Record : "+record), ("Invalid preference fallback failed",texts(root))
# Upgrade a pre-palette preference without discarding its chosen card set.
adb("shell","am","force-stop","ch.fred.surprise")
prefs=ET.fromstring(adb("shell","run-as","ch.fred.surprise","cat",prefs_path))
palette=next((n for n in prefs if n.attrib.get("name")=="palette"),None)
assert palette is not None, "Saved palette missing"
prefs.remove(palette)
legacy_pack=next((n for n in prefs if n.attrib.get("name")=="escapade"),None)
if legacy_pack is None:
    legacy_pack=ET.SubElement(prefs,"boolean",{"name":"escapade"})
legacy_pack.set("value","true")
subprocess.run(["adb","shell","run-as","ch.fred.surprise","tee",prefs_path],
               input=ET.tostring(prefs,encoding="unicode"),text=True,stdout=subprocess.DEVNULL,check=True)
adb("shell","am","start","-n","ch.fred.surprise/.MainActivity")
root=open_memory()
tap_text(root,"Passer aux cartes nature")  # Legacy selection migrated; switch to the third pack.
root=wait_dump()
root=tap_card(root,0)
root,node=card(root,0)
assert node.attrib["content-desc"].split()[-1] in {"🌸","🌿","🍀","🌵","🍄","🌼","🍁","🌳","🐝","🦋","🐞","🐬","🦉","🐢","🐟","🌷"}, node.attrib
print("Memory game smoke test passed")
