package com.example.inventorygenius.controller;

import com.example.inventorygenius.entity.BomItem;
import com.example.inventorygenius.service.BomItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bomItems")
public class BomItemController {

    @Autowired
    private BomItemService bomItemService;

    @PostMapping("/create/{bomId}")
    public ResponseEntity<BomItem> createBomItem(@PathVariable Long bomId, @RequestBody BomItem bomItem) {
        BomItem createdBomItem = bomItemService.createBomItem(bomId, bomItem);
        return ResponseEntity.ok(createdBomItem);
    }

    @GetMapping
    public ResponseEntity<List<BomItem>> getAllBomItems() {
        List<BomItem> bomItems = bomItemService.getBomItems();
        return ResponseEntity.ok(bomItems);
    }

    @GetMapping("/{bomItemId}")
    public ResponseEntity<BomItem> getBomItemById(@PathVariable Long bomItemId) {
        BomItem bomItem = bomItemService.getBomItemById(bomItemId);
        return ResponseEntity.ok(bomItem);
    }

    @PutMapping("/{bomItemId}")
    public ResponseEntity<BomItem> updateBomItem(@PathVariable Long bomItemId, @RequestBody BomItem bomItemDetails) {
        BomItem updatedBomItem = bomItemService.updateBomItem(bomItemId, bomItemDetails);
        return ResponseEntity.ok(updatedBomItem);
    }

    @DeleteMapping("/{bomItemId}")
    public ResponseEntity<Void> deleteBomItem(@PathVariable Long bomItemId) {
        bomItemService.deleteBomItem(bomItemId);
        return ResponseEntity.noContent().build();
    }
}
