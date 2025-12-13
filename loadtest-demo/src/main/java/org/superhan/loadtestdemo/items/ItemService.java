package org.superhan.loadtestdemo.items;

import java.util.List;
import java.util.Optional;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ItemService {
  private final ItemRepository repo;
  private final Cache cache;
  private final Counter cacheHit;
  private final Counter cacheMiss;

  public ItemService(ItemRepository repo, CacheManager cacheManager, MeterRegistry meterRegistry) {
    this.repo = repo;
    this.cache = cacheManager.getCache("item");
    this.cacheHit = meterRegistry.counter("app_cache_hit_total");
    this.cacheMiss = meterRegistry.counter("app_cache_miss_total");
  }

  public List<Item> list(int page, int size) {
    return repo.findAll(PageRequest.of(page, size)).getContent();
  }

  public Item getByIdCached(long id) {
    if (cache != null) {
      Item cached = cache.get(id, Item.class);
      if (cached != null) {
        cacheHit.increment();
        return cached;
      }
    }
    cacheMiss.increment();
    Item it = repo.findById(id).orElseThrow();
    if (cache != null) cache.put(id, it);
    return it;
  }
}
