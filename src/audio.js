function makeNoiseBuffer(context, seconds = 7) {
  const frameCount = Math.floor(context.sampleRate * seconds);
  const buffer = context.createBuffer(2, frameCount, context.sampleRate);
  for (let channel = 0; channel < buffer.numberOfChannels; channel += 1) {
    const data = buffer.getChannelData(channel);
    let smooth = 0;
    for (let frame = 0; frame < frameCount; frame += 1) {
      smooth = smooth * 0.985 + (Math.random() * 2 - 1) * 0.015;
      data[frame] = smooth;
    }
  }
  return buffer;
}

export class AtlasAmbience {
  constructor() {
    this.context = null;
    this.master = null;
    this.noise = null;
    this.chimeTimer = null;
    this.active = false;
  }

  async start() {
    if (this.active) return;
    const AudioContext = window.AudioContext || window.webkitAudioContext;
    if (!AudioContext) throw new Error("Web Audio is not supported");
    this.context ||= new AudioContext();
    await this.context.resume();

    this.master = this.context.createGain();
    this.master.gain.setValueAtTime(0, this.context.currentTime);
    this.master.gain.linearRampToValueAtTime(0.16, this.context.currentTime + 1.8);
    this.master.connect(this.context.destination);

    this.noise = this.context.createBufferSource();
    this.noise.buffer = makeNoiseBuffer(this.context);
    this.noise.loop = true;
    const filter = this.context.createBiquadFilter();
    filter.type = "lowpass";
    filter.frequency.value = 420;
    filter.Q.value = 0.45;
    const tide = this.context.createGain();
    tide.gain.value = 0.32;
    const lfo = this.context.createOscillator();
    const lfoGain = this.context.createGain();
    lfo.frequency.value = 0.075;
    lfoGain.gain.value = 0.18;
    lfo.connect(lfoGain).connect(tide.gain);
    this.noise.connect(filter).connect(tide).connect(this.master);
    this.noise.start();
    lfo.start();
    this.lfo = lfo;
    this.active = true;
    this.scheduleChime();
  }

  scheduleChime() {
    if (!this.active) return;
    this.chimeTimer = window.setTimeout(() => {
      this.playChime();
      this.scheduleChime();
    }, 7000 + Math.random() * 12000);
  }

  playChime() {
    if (!this.active || !this.context) return;
    const now = this.context.currentTime;
    const oscillator = this.context.createOscillator();
    const gain = this.context.createGain();
    const notes = [174.61, 220, 261.63, 329.63, 392];
    oscillator.frequency.value = notes[Math.floor(Math.random() * notes.length)];
    oscillator.type = "sine";
    gain.gain.setValueAtTime(0, now);
    gain.gain.linearRampToValueAtTime(0.06, now + 0.08);
    gain.gain.exponentialRampToValueAtTime(0.0001, now + 3.8);
    oscillator.connect(gain).connect(this.master);
    oscillator.start(now);
    oscillator.stop(now + 4);
  }

  stop() {
    if (!this.active) return;
    this.active = false;
    clearTimeout(this.chimeTimer);
    const now = this.context.currentTime;
    this.master.gain.cancelScheduledValues(now);
    this.master.gain.setValueAtTime(this.master.gain.value, now);
    this.master.gain.linearRampToValueAtTime(0, now + 0.7);
    const noise = this.noise;
    const lfo = this.lfo;
    const master = this.master;
    window.setTimeout(() => {
      noise?.stop();
      lfo?.stop();
      master?.disconnect();
    }, 800);
  }

  async toggle() {
    if (this.active) this.stop();
    else await this.start();
    return this.active;
  }
}
