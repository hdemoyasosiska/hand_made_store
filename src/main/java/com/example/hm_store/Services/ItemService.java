package com.example.hm_store.Services;

import com.example.hm_store.entity.Item;
import com.example.hm_store.repo.ItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;

    public Item getById(int id) {
        return itemRepository.findById(id).orElseThrow(() -> new IllegalStateException("Item "
                + "with this id not found"));
    }

    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public void saveItem(Item item) {
        itemRepository.save(item);
    }
    public Item deleteItemById(int id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            itemRepository.delete(item);
            return item;
        }

        return null;
    }
    public Page<Item> findProductsByPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return itemRepository.findAll(pageable);
    }
    public long getTotalProductCount() {
        return itemRepository.count();
    }

}
