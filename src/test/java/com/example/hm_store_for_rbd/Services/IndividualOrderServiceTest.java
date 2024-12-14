package com.example.hm_store_for_rbd.Services;

import com.example.hm_store.Services.IndividualOrderService;
import com.example.hm_store.entity.IndividualOrder;
import com.example.hm_store.repo.IndividualOrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class IndividualOrderServiceTest {
    private IndividualOrderService individualOrderService;

    @Mock
    IndividualOrderRepository individualOrderRepository;

    @Mock
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        individualOrderService = new IndividualOrderService(individualOrderRepository, objectMapper);
    }

    @Test
    void getById_found() {
        int id = 1;
        IndividualOrder order = new IndividualOrder();
        order.setId_of_order(id);
        when(individualOrderRepository.findById(id)).thenReturn(Optional.of(order));
        assertEquals(individualOrderService.getById(id), order);
    }

    @Test
    void getById_not_found() {
        int id = -1;
        when(individualOrderRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> individualOrderService.getById(id));
    }

    @Test
    void saveOrder_successfully() {
        int id = 1;
        IndividualOrder order = new IndividualOrder();
        order.setId_of_order(id);
        individualOrderService.saveOrder(order);

        verify(individualOrderRepository, times(1)).save(order);
    }


    @Test
    void deleteOrderById_deleted() {
        int id = 1;
        IndividualOrder order = new IndividualOrder();
        order.setId_of_order(id);
        when(individualOrderRepository.findById(id)).thenReturn(Optional.of(order));
        assertEquals(individualOrderService.deleteOrderById(id), order);
    }

    @Test
    void deleteOrderById_not_deleted() {
        int id = -1;
        when(individualOrderRepository.findById(id)).thenReturn(Optional.empty());
        assertNull(individualOrderService.deleteOrderById(id));
    }


}
