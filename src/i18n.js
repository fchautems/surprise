export const translations = {
  en: {
    fieldNotes: "Field notes from elsewhere", newAtlas: "New atlas", soundOff: "Sound off", soundOn: "Sound on",
    soundUnavailable: "Sound unavailable", install: "Install", mapHint: "Choose a visible island to travel",
    day: "Day", weather: "Weather", found: "Found", echoes: "Echoes", possibleCrossings: "Possible crossings",
    selectShore: "select a shore", expeditionLog: "Expedition log", copyLink: "Copy this world's link",
    openChart: "Open another chart", everyName: "Every name hides a world.", seedHelp: "Enter any word, date, or phrase. The same phrase will always lead back to the same archipelago.",
    atlasName: "Atlas name", seedPlaceholder: "e.g. the blue hour", surpriseMe: "Surprise me", openAtlas: "Open atlas",
    introEyebrow: "An atlas with no author", introTitle: "A coast has appeared.", introCopy: "No one has charted what lies beyond it. Travel between the visible shores, record what you find, and listen for echoes left by an earlier expedition.",
    knownIsland: "Known island", knownHelp: "Its name remains on the chart.", unknownShore: "Uncharted shore", unknownHelp: "Near enough to reach from your position.", begin: "Begin the expedition",
    endingEyebrow: "A pattern in the static", endingTitle: "The echoes make a sentence.", endingShare: "Copy the atlas link", keepExploring: "Keep exploring",
    chart: "Chart {hash}", unknown: "Uncharted shore", miles: "{distance} nautical miles", firstLanding: "first landing",
    logEntry: "Day {day} · {name} · {journey}", arrived: "Arrived at {name}", linkCopied: "Link copied", browserAddress: "Use the address in your browser",
    atlasLinkCopied: "Atlas link copied", copyBrowserAddress: "Copy the browser address",
    endingCopy: "Across {distance} nautical miles, five voices have repeated the same impossible idea: this atlas was waiting for you specifically.",
    north: "N", south: "S", east: "E", west: "W", languageLabel: "FR"
  },
  fr: {
    fieldNotes: "Notes de terrain venues d’ailleurs", newAtlas: "Nouvel atlas", soundOff: "Son coupé", soundOn: "Son actif",
    soundUnavailable: "Son indisponible", install: "Installer", mapHint: "Choisis une île visible pour voyager",
    day: "Jour", weather: "Météo", found: "Trouvées", echoes: "Échos", possibleCrossings: "Traversées possibles",
    selectShore: "choisir un rivage", expeditionLog: "Journal d’expédition", copyLink: "Copier le lien de ce monde",
    openChart: "Ouvrir une autre carte", everyName: "Chaque nom cache un monde.", seedHelp: "Entre un mot, une date ou une phrase. La même expression ramènera toujours au même archipel.",
    atlasName: "Nom de l’atlas", seedPlaceholder: "p. ex. l’heure bleue", surpriseMe: "Surprends-moi", openAtlas: "Ouvrir l’atlas",
    introEyebrow: "Un atlas sans auteur", introTitle: "Une côte est apparue.", introCopy: "Personne n’a cartographié ce qui se trouve au-delà. Voyage entre les rivages visibles, consigne tes découvertes et écoute les échos laissés par une expédition antérieure.",
    knownIsland: "Île connue", knownHelp: "Son nom reste inscrit sur la carte.", unknownShore: "Rivage inconnu", unknownHelp: "Assez proche pour être atteint depuis ta position.", begin: "Commencer l’expédition",
    endingEyebrow: "Un motif dans le silence", endingTitle: "Les échos forment une phrase.", endingShare: "Copier le lien de l’atlas", keepExploring: "Continuer l’exploration",
    chart: "Carte {hash}", unknown: "Rivage inconnu", miles: "{distance} milles nautiques", firstLanding: "premier débarquement",
    logEntry: "Jour {day} · {name} · {journey}", arrived: "Arrivée à {name}", linkCopied: "Lien copié", browserAddress: "Utilise l’adresse du navigateur",
    atlasLinkCopied: "Lien de l’atlas copié", copyBrowserAddress: "Copie l’adresse du navigateur",
    endingCopy: "Après {distance} milles nautiques, cinq voix ont répété la même idée impossible : cet atlas t’attendait précisément.",
    north: "N", south: "S", east: "E", west: "O", languageLabel: "EN"
  }
};

export function preferredLocale(search = "", browserLanguages = []) {
  const requested = new URLSearchParams(search).get("lang");
  if (requested && translations[requested]) return requested;
  return browserLanguages.some((language) => language.toLowerCase().startsWith("fr")) ? "fr" : "en";
}

export function translate(locale, key, values = {}) {
  const template = translations[locale]?.[key] ?? translations.en[key] ?? key;
  return Object.entries(values).reduce((text, [name, value]) => text.replaceAll(`{${name}}`, String(value)), template);
}

export function applyTranslations(locale, root = document) {
  root.documentElement.lang = locale;
  root.querySelectorAll("[data-i18n]").forEach((element) => {
    element.textContent = translate(locale, element.dataset.i18n);
  });
  root.querySelectorAll("[data-i18n-placeholder]").forEach((element) => {
    element.placeholder = translate(locale, element.dataset.i18nPlaceholder);
  });
}
