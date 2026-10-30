#!/usr/bin/env python3
"""Guard the matching deck against ambiguous or missing symbols."""
import re
import unittest
from pathlib import Path

SOURCE = Path(__file__).resolve().parents[1] / "app/src/main/java/ch/fred/surprise/MemoryActivity.java"

class DeckContentTest(unittest.TestCase):
    def test_sets_are_complete_and_unambiguous(self):
        source = SOURCE.read_text(encoding="utf-8")
        maximum = int(re.search(r"MAX_PAIRS\s*=\s*(\d+)", source).group(1))
        sizes = []
        for name in ("SYMBOLS", "ESCAPADE", "NATURE"):
            with self.subTest(pack=name):
                match = re.search(r"String\[\]\s+" + name + r"\s*=\s*\{([^}]+)\}", source)
                self.assertIsNotNone(match, name)
                symbols = re.findall(r'"([^"]+)"', match.group(1))
                self.assertGreater(len(symbols), maximum, "replay requires an unused symbol")
                self.assertEqual(len(symbols), len(set(symbols)), "duplicates make pairs ambiguous")
                sizes.append(len(symbols))
        self.assertEqual(len(set(sizes)), 1, "all sets must support the same deck indices")

if __name__ == "__main__":
    unittest.main()
