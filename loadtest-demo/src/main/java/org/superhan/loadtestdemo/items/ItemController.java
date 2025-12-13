package org.superhan.loadtestdemo.items;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
public class ItemController {
  private final ItemService service;

  public ItemController(ItemService service) {
    this.service = service;
  }

  @GetMapping
  public List<Item> list(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size) {
    return service.list(page, size);
  }

  @GetMapping("/{id}")
  public Item get(@PathVariable long id) {
    return service.getByIdCached(id);
  }
}
