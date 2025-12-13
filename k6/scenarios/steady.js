import http from "k6/http";
import { check, sleep } from "k6";
import { randomIntBetween } from "https://jslib.k6.io/k6-utils/1.4.0/index.js";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const ITEMS_MAX = parseInt(__ENV.ITEMS_MAX || "100000", 10);
const USER_MAX = parseInt(__ENV.USER_MAX || "5000", 10);

export const options = {
  scenarios: {
    steady: {
      executor: "constant-arrival-rate",
      rate: 200, // 200 RPS
      timeUnit: "1s",
      duration: "10m",
      preAllocatedVUs: 200,
      maxVUs: 2000,
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<400", "p(99)<900"],
  },
};

function userId() {
  return randomIntBetween(1, USER_MAX);
}

function uniformItemId() {
  return randomIntBetween(1, ITEMS_MAX);
}

export default function () {
  const u = userId();
  const pick = Math.random();

  // endpoint mix: list 40%, detail 55%, order 5%
  if (pick < 0.40) {
    const res = http.get(`${BASE_URL}/items?page=0&size=20`, { headers: { "X-User-Id": String(u) } });
    check(res, { "list 200": (r) => r.status === 200 });
  } else if (pick < 0.95) {
    const id = uniformItemId();
    const res = http.get(`${BASE_URL}/items/${id}`, { headers: { "X-User-Id": String(u) } });
    check(res, { "detail 200": (r) => r.status === 200 });
  } else {
    const id = uniformItemId();
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

  sleep(Math.random() * 0.3); // 약간의 think time
}
