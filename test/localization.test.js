import test from "node:test";
import assert from "node:assert/strict";
import { worldContent } from "../src/content.js";
import { preferredLocale, translate, translations } from "../src/i18n.js";
import { generateWorld } from "../src/world.js";

test("interface translations expose the same keys", () => {
  assert.deepEqual(Object.keys(translations.fr).sort(), Object.keys(translations.en).sort());
});

test("world translations preserve procedural choice counts", () => {
  for (const key of ["forms", "details", "fragments", "weather"]) {
    assert.equal(worldContent.fr[key].length, worldContent.en[key].length, `${key} should have translation parity`);
  }
});

test("language changes words but never geography", () => {
  const english = generateWorld("shared horizon", 28, "en");
  const french = generateWorld("shared horizon", 28, "fr");
  assert.deepEqual(
    english.islands.map(({ name, x, y, neighbours }) => ({ name, x, y, neighbours })),
    french.islands.map(({ name, x, y, neighbours }) => ({ name, x, y, neighbours }))
  );
  assert.notEqual(english.islands[0].description, french.islands[0].description);
});

test("locale selection honours explicit links before browser settings", () => {
  assert.equal(preferredLocale("?lang=en", ["fr-CH"]), "en");
  assert.equal(preferredLocale("", ["fr-CH", "en"]), "fr");
  assert.equal(translate("fr", "miles", { distance: 12 }), "12 milles nautiques");
});
