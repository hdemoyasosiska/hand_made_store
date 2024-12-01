package com.example.hm_store.Services;

import com.example.hm_store.entity.Customer;
import com.example.hm_store.entity.IndividualOrder;
import com.example.hm_store.entity.Item;
import com.example.hm_store.entity.Order;
import com.example.hm_store.repo.IndividualOrderRepository;
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
public class IndividualOrderService {
    private final IndividualOrderRepository individualOrderRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper;

    public IndividualOrder getById(int id) {
        log.info("Find order {}", id);
        return individualOrderRepository.findById(id).orElseThrow(() -> new IllegalStateException("Order "
                + "with this id not found"));
    }
    public void saveOrder(IndividualOrder order) {
        log.info("Save order {}", order);
        individualOrderRepository.save(order);
    }

    public List<IndividualOrder> findAllOrders() {
        log.info("Find all orders");

        return individualOrderRepository.findAll();
    }
    public IndividualOrder deleteOrderById(int id) {
        log.info("Delete order {}", id);

        Optional<IndividualOrder> optionalOrder = individualOrderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            IndividualOrder order = optionalOrder.get();
            individualOrderRepository.delete(order);
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




}

