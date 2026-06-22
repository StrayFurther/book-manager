const PROD_API = import.meta.env.VITE_GRAPHQL_URL ?? '/v1/graphql';
const DEV_CANDIDATES = [
      // prefer developer-supplied override
      import.meta.env.VITE_GRAPHQL_URL,
      // prefer vite proxy endpoints (these will be forwarded by vite -> backend)
      '/api/graphql',
      '/api/v1/graphql',
      // common spring-graphql defaults / variations (backend direct - may 404 if servlet path differs)
      '/v1/graphql',
      '/graphql',
      // direct backend (in case proxy not used)
      'http://localhost:8443/v1/graphql',
      'http://localhost:8443/graphql',
    ].filter(Boolean) as string[];

let resolvedAPI: string | null = import.meta.env.DEV ? null : PROD_API;
let resolvingPromise: Promise<string> | null = null;

async function probeEndpoint(endpoint: string, timeoutMs = 2500): Promise<boolean> {
  const controller = new AbortController();
  const id = setTimeout(() => controller.abort(), timeoutMs);
  try {
    const res = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ query: 'query { __typename }' }),
      signal: controller.signal,
    });
    clearTimeout(id);
    if (!res.ok) return false;
    const text = await res.text().catch(() => '');
    if (!text) return false;
    try {
      const json = JSON.parse(text);
      // Accept if we get data or errors (server is GraphQL-capable)
      return json && (json.data !== undefined || Array.isArray(json.errors));
    } catch {
      return false;
    }
  } catch {
    clearTimeout(id);
    return false;
  }
}

async function resolveAPI(): Promise<string> {
  if (resolvedAPI) return resolvedAPI;
  if (resolvingPromise) return resolvingPromise;

  resolvingPromise = (async () => {
    // In dev try candidates, in prod use PROD_API
    const candidates = import.meta.env.DEV ? DEV_CANDIDATES : [PROD_API];
    for (const c of candidates) {
      if (!c) continue;
      // ensure absolute URLs when needed
      const endpoint = c.startsWith('http') ? c : c.startsWith('/') ? c : `/${c}`;
      console.log('[graphql-helper] probing', endpoint);
      // try probe; ignore exceptions
      try {
        const ok = await probeEndpoint(endpoint);
        if (ok) {
          resolvedAPI = endpoint;
          console.log('[graphql-helper] selected API ->', resolvedAPI);
          return resolvedAPI;
        }
      } catch (e) {
        // swallow and try next
      }
    }
    // fallback: use PROD_API or first candidate
    resolvedAPI = PROD_API || DEV_CANDIDATES[0];
    console.warn('[graphql-helper] no probe succeeded, falling back to', resolvedAPI);
    return resolvedAPI;
  })();

  return resolvingPromise;
}

// debug: show initial mode
console.log('[graphql-helper] DEV=', Boolean(import.meta.env.DEV));

export async function graphql<T = any>(query: string, variables?: Record<string, unknown>): Promise<T> {
  const api = await resolveAPI();
  console.log('[graphql-helper] using API ->', api);
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  const res = await fetch(api, {
    method: 'POST',
    headers,
    body: JSON.stringify({ query, variables }),
  });

  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`GraphQL request failed: ${res.status} ${res.statusText} — ${text}`);
  }

  const body = await res.json();
  if (body.errors) throw new Error(body.errors.map((e: any) => e.message).join('; '));
  return body.data;
}