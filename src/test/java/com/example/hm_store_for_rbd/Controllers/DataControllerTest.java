package com.example.hm_store_for_rbd.Controllers;


import com.example.hm_store.Controllers.DataController;
import com.example.hm_store.Services.CustomerService;
import com.example.hm_store.Services.IndividualOrderService;
import com.example.hm_store.Services.ItemService;
import com.example.hm_store.Services.OrderService;
import com.example.hm_store.entity.Customer;
import com.example.hm_store.entity.IndividualOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DataControllerTest {
    @Mock
    private OrderService orderService;
    @Mock
    private CustomerService customerService;
    @Mock
    private ItemService itemService;
    @Mock
    private IndividualOrderService individualOrderService;

    @InjectMocks
    DataController dataController;

    private MockMvc mockMvc;
    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(dataController).build();
    }

    @Test
    void sendData() throws Exception {
        mockMvc.perform(post("/basket")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "req")
                .param("id", "1")
                .param("cart", "abc"))
                .andExpect(status().isOk());

        verify(customerService, times(1)).save(any(Customer.class));
        verify(orderService, times(1)).createOrder(any(Customer.class), any(String.class));
    }

    @Test
    void handleOrder() throws Exception {


        mockMvc.perform(multipart("/individual_order")

                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .param("name", "John Doe")
                        .param("phone", "1234567890")
                        .param("address", "123 Main St")
                        .param("email", "johndoe@example.com")
                        .param("requirements", "Custom design")
                        .param("budget", "500")
                        .param("color", "Blue")
                        .param("materials", "Wood")
                        .param("deadline", "2024-12-31")
                        )
                .andExpect(status().isOk());

        verify(customerService, times(1)).save(any(Customer.class));
        verify(individualOrderService, times(1)).saveOrder(any(IndividualOrder.class));
    }

    @Test
    void getData() throws Exception {
        mockMvc.perform(get("/getData")
                        .param("page_number", "1")
                        .param("countPerPage", "3"))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .findProductsByPage(any(Integer.class), any(Integer.class));
    }

    @Test
    void getLength() throws Exception {
        mockMvc.perform(get("/getLength"))
                .andExpect(status().isOk());

        verify(itemService, times(1))
                .getTotalProductCount();
    }
}
