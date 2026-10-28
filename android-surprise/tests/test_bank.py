"""Validate the embedded question bank without Android dependencies."""
import pathlib
import re
import unittest

SOURCE = pathlib.Path(__file__).resolve().parents[1] / "app/src/main/java/ch/fred/surprise/MainActivity.java"
ENTRY = re.compile(r'\{"([^"]+)","([^"]+)","([^"]+)","([^"]+)","([^"]+)","([^"]+)"\}')

class BankTests(unittest.TestCase):
    def test_bank_is_replayable_and_unambiguous(self):
        source = SOURCE.read_text(encoding="utf-8")
        rows = ENTRY.findall(source)
        round_size = int(re.search(r'ROUND=(\d+)', source).group(1))
        self.assertGreaterEqual(len(rows), round_size * 3)
        from collections import Counter
        counts = Counter(row[0] for row in rows)
        self.assertEqual(set(counts), {"MUSIQUE", "CUISINE", "VOYAGE", "CIEL", "ALLEMAND", "TEMPS", "CRYPTO"})
        for name, count in counts.items():
            self.assertGreaterEqual(count, round_size * 2, name)
        self.assertEqual(len({row[1] for row in rows}), len(rows))
        for category, question, *answers in rows:
            self.assertTrue(category.strip())
            self.assertTrue(question.strip())
            self.assertTrue(all(a.strip() for a in answers))
            self.assertEqual(len(set(answers)), 4, question)

if __name__ == "__main__":
    unittest.main()
