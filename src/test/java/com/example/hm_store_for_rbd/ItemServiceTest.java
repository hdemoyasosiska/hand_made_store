package com.example.hm_store_for_rbd;

import com.example.hm_store.Services.ItemService;
import com.example.hm_store.entity.Item;
import com.example.hm_store.repo.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository);
    }


    @Test
    void getById_found(){
        int id = 1;
        Item item = new Item();
        item.setId(id);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        assertEquals(itemService.getById(id), item);
    }
    @Test
    void getById_not_found(){
        int id = -1;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> itemService.getById(id));
    }

    @Test
    void deleteItemById_deleted(){
        int id = 1;
        Item item = new Item();
        item.setId(id);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        assertEquals(itemService.deleteItemById(id), item);
    }

    @Test
    void deleteItemById_not_deleted(){
        int id = -1;
        when(itemRepository.findById(id)).thenReturn(Optional.empty());
        assertNull(itemService.deleteItemById(id));
    }
    @Test
    void findProductsByPage_found(){

        int pageNumber = 1;
        int pageSize = 3;

        List<Item> items = List.of(
                new Item(),
                new Item()
        );

        items.get(0).setName("i1");
        items.get(1).setName("i2");
        Page<Item> mockPage = new PageImpl<>(items,
                PageRequest.of(pageNumber, pageSize), items.size());

        when(itemRepository.findAll(Mockito.any(Pageable.class))).thenReturn(mockPage);


        Page<Item> result = itemService.findProductsByPage(pageNumber, pageSize);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals("i1", result.getContent().get(0).getName());
        assertEquals("i2", result.getContent().get(1).getName());
    }

    @Test
    void findProductsByPage_not_found(){

        int pageNumber = 1;
        int pageSize = 3;


        when(itemRepository.findAll(Mockito.any(Pageable.class))).thenReturn(null);

        Page<Item> result = itemService.findProductsByPage(pageNumber, pageSize);

        // Assert
        assertNull(result);
    }

}
