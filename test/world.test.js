import test from "node:test";
import assert from "node:assert/strict";
import { createRandom, hashString } from "../src/random.js";
import { createInitialState, generateWorld, routeDistance, weatherForDay } from "../src/world.js";

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

test("each island links to three valid neighbours", () => {
  const world = generateWorld("connected");
  world.islands.forEach((island) => {
    assert.equal(island.neighbours.length, 3);
    island.neighbours.forEach((index) => assert.ok(index >= 0 && index < world.islands.length));
  });
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

