#!/usr/bin/env python3
"""Check readable contrast for the shared card palette."""
import math
import re
import unittest
from pathlib import Path

SOURCE = Path(__file__).resolve().parents[1] / "app/src/main/java/ch/fred/surprise"

def luminance(rgb):
    channels = [v / 255 for v in rgb]
    linear = [v / 12.92 if v <= 0.04045 else ((v + 0.055) / 1.055) ** 2.4 for v in channels]
    return sum(c * w for c, w in zip(linear, (0.2126, 0.7152, 0.0722)))

def contrast(a, b):
    values = sorted((luminance(a), luminance(b)))
    return (values[1] + 0.05) / (values[0] + 0.05)

class PaletteContrastTest(unittest.TestCase):
    def test_shared_surfaces_and_text(self):
        for name in ("MainActivity.java", "MusicActivity.java", "MemoryActivity.java"):
            with self.subTest(screen=name):
                source=(SOURCE / name).read_text(encoding="utf-8")
                self.assertIn("Color.rgb(39,53,80),CARD", source)
                self.assertIn("Color.rgb(31,42,68)", source)
                self.assertIn("RippleDrawable", source)
        for background in ((39,53,80),(31,42,68)):
            for ink in ((245,242,235),(255,195,107),(147,207,201),(179,185,255),
                        (241,179,122),(244,168,141),(218,184,234),(248,196,116)):
                self.assertGreaterEqual(contrast(background,ink),4.5,(background,ink))

    def test_progress_track_contrast(self):
        track=(98,110,133)
        for name in ("MainActivity.java", "MemoryActivity.java"):
            self.assertIn("setProgressBackgroundTintList", (SOURCE / name).read_text(encoding="utf-8"))
            self.assertIn("Color.rgb(98,110,133)", (SOURCE / name).read_text(encoding="utf-8"))
        for background in ((17,22,42),(20,31,53),(34,25,44)):
            self.assertGreaterEqual(contrast(track,background),3.0)
        self.assertGreaterEqual(contrast(track,(255,195,107)),3.0)

    def test_matched_card_palette(self):
        source = (SOURCE / "MemoryActivity.java").read_text(encoding="utf-8")
        colors = re.search(r"int\[\] INKS=\{(.*?)\};", source, re.S).group(1)
        inks = [tuple(bytes.fromhex(h)) for h in re.findall(r"0xff([0-9a-f]{6})", colors)]
        self.assertEqual(len(inks), 8)
        self.assertIn("Color.rgb(27,82,70)", source)
        for background in ((27,82,70), (39,53,80), (31,42,68)):
            for ink in inks:
                self.assertGreaterEqual(contrast(background, ink), 4.5, (background, ink))

if __name__ == "__main__":
    unittest.main()
