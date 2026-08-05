import { createRandom, hashString } from "./random.js";

const starts = ["Ae", "Ar", "Bel", "Cael", "Cor", "Eli", "Far", "Ily", "Is", "Lo", "Mor", "Na", "Or", "Pel", "Ra", "Sel", "Tal", "Ul", "Va", "Ys"];
const middles = ["da", "en", "fi", "il", "ka", "lo", "me", "na", "ra", "ri", "sa", "se", "the", "va", "wen"];
const ends = ["a", "ai", "en", "eth", "ia", "in", "is", "oa", "on", "or", "os", "une", "yr"];

const forms = [
  ["basalt island", "black shelves descend into clear water"],
  ["salt meadow", "silver grass moves although the air is still"],
  ["cloud forest", "the upper paths disappear into a permanent veil"],
  ["red limestone cay", "rain has written narrow canyons through the stone"],
  ["tide garden", "pools hold constellations of pale anemones"],
  ["wind-carved island", "every ridge points toward a forgotten storm"],
  ["amber marsh", "warm reeds hum at the edge of hearing"],
  ["glass beach", "the shore rings softly beneath each returning wave"],
  ["cedar island", "the oldest trees lean together like conspirators"],
  ["blue volcanic isle", "mineral springs trace bright lines toward the sea"]
];

const details = [
  "A stairway ends several steps above the earth.",
  "Someone has tied small bells to the lowest branches.",
  "The sand preserves tracks from an animal you never see.",
  "At noon, every shadow points toward the harbour.",
  "A dry fountain carries the scent of rain.",
  "Three empty chairs face the open water.",
  "The local map marks a road that does not exist.",
  "Tiny blue flowers grow only inside old footprints.",
  "A stone arch frames a star even in daylight.",
  "The waves arrive in groups of seven.",
  "A warm current runs beneath the northern cliffs.",
  "All the windows here have been left slightly open.",
  "A line of white feathers leads inland, then stops.",
  "The rain tastes faintly of mint.",
  "Every path eventually returns to the same fig tree."
];

const fragments = [
  "We named the horizon so it would know where to wait.",
  "The shortest route was the one that changed us.",
  "Nothing vanished. It only became difficult to point at.",
  "Leave one light burning for the person you will be tomorrow.",
  "The sea remembers shapes, never names.",
  "We arrived too early and called it an empty place.",
  "A map is a promise made by someone who had to leave.",
  "There are distances measured only by returning.",
  "Listen long enough and every silence develops a weather.",
  "What we could not carry, we taught the wind."
];

const weather = ["Still air", "Veiled sun", "Fine rain", "North wind", "Sea mist", "High clouds", "Distant thunder", "Clear light"];

function makeName(random) {
  const middleCount = random() > 0.68 ? 2 : 1;
  let name = random.pick(starts);
  for (let index = 0; index < middleCount; index += 1) name += random.pick(middles);
  return name + random.pick(ends);
}

function distance(first, second) {
  return Math.hypot(first.x - second.x, first.y - second.y);
}

function makeSites(random, count) {
  const sites = [];
  let attempts = 0;
  while (sites.length < count && attempts < 5000) {
    attempts += 1;
    const candidate = { x: random.between(0.07, 0.93), y: random.between(0.08, 0.92) };
    const separation = sites.every((site) => distance(site, candidate) > 0.105);
    if (separation) sites.push(candidate);
  }
  return sites;
}

function connectIslands(islands) {
  const links = islands.map(() => new Set());
  const connected = new Set([0]);

  // A minimum-distance spanning tree guarantees that no generated map contains
  // a beautiful but permanently unreachable pocket of islands.
  while (connected.size < islands.length) {
    let shortest = null;
    for (const from of connected) {
      islands.forEach((island, to) => {
        if (connected.has(to)) return;
        const length = distance(islands[from], island);
        if (!shortest || length < shortest.length) shortest = { from, to, length };
      });
    }
    links[shortest.from].add(shortest.to);
    links[shortest.to].add(shortest.from);
    connected.add(shortest.to);
  }

  // Add local alternatives so exploration involves choices rather than a tree.
  islands.forEach((source, from) => {
    const nearest = islands
      .map((island, to) => ({ to, length: distance(source, island) }))
      .filter(({ to }) => to !== from && !links[from].has(to))
      .sort((first, second) => first.length - second.length);
    while (links[from].size < 3 && nearest.length) {
      const { to } = nearest.shift();
      links[from].add(to);
      links[to].add(from);
    }
  });

  return links.map((neighbours) => [...neighbours].sort((first, second) => first - second));
}

export function generateWorld(seedPhrase, count = 28) {
  const seed = String(seedPhrase || "quiet atlas").trim().toLowerCase();
  const random = createRandom(seed);
  const sites = makeSites(random, count);
  const usedNames = new Set();
  const islands = sites.map((site, index) => {
    let name = makeName(random);
    while (usedNames.has(name)) name = makeName(random);
    usedNames.add(name);
    const [kind, description] = random.pick(forms);
    return {
      id: index,
      ...site,
      radius: random.between(4.5, 9),
      name,
      kind,
      description: `${description}. ${random.pick(details)}`,
      fragment: "",
      phase: random.between(0, Math.PI * 2)
    };
  });
  const connections = connectIslands(islands);
  islands.forEach((island, index) => { island.neighbours = connections[index]; });

  const start = islands
    .map((island, index) => ({ index, score: Math.hypot(island.x - .5, island.y - .5) }))
    .sort((first, second) => first.score - second.score)[0].index;

  const fragmentTotal = Math.min(8, islands.length - 1);
  const fragmentSites = random.shuffle(islands.map(({ id }) => id).filter((id) => id !== start)).slice(0, fragmentTotal);
  fragmentSites.forEach((islandId, index) => {
    islands[islandId].fragment = fragments[index];
  });

  return { seed, hash: hashString(seed).toString(36), islands, start, weather, fragmentTotal, echoTarget: Math.min(5, fragmentTotal) };
}

export function routeDistance(world, from, to) {
  return Math.round(distance(world.islands[from], world.islands[to]) * 730);
}

export function weatherForDay(world, day) {
  const random = createRandom(`${world.seed}:weather:${day}`);
  return random.pick(world.weather);
}

export function createInitialState(world) {
  return {
    current: world.start,
    visited: [world.start],
    day: 1,
    distance: 0,
    log: [{ day: 1, island: world.start, distance: 0 }],
    endingSeen: false
  };
}

export function countEchoes(world, state) {
  return state.visited.reduce((total, islandId) => total + (world.islands[islandId].fragment ? 1 : 0), 0);
}

export function reachableIslands(world, start = world.start) {
  const found = new Set([start]);
  const queue = [start];
  while (queue.length) {
    const current = queue.shift();
    world.islands[current].neighbours.forEach((next) => {
      if (!found.has(next)) { found.add(next); queue.push(next); }
    });
  }
  return found;
}
