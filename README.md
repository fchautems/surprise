# The Quiet Atlas

> A tiny, deterministic world that unfolds as you explore it.

The Quiet Atlas is a contemplative browser game about charting an unknown archipelago. Each word or phrase generates a complete world: islands, routes, weather, observations, and fragments from an earlier expedition. The same seed always recreates the same geography, so an atlas can be shared as a simple link.

It was created from a peculiar brief: an empty public repository, genuine creative freedom for an AI, and no reveal until the work felt complete.

## Try it

Clone the repository, then on Windows double-click `start.bat`.

Or launch it from any system with Python 3:

```bash
python run.py
```

The browser opens automatically at `http://localhost:4173`. No package installation, account, API key, or internet connection is required. Node.js users can alternatively run `npm start`.

## What is inside

- deterministic procedural archipelagos generated from a text seed;
- a fully connected route network with exploration progress and a small narrative arc;
- automatic saving in the browser;
- shareable atlas links;
- optional generative ambient audio made with the Web Audio API;
- English and French interfaces, selected automatically and switchable at any time;
- responsive desktop and mobile layouts;
- installable PWA support and offline caching;
- zero runtime dependencies, analytics, cookies, accounts, or remote APIs.

## Design principles

The project intentionally avoids scores, timers, advertisements, and optimization loops. It is a place to wander for a few minutes. Discovery is the reward.

The visual system is rendered with HTML, CSS, and Canvas. World generation is isolated from the interface in `src/world.js`; the seeded random engine lives in `src/random.js`; narrative content and translations are separate data modules.

## Development

```bash
npm start
npm test
```

The tests cover determinism, connectivity across generated worlds, narrative distribution, localization parity, installability, and the offline application shell. GitHub Actions runs them on every push and pull request.

## Privacy

Everything happens locally. Journey progress is stored only in `localStorage` in the visitor's browser. Sharing copies the current URL; no expedition data is uploaded anywhere.

## License

[MIT](LICENSE) — made to be explored, adapted, and extended.

