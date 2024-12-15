package com.example.hm_store_for_rbd.Controllers;

import com.example.hm_store.Controllers.ItemController;
import com.example.hm_store.Services.ItemService;
import com.example.hm_store.entity.Item;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void findAllItems() throws Exception {
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        when(itemService.findAllItems()).thenReturn(items);
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk());
    }

    @Test
    void findAllItems_empty() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getItemById_found() throws Exception {
        Item item = new Item();
        item.setName("name");
        item.setId(1);
        when(itemService.getById(1)).thenReturn(item);
        mockMvc.perform(get("/items/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getItemById_not_found() throws Exception {
        mockMvc.perform(get("/items/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItem() throws Exception {
        Item item = new Item();
        item.setName("name");
        item.setId(1);
        ObjectMapper objectMapper = new ObjectMapper();
        String itemJson = objectMapper.writeValueAsString(item);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemJson))
                .andExpect(status().isCreated());
    }

    @Test
    void createItem_addItem() throws Exception {
        String name = "Test Item";
        String size = "M";
        String color = "Red";
        String material = "Cotton";
        int price = 100;
        int quantity = 10;
        String image = "image_url";

        mockMvc.perform(post("/items/add_item")
                .param("name", name)
                .param("size", size)
                .param("color", color)
                .param("material", material)
                .param("price", String.valueOf(price))
                .param("quantity", String.valueOf(quantity))
                .param("image", image))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/items/add_item"));
    }

    @Test
    void deleteItem_found() throws Exception {
        Item item = new Item();
        item.setName("name");
        item.setId(1);
        when(itemService.deleteItemById(1)).thenReturn(item);
        mockMvc.perform(delete("/items/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    void deleteOrder_not_found() throws Exception {
        mockMvc.perform(delete("/items/{id}", 1))
                .andExpect(status().isNotFound());
    }
}
