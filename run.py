"""Launch The Quiet Atlas with Python's standard library."""

from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from threading import Timer
import os
import webbrowser


PORT = 4173
ROOT = Path(__file__).resolve().parent


class QuietHandler(SimpleHTTPRequestHandler):
    extensions_map = {
        **SimpleHTTPRequestHandler.extensions_map,
        ".webmanifest": "application/manifest+json",
        ".js": "text/javascript",
        ".svg": "image/svg+xml",
    }


def main() -> None:
    os.chdir(ROOT)
    address = f"http://localhost:{PORT}"
    server = ThreadingHTTPServer(("127.0.0.1", PORT), QuietHandler)
    print(f"The Quiet Atlas is waiting at {address}")
    print("Press Ctrl+C to close it.")
    Timer(0.4, lambda: webbrowser.open(address)).start()
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nThe atlas is closed.")
    finally:
        server.server_close()


if __name__ == "__main__":
    main()

