package com.example.inventorygenius.service;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    // public List<Item> getAllItems() {
    //     return itemRepository.findAll();
    // }

    public Optional<Item> getItemById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    // public Item addItem(Item item) {
    //     return itemRepository.save(item);
    // }

    public Item updateItem(Long id, Item updatedItem) {
        // Check if the item exists
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        // Update the existing item with the new values
        existingItem.setSKUCode(updatedItem.getSKUCode());
        existingItem.setDescription(updatedItem.getDescription());
        // Update other fields as needed
        
        // Save the updated item
        return itemRepository.save(existingItem);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
