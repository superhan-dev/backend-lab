package org.superhan.loadtestdemo.common;

import jakarta.servlet.http.HttpServletRequest;

public final class UserId {
  private UserId() {}

  public static long from(HttpServletRequest request) {
    String v = request.getHeader("X-User-Id");
    if (v == null || v.isBlank()) return 0L;
    try { return Long.parseLong(v); } catch (Exception e) { return 0L; }
  }
}
