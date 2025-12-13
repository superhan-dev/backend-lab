package org.superhan.loadtestdemo.items;

import jakarta.persistence.*;

@Entity
@Table(name = "items",
  indexes = {
    @Index(name = "idx_items_title", columnList = "title")
  }
)
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false)
  private Long price;

  public Long getId() { return id; }
  public String getTitle() { return title; }
  public Long getPrice() { return price; }

  public void setTitle(String title) { this.title = title; }
  public void setPrice(Long price) { this.price = price; }
}
