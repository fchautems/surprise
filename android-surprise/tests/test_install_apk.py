#!/usr/bin/env python3
"""Exercise installation recovery without requiring an Android SDK."""
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

SCRIPT = Path(__file__).with_name("install_apk.sh")

class InstallTest(unittest.TestCase):
    def run_install(self, mode):
        with tempfile.TemporaryDirectory() as d:
            root = Path(d)
            fake = root / "adb"
            fake.write_text("""#!/bin/sh
echo "$*" >> "$CALLS"
case "$1" in
  install)
    count=$(grep -c '^install ' "$CALLS")
    [ "$MODE" = ok ] && exit 0
    [ "$MODE" = retry ] && [ "$count" -gt 1 ] && exit 0
    exit 1 ;;
  *) exit 0 ;;
esac
""")
            fake.chmod(0o755)
            env = dict(os.environ, PATH=str(root) + os.pathsep + os.environ["PATH"],
                       CALLS=str(root / "calls"), MODE=mode)
            result = subprocess.run(["bash", str(SCRIPT), "test app.apk"], env=env,
                                    capture_output=True, text=True, timeout=10)
            return result, (root / "calls").read_text().splitlines()

    def test_success_does_not_retry(self):
        result, calls = self.run_install("ok")
        self.assertEqual(result.returncode, 0)
        self.assertEqual(calls, ["install test app.apk"])

    def test_failed_install_restarts_and_retries(self):
        result, calls = self.run_install("retry")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(calls, ["install test app.apk", "kill-server", "start-server",
                                 "wait-for-device", "install test app.apk"])

    def test_repeated_failure_is_reported(self):
        result, calls = self.run_install("fail")
        self.assertNotEqual(result.returncode, 0)
        self.assertEqual(calls.count("install test app.apk"), 2)
        self.assertIn("retrying once", result.stderr)

if __name__ == "__main__":
    unittest.main()
