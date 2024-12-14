package com.example.hm_store.Controllers;

import com.example.hm_store.Services.ItemService;
import com.example.hm_store.entity.Item;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    
    @PostMapping
    public @ResponseBody ResponseEntity<Item> createItem(@RequestBody Item request) {
        itemService.saveItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody ResponseEntity<?> getItemById(@PathVariable int id) {
        Item item = itemService.getById(id);
        if (item == null) {
            return new ResponseEntity<>("{\"id\": " + id + "} is not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Item>> findAllItems() {
        List<Item> items =itemService.findAllItems();
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(items);
    }

    @DeleteMapping(value = "/{id}")
    public @ResponseBody ResponseEntity<?> deleteItem(@PathVariable int id) {
        Item item = itemService.deleteItemById(id);

        if (item == null) {
            return new ResponseEntity<>("Item{\"id\": " + id + "} is not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok("Item{\"id\": " + id + "} was deleted");
    }


    @PostMapping(value = "/add_item")
    public void createItem(HttpServletResponse response, @RequestParam("name") String name,
                           @RequestParam("size") String size,@RequestParam("color") String color,
                           @RequestParam("material") String material,
                           @RequestParam("price") int price, @RequestParam("quantity") int quantity,
                           @RequestParam("image") String image,
                           HttpSession session) throws IOException {

        Item item = new Item();
        item.setName(name);
        item.setSize(size);
        item.setColor(color);
        item.setMaterial(material);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setImage(image);
        itemService.saveItem(item);
        response.sendRedirect("/items/add_item");
    }

    
}
