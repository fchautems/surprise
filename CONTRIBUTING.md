# Contributing

The Quiet Atlas is deliberately small: no framework, no build step, no account, no tracking. Contributions should preserve that calm surface.

Good contributions include new translated text sets, accessibility improvements, carefully tested procedural content, and fixes that work in current desktop and mobile browsers.

## Before opening a pull request

1. Create a focused branch.
2. Run `npm test`.
3. Open several atlas seeds and check both English and French.
4. Confirm that a saved journey still loads after refreshing.

Please keep world generation deterministic. A given seed must always produce the same geography and names, regardless of interface language.

