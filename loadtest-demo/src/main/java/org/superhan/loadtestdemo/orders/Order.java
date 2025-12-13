package org.superhan.loadtestdemo.orders;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "orders",
  uniqueConstraints = {
    @UniqueConstraint(name = "uq_order_user_idem", columnNames = {"user_id", "idempotency_key"})
  },
  indexes = {
    @Index(name="idx_orders_user", columnList="user_id")
  }
)
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="user_id", nullable = false)
  private Long userId;

  @Column(name="item_id", nullable = false)
  private Long itemId;

  @Column(nullable = false)
  private Integer quantity;

  @Column(name="idempotency_key", nullable = false, length = 64)
  private String idempotencyKey;

  @Column(name="created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public Long getId() { return id; }
  public Long getUserId() { return userId; }
  public Long getItemId() { return itemId; }
  public Integer getQuantity() { return quantity; }
  public String getIdempotencyKey() { return idempotencyKey; }
  public Instant getCreatedAt() { return createdAt; }

  public void setUserId(Long userId) { this.userId = userId; }
  public void setItemId(Long itemId) { this.itemId = itemId; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }
  public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }
}
