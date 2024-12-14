package com.example.hm_store_for_rbd.Controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hm_store.Controllers.OrderController;
import com.example.hm_store.Services.OrderService;
import com.example.hm_store.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;



    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

    }


    @Test
    void findAllItems() throws Exception {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        when(orderService.findAllOrders()).thenReturn(orders);
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_found() throws Exception {
        Date date = new Date();
        Order order = new Order(date);
        when(orderService.getById(1)).thenReturn(order);
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderDate").value(date));
    }

    @Test
    void getOrderById_not_found() throws Exception {
        mockMvc.perform(get("/orders/{id}", 1))
              .andExpect(status().isNotFound());
    }

    @Test
    void createUser() throws Exception {
        Date date = new Date();
        Order order = new Order(date);
        ObjectMapper objectMapper = new ObjectMapper();
        String orderJson = objectMapper.writeValueAsString(order);
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteUser_found() throws Exception {
        Date date = new Date();
        Order order = new Order(date);
        when(orderService.deleteOrderById(1)).thenReturn(order);
        mockMvc.perform(delete("/orders/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_not_found() throws Exception {
        mockMvc.perform(delete("/orders/{id}", 1))
                .andExpect(status().isNotFound());
    }

}
