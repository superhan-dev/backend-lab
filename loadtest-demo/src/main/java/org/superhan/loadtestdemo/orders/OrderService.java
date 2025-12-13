package org.superhan.loadtestdemo.orders;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
  private final OrderRepository repo;

  public OrderService(OrderRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public Order createIdempotent(long userId, String idemKey, long itemId, int quantity) {
    return repo.findByUserIdAndIdempotencyKey(userId, idemKey)
        .orElseGet(() -> {
          Order o = new Order();
          o.setUserId(userId);
          o.setItemId(itemId);
          o.setQuantity(quantity);
          o.setIdempotencyKey(idemKey);
          return repo.save(o);
        });
  }
}
