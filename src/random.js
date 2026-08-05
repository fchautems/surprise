export function hashString(value) {
  let hash = 2166136261;
  for (let index = 0; index < value.length; index += 1) {
    hash ^= value.charCodeAt(index);
    hash = Math.imul(hash, 16777619);
  }
  return hash >>> 0;
}

export function createRandom(seed) {
  let state = typeof seed === "number" ? seed >>> 0 : hashString(String(seed));
  const random = () => {
    state += 0x6d2b79f5;
    let value = state;
    value = Math.imul(value ^ (value >>> 15), value | 1);
    value ^= value + Math.imul(value ^ (value >>> 7), value | 61);
    return ((value ^ (value >>> 14)) >>> 0) / 4294967296;
  };
  random.between = (minimum, maximum) => minimum + random() * (maximum - minimum);
  random.int = (minimum, maximum) => Math.floor(random.between(minimum, maximum + 1));
  random.pick = (items) => items[Math.floor(random() * items.length)];
  random.shuffle = (items) => {
    const copy = [...items];
    for (let index = copy.length - 1; index > 0; index -= 1) {
      const other = Math.floor(random() * (index + 1));
      [copy[index], copy[other]] = [copy[other], copy[index]];
    }
    return copy;
  };
  return random;
}

