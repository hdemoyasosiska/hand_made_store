package com.example.hm_store.Controllers;

import com.example.hm_store.Services.OrderService;
import com.example.hm_store.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public @ResponseBody ResponseEntity<Order> createUser(@RequestBody Order request) {
        orderService.saveOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(request);
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody ResponseEntity<?> getOrderById(@PathVariable("id") int id) {
        Order order = orderService.getById(id);

        if (order == null) {
            return new ResponseEntity<>("{\"id\": " + id + "} is not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Order>> findAllItems() {
        List<Order> orders =orderService.findAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(orders);
    }


    @DeleteMapping(value = "/{id}")
    public @ResponseBody ResponseEntity<?> deleteUser(@PathVariable int id) {
        Order order = orderService.deleteOrderById(id);

        if (order == null) {
            return new ResponseEntity<>("Order{\"id\": " + id + "} is not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok("University{\"id\": " + id + "} was deleted");
    }


}
