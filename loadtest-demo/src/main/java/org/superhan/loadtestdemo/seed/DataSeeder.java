package org.superhan.loadtestdemo.seed;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.superhan.loadtestdemo.items.Item;
import org.superhan.loadtestdemo.items.ItemRepository;

@Component
public class DataSeeder implements CommandLineRunner {
  private final ItemRepository repo;
  private final int n;

  public DataSeeder(ItemRepository repo, @Value("${app.seed.items:100000}") int n) {
    this.repo = repo;
    this.n = n;
  }

  @Override
  public void run(String... args) {
    if (repo.count() > 0) return;

    List<Item> batch = new ArrayList<>();
    for (int i = 1; i <= n; i++) {
      Item it = new Item();
      it.setTitle("item-" + i);
      it.setPrice((long) (1000 + (i % 50000)));
      batch.add(it);
      if (batch.size() == 1000) {
        repo.saveAll(batch);
        batch.clear();
      }
    }
    if (!batch.isEmpty()) repo.saveAll(batch);
  }
}
