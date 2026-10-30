#!/usr/bin/env bash
# Bound an occasionally stuck ADB install; retry once after resetting the server.
set -eu
if [ "$#" -ne 1 ]; then
    echo "Usage: install_apk.sh APK" >&2
    exit 2
fi
if timeout 90s adb install "$1"; then
    exit 0
fi
echo "ADB install failed or timed out; restarting the server and retrying once." >&2
timeout 10s adb kill-server || true
timeout 30s adb start-server
timeout 90s adb wait-for-device
timeout 90s adb install "$1"
