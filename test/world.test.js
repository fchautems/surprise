import test from "node:test";
import assert from "node:assert/strict";
import { createRandom, hashString } from "../src/random.js";
import { countEchoes, createInitialState, generateWorld, reachableIslands, routeDistance, weatherForDay } from "../src/world.js";

test("string hashing and random sequences are deterministic", () => {
  assert.equal(hashString("same place"), hashString("same place"));
  const first = createRandom("a small key");
  const second = createRandom("a small key");
  assert.deepEqual([first(), first(), first()], [second(), second(), second()]);
});

test("a seed always produces the same complete world", () => {
  const first = generateWorld("blue hour");
  const second = generateWorld("blue hour");
  assert.deepEqual(first, second);
  assert.equal(first.islands.length, 28);
  assert.equal(new Set(first.islands.map(({ name }) => name)).size, 28);
});

test("island routes are bidirectional and offer useful choices", () => {
  const world = generateWorld("connected");
  world.islands.forEach((island) => {
    assert.ok(island.neighbours.length >= 3);
    island.neighbours.forEach((index) => {
      assert.ok(index >= 0 && index < world.islands.length);
      assert.ok(world.islands[index].neighbours.includes(island.id));
    });
  });
});

test("every island is reachable across many generated worlds", () => {
  for (let index = 0; index < 200; index += 1) {
    const world = generateWorld(`reachability-${index}`);
    assert.equal(reachableIslands(world).size, world.islands.length);
  }
});

test("echoes are distributed away from the first landing", () => {
  const world = generateWorld("voices");
  const fragmentSites = world.islands.filter(({ fragment }) => fragment);
  assert.equal(fragmentSites.length, world.fragmentTotal);
  assert.equal(world.islands[world.start].fragment, "");
  const state = createInitialState(world);
  assert.equal(countEchoes(world, state), 0);
  state.visited.push(fragmentSites[0].id);
  assert.equal(countEchoes(world, state), 1);
});

test("initial expedition state begins on the world's start island", () => {
  const world = generateWorld("departure");
  const state = createInitialState(world);
  assert.equal(state.current, world.start);
  assert.deepEqual(state.visited, [world.start]);
  assert.equal(state.day, 1);
});

test("route distances and weather remain stable", () => {
  const world = generateWorld("weather station");
  const neighbour = world.islands[world.start].neighbours[0];
  assert.ok(routeDistance(world, world.start, neighbour) > 0);
  assert.equal(weatherForDay(world, 42), weatherForDay(world, 42));
});
