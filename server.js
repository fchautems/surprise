import { createServer } from "node:http";
import { readFile, stat } from "node:fs/promises";
import { extname, join, normalize } from "node:path";

const port = Number(process.env.PORT || 4173);
const root = new URL(".", import.meta.url).pathname;
const types = {
  ".html": "text/html; charset=utf-8",
  ".css": "text/css; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".json": "application/json; charset=utf-8",
  ".svg": "image/svg+xml"
};

createServer(async (request, response) => {
  try {
    const rawPath = decodeURIComponent(new URL(request.url, "http://localhost").pathname);
    const safePath = normalize(rawPath).replace(/^(\.\.(\/|\\|$))+/, "");
    let filePath = join(root, safePath === "/" ? "index.html" : safePath);
    if ((await stat(filePath)).isDirectory()) filePath = join(filePath, "index.html");
    response.writeHead(200, { "content-type": types[extname(filePath)] || "application/octet-stream" });
    response.end(await readFile(filePath));
  } catch {
    response.writeHead(404, { "content-type": "text/plain; charset=utf-8" });
    response.end("Not found");
  }
}).listen(port, () => {
  console.log(`The Quiet Atlas is waiting at http://localhost:${port}`);
});

