"""Validate the embedded question bank without Android dependencies."""
import hashlib
import json
import pathlib
import re
import unittest

SOURCE = pathlib.Path(__file__).resolve().parents[1] / "app/src/main/java/ch/fred/surprise/MainActivity.java"
ENTRY = re.compile(r'\{"([^"]+)","([^"]+)","([^"]+)","([^"]+)","([^"]+)","([^"]+)"\}')

class BankTests(unittest.TestCase):
    def test_existing_question_ids_remain_stable(self):
        # Saved rounds and recent-question history store numeric positions.
        # If legacy content must change deliberately, review this fingerprint.
        rows = ENTRY.findall(SOURCE.read_text(encoding="utf-8"))
        prefix = json.dumps(rows[:116], ensure_ascii=False, separators=(",", ":")).encode()
        self.assertEqual(hashlib.sha256(prefix).hexdigest(),
                         "5752d8935db6ee9b3c37ffd2086e1cfb30a8932e3b65cbd51d2a14ad05a34cc4")
        # Protect the entire previously shipped bank, not just its first 116 entries.
        full_legacy = json.dumps(rows[:137], ensure_ascii=False, separators=(",", ":")).encode()
        self.assertEqual(hashlib.sha256(full_legacy).hexdigest(),
                         "5ee985d84b4d748152732e65a0baddaf434f863e4dd1d54e2f0242cfa8343cfc")

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
