package com.example.inventorygenius.controller;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // @GetMapping
    // public ResponseEntity<List<Item>> getAllItems() {
    //     List<Item> items = itemService.getAllItems();
    //     return new ResponseEntity<>(items, HttpStatus.OK);
    // }

    // @GetMapping("/{itemId}")
    // public ResponseEntity<Item> getItemById(@PathVariable Long itemId) {
    //     Item item = itemService.getItemById(itemId)
    //             .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
    //     return new ResponseEntity<>(item, HttpStatus.OK);
    // }

    // @PostMapping
    // public ResponseEntity<Item> addItem(@RequestBody Item item) {
    //     Item newItem = itemService.addItem(item);
    //     return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    // }

    @PutMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable Long itemId, @RequestBody Item updatedItem) {
        Item updatedItems = itemService.updateItem(itemId, updatedItem);
        return new ResponseEntity<>(updatedItems, HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

