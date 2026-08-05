import { countEchoes, generateWorld, routeDistance, weatherForDay, createInitialState } from "./world.js";
import { AtlasAmbience } from "./audio.js";

const elements = {
  canvas: document.querySelector("#map"),
  day: document.querySelector("#day"),
  weather: document.querySelector("#weather"),
  found: document.querySelector("#found"),
  total: document.querySelector("#total"),
  echoes: document.querySelector("#echoes"),
  echoTarget: document.querySelector("#echo-target"),
  coordinates: document.querySelector("#coordinates"),
  name: document.querySelector("#place-name"),
  kind: document.querySelector("#place-kind"),
  observation: document.querySelector("#observation"),
  fragment: document.querySelector("#fragment"),
  log: document.querySelector("#log"),
  routes: document.querySelector("#routes"),
  distance: document.querySelector("#distance"),
  seedLabel: document.querySelector("#seed-label"),
  mapHint: document.querySelector("#map-hint"),
  shareStatus: document.querySelector("#share-status"),
  dialog: document.querySelector("#new-dialog"),
  seedInput: document.querySelector("#seed-input"),
  endingDialog: document.querySelector("#ending-dialog"),
  endingCopy: document.querySelector("#ending-copy")
};

const context = elements.canvas.getContext("2d");
const colours = { sea: "#09171e", line: "#36555b", known: "#d8b779", reachable: "#82b8b5", unknown: "#263d44", ink: "#dfe4dc" };
let scale = 1;
let world;
let state;
let hover = null;
let animationFrame;
let installPrompt = null;
const ambience = new AtlasAmbience();

function defaultSeed() {
  const querySeed = new URLSearchParams(location.search).get("atlas");
  if (querySeed) return querySeed;
  return ["the", "quiet", "atlas", new Date().toISOString().slice(0, 10)].join("-");
}

function storageKey(seed) { return `quiet-atlas:${seed}`; }

function loadState(nextWorld) {
  try {
    const saved = JSON.parse(localStorage.getItem(storageKey(nextWorld.seed)));
    if (saved && saved.current < nextWorld.islands.length && Array.isArray(saved.visited)) return saved;
  } catch { /* A damaged field note simply starts a fresh journey. */ }
  return createInitialState(nextWorld);
}

function save() { localStorage.setItem(storageKey(world.seed), JSON.stringify(state)); }

function openWorld(seed) {
  world = generateWorld(seed);
  state = loadState(world);
  const url = new URL(location.href);
  url.searchParams.set("atlas", world.seed);
  history.replaceState(null, "", url);
  elements.seedLabel.textContent = `Chart ${world.hash.toUpperCase()}`;
  elements.total.textContent = world.islands.length;
  elements.echoTarget.textContent = world.echoTarget;
  updateJournal();
  resize();
}

function canvasPoint(island) {
  const width = elements.canvas.width / scale;
  const height = elements.canvas.height / scale;
  return { x: island.x * width, y: island.y * height };
}

function isKnown(index) { return state.visited.includes(index); }
function isReachable(index) { return world.islands[state.current].neighbours.includes(index); }

function draw(time = 0) {
  const width = elements.canvas.width / scale;
  const height = elements.canvas.height / scale;
  context.setTransform(scale, 0, 0, scale, 0, 0);
  context.clearRect(0, 0, width, height);
  context.fillStyle = colours.sea;
  context.fillRect(0, 0, width, height);

  const wash = context.createRadialGradient(width * .55, height * .45, 10, width * .5, height * .5, Math.max(width, height) * .7);
  wash.addColorStop(0, "rgba(35,71,76,.24)");
  wash.addColorStop(1, "rgba(2,8,11,.1)");
  context.fillStyle = wash;
  context.fillRect(0, 0, width, height);

  context.setLineDash([2, 7]);
  context.lineWidth = 1;
  world.islands.forEach((island, index) => {
    if (!isKnown(index) && index !== state.current) return;
    island.neighbours.forEach((neighbour) => {
      if (neighbour < index && !isKnown(neighbour)) return;
      const from = canvasPoint(island);
      const to = canvasPoint(world.islands[neighbour]);
      context.strokeStyle = isKnown(neighbour) ? "rgba(216,183,121,.18)" : "rgba(130,184,181,.13)";
      context.beginPath();
      context.moveTo(from.x, from.y);
      context.lineTo(to.x, to.y);
      context.stroke();
    });
  });
  context.setLineDash([]);

  world.islands.forEach((island, index) => {
    const known = isKnown(index);
    const reachable = isReachable(index);
    if (!known && !reachable) return;
    const point = canvasPoint(island);
    const pulse = index === state.current ? Math.sin(time / 700) * 2 : 0;
    const radius = island.radius + pulse + (hover === index ? 2.5 : 0);
    context.fillStyle = known ? colours.known : colours.reachable;
    context.globalAlpha = known ? .95 : .48;
    context.beginPath();
    context.arc(point.x, point.y, radius, 0, Math.PI * 2);
    context.fill();
    context.globalAlpha = 1;

    if (index === state.current) {
      context.strokeStyle = colours.known;
      context.globalAlpha = .36;
      context.beginPath();
      context.arc(point.x, point.y, radius + 9, 0, Math.PI * 2);
      context.stroke();
      context.globalAlpha = 1;
    }

    if (known || hover === index) {
      context.fillStyle = known ? colours.ink : colours.reachable;
      context.font = `${known ? 11 : 10}px DM Mono, monospace`;
      context.fillText(known ? island.name : "uncharted", point.x + radius + 7, point.y + 4);
    }
  });

  animationFrame = requestAnimationFrame(draw);
}

function formatCoordinates(island) {
  const latitude = ((.5 - island.y) * 120).toFixed(2);
  const longitude = ((island.x - .5) * 220).toFixed(2);
  return `${Math.abs(latitude)}° ${latitude >= 0 ? "N" : "S"} · ${Math.abs(longitude)}° ${longitude >= 0 ? "E" : "W"}`;
}

function updateJournal() {
  const island = world.islands[state.current];
  elements.day.textContent = state.day;
  elements.weather.textContent = weatherForDay(world, state.day);
  elements.found.textContent = state.visited.length;
  elements.echoes.textContent = countEchoes(world, state);
  elements.coordinates.textContent = formatCoordinates(island);
  elements.name.textContent = island.name;
  elements.kind.textContent = island.kind;
  elements.observation.textContent = island.description;
  elements.fragment.textContent = island.fragment ? `“${island.fragment}”` : "";
  elements.distance.textContent = `${state.distance} nautical miles`;
  elements.routes.replaceChildren(...island.neighbours
    .slice()
    .sort((first, second) => routeDistance(world, state.current, first) - routeDistance(world, state.current, second))
    .map((islandId) => {
      const destination = world.islands[islandId];
      const button = document.createElement("button");
      button.type = "button";
      button.className = "route-button";
      const title = document.createElement("b");
      title.textContent = isKnown(islandId) ? destination.name : "Uncharted shore";
      const note = document.createElement("span");
      note.textContent = `${routeDistance(world, state.current, islandId)} nautical miles`;
      button.append(title, note);
      button.addEventListener("mouseenter", () => { hover = islandId; });
      button.addEventListener("mouseleave", () => { hover = null; });
      button.addEventListener("click", () => travel(islandId));
      return button;
    }));
  elements.log.replaceChildren(...state.log.slice().reverse().map((entry) => {
    const item = document.createElement("li");
    const place = world.islands[entry.island];
    item.innerHTML = `Day ${entry.day} · <b>${place.name}</b>${entry.distance ? ` · ${entry.distance} nm` : " · first landing"}`;
    return item;
  }));
}

function travel(index) {
  if (!isReachable(index) || index === state.current) return;
  const travelled = routeDistance(world, state.current, index);
  state.current = index;
  state.day += Math.max(1, Math.round(travelled / 80));
  state.distance += travelled;
  if (!isKnown(index)) state.visited.push(index);
  state.log.push({ day: state.day, island: index, distance: travelled });
  save();
  updateJournal();
  elements.mapHint.textContent = `Arrived at ${world.islands[index].name}`;
  setTimeout(() => { elements.mapHint.textContent = "Choose a visible island to travel"; }, 1800);
  if (!state.endingSeen && countEchoes(world, state) >= world.echoTarget) {
    state.endingSeen = true;
    save();
    elements.endingCopy.textContent = `Across ${state.distance} nautical miles, five voices have repeated the same impossible idea: this atlas was waiting for you specifically.`;
    setTimeout(() => elements.endingDialog.showModal(), 650);
  }
}

function findIsland(event) {
  const rect = elements.canvas.getBoundingClientRect();
  const x = event.clientX - rect.left;
  const y = event.clientY - rect.top;
  let closest = null;
  let closestDistance = 18;
  world.islands.forEach((island, index) => {
    if (!isKnown(index) && !isReachable(index)) return;
    const point = { x: island.x * rect.width, y: island.y * rect.height };
    const distance = Math.hypot(point.x - x, point.y - y);
    if (distance < closestDistance) { closest = index; closestDistance = distance; }
  });
  return closest;
}

function resize() {
  const rect = elements.canvas.getBoundingClientRect();
  scale = Math.min(window.devicePixelRatio || 1, 2);
  elements.canvas.width = Math.round(rect.width * scale);
  elements.canvas.height = Math.round(rect.height * scale);
}

elements.canvas.addEventListener("pointermove", (event) => {
  hover = findIsland(event);
  elements.canvas.style.cursor = hover !== null && isReachable(hover) ? "pointer" : "crosshair";
  if (hover !== null && isReachable(hover)) {
    const destination = world.islands[hover];
    const name = isKnown(hover) ? destination.name : "Uncharted shore";
    elements.mapHint.textContent = `${name} · ${routeDistance(world, state.current, hover)} nautical miles`;
  } else {
    elements.mapHint.textContent = "Choose a visible island to travel";
  }
});
elements.canvas.addEventListener("pointerleave", () => { hover = null; elements.mapHint.textContent = "Choose a visible island to travel"; });
elements.canvas.addEventListener("click", (event) => { const index = findIsland(event); if (index !== null) travel(index); });
window.addEventListener("resize", resize);

document.querySelector("#new-atlas").addEventListener("click", () => {
  elements.seedInput.value = "";
  elements.dialog.showModal();
  setTimeout(() => elements.seedInput.focus(), 50);
});
document.querySelector("#random-seed").addEventListener("click", () => {
  const words = ["lantern", "moss", "afterglow", "winter", "echo", "north", "paper", "tide", "moth", "copper"];
  elements.seedInput.value = `${words[Math.floor(Math.random() * words.length)]}-${Math.random().toString(36).slice(2, 7)}`;
});
elements.dialog.addEventListener("close", () => {
  if (elements.dialog.returnValue === "create") openWorld(elements.seedInput.value.trim() || defaultSeed());
});
document.querySelector("#share").addEventListener("click", async () => {
  try {
    await navigator.clipboard.writeText(location.href);
    elements.shareStatus.textContent = "Link copied";
  } catch {
    elements.shareStatus.textContent = "Use the address in your browser";
  }
  setTimeout(() => { elements.shareStatus.textContent = ""; }, 2200);
});
document.querySelector("#sound-toggle").addEventListener("click", async (event) => {
  const button = event.currentTarget;
  try {
    const active = await ambience.toggle();
    button.setAttribute("aria-pressed", String(active));
    button.querySelector(".action-label").textContent = active ? "Sound on" : "Sound off";
  } catch {
    button.querySelector(".action-label").textContent = "Sound unavailable";
    button.disabled = true;
  }
});

const installButton = document.querySelector("#install-app");
window.addEventListener("beforeinstallprompt", (event) => {
  event.preventDefault();
  installPrompt = event;
  installButton.hidden = false;
});
installButton.addEventListener("click", async () => {
  if (!installPrompt) return;
  await installPrompt.prompt();
  await installPrompt.userChoice;
  installPrompt = null;
  installButton.hidden = true;
});
window.addEventListener("appinstalled", () => { installButton.hidden = true; });
document.querySelector("#ending-share").addEventListener("click", async () => {
  try {
    await navigator.clipboard.writeText(location.href);
    document.querySelector("#ending-share").textContent = "Atlas link copied";
  } catch {
    document.querySelector("#ending-share").textContent = "Copy the browser address";
  }
});

openWorld(defaultSeed());
cancelAnimationFrame(animationFrame);
draw();

if (!localStorage.getItem("quiet-atlas:introduction-seen")) {
  const introduction = document.querySelector("#intro-dialog");
  introduction.showModal();
  introduction.addEventListener("close", () => localStorage.setItem("quiet-atlas:introduction-seen", "true"), { once: true });
}

if ("serviceWorker" in navigator && location.protocol !== "file:") {
  window.addEventListener("load", () => navigator.serviceWorker.register("service-worker.js"));
}
