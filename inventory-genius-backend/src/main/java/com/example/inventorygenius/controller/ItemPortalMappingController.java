package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.ItemPortalMapping;
import com.example.inventorygenius.entity.StockInward;
import com.example.inventorygenius.repository.ItemPortalMappingRepository;
import com.example.inventorygenius.service.ItemPortalMappingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/itemportalmapping")
public class ItemPortalMappingController {

    @Autowired
    private ItemPortalMappingService itemService;
    private  ItemPortalMappingRepository itemPortalMappingRepository;

    // Endpoint to add a new item
    @PostMapping
    public ResponseEntity<ItemPortalMapping> addItem(@RequestBody ItemPortalMapping itemPortalMapping) {
        ItemPortalMapping newItem = itemService.addItem(itemPortalMapping);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }

    // Endpoint to get all items
    @GetMapping
    public ResponseEntity<List<ItemPortalMapping>> getAllItems() {
        List<ItemPortalMapping> items = itemService.getAllItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteIMP(@PathVariable("id") Long id) {
        System.out.println("deleted");
        itemService.deleteIMPById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemPortalMapping> updateIPM(@PathVariable Long id, @RequestBody ItemPortalMapping stockInwardDetails) {
        ItemPortalMapping updatedIPM = itemService.updateIPM(id, stockInwardDetails);
        return new ResponseEntity<>(updatedIPM, HttpStatus.OK);
    }
    
}
