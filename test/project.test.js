import test from "node:test";
import assert from "node:assert/strict";
import { access, readFile } from "node:fs/promises";

test("the web app manifest describes an installable standalone app", async () => {
  const manifest = JSON.parse(await readFile("manifest.webmanifest", "utf8"));
  assert.equal(manifest.display, "standalone");
  assert.equal(manifest.start_url, "./");
  assert.ok(manifest.icons.length > 0);
  await Promise.all(manifest.icons.map(({ src }) => access(src)));
});

test("every local service-worker shell asset exists", async () => {
  const worker = await readFile("service-worker.js", "utf8");
  const shellMatch = worker.match(/const APP_SHELL = \[([\s\S]*?)\];/);
  assert.ok(shellMatch, "APP_SHELL list should be present");
  const assets = [...shellMatch[1].matchAll(/"\.\/([^\"]*)"/g)].map((match) => match[1] || "index.html");
  assert.ok(assets.length >= 8);
  await Promise.all(assets.map((asset) => access(asset)));
});

test("the document exposes the controls required by the application", async () => {
  const html = await readFile("index.html", "utf8");
  for (const id of ["map", "routes", "new-dialog", "intro-dialog", "ending-dialog", "sound-toggle", "install-app", "language-toggle"]) {
    assert.match(html, new RegExp(`id=["']${id}["']`));
  }
});

test("the application shell has no remote runtime dependencies", async () => {
  const files = await Promise.all(["index.html", "styles.css", "src/app.js"].map((path) => readFile(path, "utf8")));
  files.forEach((content) => {
    assert.doesNotMatch(content, /fonts\.googleapis|unpkg|cdn\.jsdelivr/);
    assert.doesNotMatch(content, /<(script|link)[^>]+https?:\/\//);
  });
});
