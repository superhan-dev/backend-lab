/**
 * Zipf sampler (approx.)
 * - N: number of items
 * - s: skew parameter (1.05~1.3 정도면 현실적인 핫키 느낌)
 *
 * Returns integer in [1..N]
 */
export function zipfSample(N, s = 1.15) {
  // Precompute only small-ish tables is expensive in k6; use rejection-ish method.
  // Simple approach: use power-law transform on uniform.
  // Not perfect Zipf, but 충분히 "핫키 몰림"을 재현함.
  const u = Math.random();
  const x = Math.floor(Math.pow(u, -1 / (s - 1)));
  const v = ((x % N) + 1);
  return v;
}

/**
 * Hotset sampler:
 * - hotPct: fraction of IDs considered hot (e.g. 0.01 = top 1%)
 * - hotTraffic: probability of drawing from hot set (e.g. 0.7 = 70% traffic)
 */
export function hotsetSample(N, hotPct = 0.01, hotTraffic = 0.7) {
  const hotN = Math.max(1, Math.floor(N * hotPct));
  if (Math.random() < hotTraffic) {
    return 1 + Math.floor(Math.random() * hotN); // top hotN
  }
  return 1 + hotN + Math.floor(Math.random() * (N - hotN));
}
