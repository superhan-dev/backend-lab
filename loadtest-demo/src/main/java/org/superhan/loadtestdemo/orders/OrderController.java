package org.superhan.loadtestdemo.orders;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.superhan.loadtestdemo.common.UserId;
import org.superhan.loadtestdemo.orders.dto.CreateOrderRequest;

@RestController
@RequestMapping("/orders")
public class OrderController {
  private final OrderService service;

  public OrderController(OrderService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Order create(HttpServletRequest req,
                      @RequestHeader(name="Idempotency-Key", required = false) String idemKey,
                      @RequestBody CreateOrderRequest body) {
    long userId = UserId.from(req);
    if (userId <= 0) throw new IllegalArgumentException("X-User-Id header required");
    if (idemKey == null || idemKey.isBlank()) throw new IllegalArgumentException("Idempotency-Key header required");
    if (body.getItemId() == null || body.getQuantity() == null) throw new IllegalArgumentException("itemId, quantity required");
    return service.createIdempotent(userId, idemKey, body.getItemId(), body.getQuantity());
  }
}
