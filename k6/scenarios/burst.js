import http from "k6/http";
import { check, sleep } from "k6";
import { randomIntBetween } from "https://jslib.k6.io/k6-utils/1.4.0/index.js";
import { hotsetSample } from "../utils/zipf.js";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const ITEMS_MAX = parseInt(__ENV.ITEMS_MAX || "100000", 10);
const USER_MAX = parseInt(__ENV.USER_MAX || "5000", 10);

const HOT_PCT = parseFloat(__ENV.HOT_PCT || "0.01");
const HOT_TRAFFIC = parseFloat(__ENV.HOT_TRAFFIC || "0.70");

export const options = {
  scenarios: {
    warm: {
      executor: "constant-arrival-rate",
      rate: 200,
      timeUnit: "1s",
      duration: "5m",
      preAllocatedVUs: 200,
      maxVUs: 2000,
      exec: "flow",
    },
    burst: {
      executor: "constant-arrival-rate",
      rate: 800,
      timeUnit: "1s",
      duration: "2m",
      startTime: "5m",
      preAllocatedVUs: 400,
      maxVUs: 4000,
      exec: "flow",
    },
    recover: {
      executor: "constant-arrival-rate",
      rate: 200,
      timeUnit: "1s",
      duration: "5m",
      startTime: "7m",
      preAllocatedVUs: 200,
      maxVUs: 2000,
      exec: "flow",
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.02"],
  },
};

function userId() { return randomIntBetween(1, USER_MAX); }
function itemId() { return hotsetSample(ITEMS_MAX, HOT_PCT, HOT_TRAFFIC); }

export function flow() {
  const u = userId();
  const pick = Math.random();

  if (pick < 0.30) {
    const res = http.get(`${BASE_URL}/items?page=0&size=20`, { headers: { "X-User-Id": String(u) } });
    check(res, { "list 200": (r) => r.status === 200 });
  } else if (pick < 0.95) {
    const id = itemId();
    const res = http.get(`${BASE_URL}/items/${id}`, { headers: { "X-User-Id": String(u) } });
    check(res, { "detail 200": (r) => r.status === 200 });
  } else {
    const id = itemId();
    const idem = `${__VU}-${__ITER}-${Date.now()}`;
    const res = http.post(`${BASE_URL}/orders`, JSON.stringify({ itemId: id, quantity: 1 }), {
      headers: {
        "Content-Type": "application/json",
        "X-User-Id": String(u),
        "Idempotency-Key": idem,
      },
    });
    check(res, { "order 201": (r) => r.status === 201 });
  }

  sleep(Math.random() * 0.2);
}

export default flow;
