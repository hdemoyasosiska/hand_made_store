package com.example.hm_store.Services;

import com.example.hm_store.entity.Customer;
import com.example.hm_store.entity.Item;
import com.example.hm_store.entity.Order;
import com.example.hm_store.repo.ItemRepository;
import com.example.hm_store.repo.OrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;


    private final ObjectMapper objectMapper;

    public Order getById(int id) {
        log.info("Find order {}", id);
        return orderRepository.findById(id).orElseThrow(() -> new IllegalStateException("Order "
                + "with this id not found"));
    }
    public void saveOrder(Order order) {
        log.info("Save order {}", order);
        orderRepository.save(order);
    }

    public List<Order> findAllOrders() {
        log.info("Find all orders");

        return orderRepository.findAll();
    }
    public Order deleteOrderById(int id) {
        log.info("Delete order {}", id);

        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            orderRepository.delete(order);
            return order;
        }
        return null;
    }

    private Map<String, Map<String, Object>> getMap(String shoppingCart) {
        try {
            return objectMapper.readValue(shoppingCart, new TypeReference<Map<String, Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse shopping cart data", e);
        }
    }

    public void createOrder(Customer customer, String shoppingCart) {
        Map<String, Map<String, Object>> cart = getMap(shoppingCart);
        System.out.println(cart.entrySet());
        assert cart != null;
        List<Integer> itemIds = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : cart.entrySet()) {
            String itemName = entry.getKey();
            System.out.println(itemName);
            Map<String, Object> entry1 = entry.getValue();
            int itemId = 0;
            for (Map.Entry<String, Object> entry2 : entry1.entrySet()) {
                if (Objects.equals(entry2.getKey(), "id")) {
                    itemId = (int) entry2.getValue();
                }
            }
            Optional<Item> item = itemRepository.findById(itemId);

            if (!item.isPresent()) {
                throw new IllegalArgumentException("Product with ID " + itemId + " not found");
            }
            itemIds.add(itemId);
            System.out.println(itemIds);
        }
        Order order = new Order();
        order.setCustomer(customer);
        order.setProductId(itemIds.stream().mapToInt(Integer::intValue).toArray());
        order.setOrderDate(new Date());
        order.setTotal_price(customer.getPrice());
        save(order);
    }
    private void save(Order order){
        orderRepository.save(order);
    }


}

