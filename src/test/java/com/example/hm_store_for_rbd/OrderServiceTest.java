package com.example.hm_store_for_rbd;


import com.example.hm_store.Services.OrderService;
import com.example.hm_store.entity.Customer;
import com.example.hm_store.entity.Item;
import com.example.hm_store.entity.Order;
import com.example.hm_store.repo.ItemRepository;
import com.example.hm_store.repo.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, itemRepository, objectMapper);
    }

    @Test
    void getById_found() {
        int id = 1;
        Order order = new Order();
        order.setId_of_order(id);
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        assertEquals(orderService.getById(id), order);
    }

    @Test
    void getById_not_found() {
        int id = -1;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> orderService.getById(id));
    }

    @Test
    void saveOrder_successfully() {
        int id = 1;
        Order order = new Order();
        order.setId_of_order(id);
        orderService.saveOrder(order);

        verify(orderRepository, times(1)).save(order);
    }


    @Test
    void deleteOrderById_deleted() {
        int id = 1;
        Order order = new Order();
        order.setId_of_order(id);
        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        assertEquals(orderService.deleteOrderById(id), order);
    }

    @Test
    void deleteOrderById_not_deleted() {
        int id = -1;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertNull(orderService.deleteOrderById(id));
    }

    @Test
    void createOrder_success() throws JsonProcessingException {

        Customer customer = new Customer();
        customer.setPrice(100);

        String shoppingCart = "{\"item1\": {\"id\": 1}, \"item2\": {\"id\": 2}}";

        Map<String, Map<String, Object>> mockCart = new HashMap<>();
        mockCart.put("item1", Map.of("id", 1));
        mockCart.put("item2", Map.of("id", 2));

        Item item1 = new Item();
        item1.setId(1);
        Item item2 = new Item();
        item2.setId(2);

        Map<String, Map<String, Object>> cart = new HashMap<>();
        when(objectMapper.readValue(eq(shoppingCart), any(TypeReference.class))).thenReturn(mockCart);
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2)).thenReturn(Optional.of(item2));

        orderService.createOrder(customer, shoppingCart);

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_unsuccess() throws JsonProcessingException {
        Customer customer = new Customer();
        String shoppingCart = "{\"item1\": {\"id\": 99}}";

        Map<String, Map<String, Object>> mockCart = new HashMap<>();
        mockCart.put("item1", Map.of("id", 99));

        when(objectMapper.readValue(eq(shoppingCart), any(TypeReference.class))).thenReturn(mockCart);
        when(itemRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(customer, shoppingCart);
        });

        assertEquals("Product with ID 99 not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }
}
