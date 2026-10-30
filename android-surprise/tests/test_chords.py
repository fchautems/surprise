#!/usr/bin/env python3
"""Validate the pitches and structure of the playable chord palette."""
import math
import re
import unittest
from pathlib import Path

SOURCE = Path(__file__).resolve().parents[1] / "app/src/main/java/ch/fred/surprise/MusicActivity.java"

class ChordContentTest(unittest.TestCase):
    def test_extra_notes_are_in_tune(self):
        source = SOURCE.read_text(encoding="utf-8")
        names = re.findall(r'"([^"]+)"', re.search(r"EXTRA_NAMES\s*=\s*\{([^}]+)\}", source).group(1))
        values = [float(x) for x in re.search(r"EXTRA_FREQ\s*=\s*\{([^}]+)\}", source).group(1).split(",")]
        self.assertEqual(names, ["Fa", "Si"])
        self.assertEqual(len(values), len(names))
        for name, frequency, midi in zip(names, values, (65, 71)):
            with self.subTest(note=name):
                self.assertAlmostEqual(69 + 12 * math.log2(frequency / 440), midi, delta=0.01)

    def test_intervals_and_registers(self):
        source = SOURCE.read_text(encoding="utf-8")
        name_block = re.search(r"CHORD_NAMES\s*=\s*\{([^}]+)\}", source).group(1)
        names = re.findall(r'"([^"]+)"', name_block)
        frequency_block = re.search(r"double\[\]\[\]\s+CHORDS\s*=\s*\{(.*?)\n    \};", source, re.S).group(1)
        chords = [[float(x) for x in row.split(",")] for row in re.findall(r"\{([^}]+)\}", frequency_block)]
        self.assertEqual(len(names), len(chords))
        self.assertEqual(len(set(names)), len(names))
        self.assertGreaterEqual(len(names), 6)
        for name, frequencies in zip(names, chords):
            with self.subTest(name=name):
                self.assertEqual(len(frequencies), 4)
                self.assertEqual(frequencies, sorted(frequencies))
                roots = {"Do": 60, "Fa": 65, "Sol": 67, "La": 57, "Ré": 62, "Mi": 64}
                midi = 69 + 12 * math.log2(frequencies[0] / 440)
                self.assertAlmostEqual(midi, roots[name.split()[0]], delta=0.01)
                self.assertGreater(frequencies[0], 100)
                self.assertLess(frequencies[-1], 1000)
                semitones = [round(12 * math.log2(f / frequencies[0])) for f in frequencies]
                expected = [0, 3, 7, 10] if name.endswith("m7") else [0, 4, 7, 10 if name.endswith(" 7") else 11]
                self.assertEqual(semitones, expected)
                for interval, freq in zip(semitones, frequencies):
                    self.assertLess(abs(12 * math.log2(freq / frequencies[0]) - interval), 0.01)

if __name__ == "__main__":
    unittest.main()
