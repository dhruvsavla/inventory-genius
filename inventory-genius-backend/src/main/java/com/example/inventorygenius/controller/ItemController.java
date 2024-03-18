package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.repository.SupplierRepository;
import com.example.inventorygenius.service.ItemService;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // Endpoint to add a new item
    @Autowired
    private SupplierRepository supplierRepository; // Assuming you have a repository for Supplier

    @PostMapping(path = "/{supplierId}", consumes = { "application/json" })
    public ResponseEntity<Item> addItem(@RequestBody Item item, @PathVariable Long supplierId) {
        Item newItem = itemService.addItem(supplierId, item);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }

    // Endpoint to get all items
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    // Endpoint to update an existing item by its ID
    @PutMapping(path = "/{itemId}", consumes = { "application/json" })
    public ResponseEntity<Item> updateItem(@PathVariable Long itemId, @RequestBody Item updatedItem) {
        Optional<Item> existingItemOptional = itemService.getItemById(itemId);

        if (existingItemOptional.isPresent()) {
            Item existingItem = existingItemOptional.get();

            // Update the fields of the existing item with the updated values
            existingItem.setDescription(updatedItem.getDescription());
            existingItem.setPackOf(updatedItem.getPackOf());
            existingItem.setParentSKU(updatedItem.getParentSKU());
            existingItem.setGroup1(updatedItem.getGroup1());
            existingItem.setGroup2(updatedItem.getGroup2());
            existingItem.setGroup3(updatedItem.getGroup3());
            existingItem.setSizeRange(updatedItem.getSizeRange());
            existingItem.setSize(updatedItem.getSize());
            existingItem.setUnit(updatedItem.getUnit());
            existingItem.setBarcode(updatedItem.getBarcode());
            existingItem.setSellingPrice(updatedItem.getSellingPrice());
            existingItem.setMrp(updatedItem.getMrp());
            existingItem.setSellerSKUCode(updatedItem.getSellerSKUCode());

            // Update supplier information if provided
            if (updatedItem.getSupplier() != null) {
                existingItem.setSupplier(updatedItem.getSupplier());
            }

            // Save the updated item
            Item updatedItemEntity = itemService.updateItem(existingItem);
            return new ResponseEntity<>(updatedItemEntity, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Item not found
        }
    }

    @GetMapping("/{skuCode}")
    public ResponseEntity<Item> getItemBySkuCode(@PathVariable String skuCode) {
        Item item = itemService.getItemBySkuCode(skuCode);
        if (item != null) {
            return new ResponseEntity<>(item, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{supplierSKUCode}/{supplierName}")
    public ResponseEntity<Item> getItemBySupplierSKUCodeAndSupplierName(
            @PathVariable String supplierSKUCode, @PathVariable String supplierName) {
        Item item = itemService.getItemBySupplierSKUCodeAndSupplierName(supplierSKUCode, supplierName);
        if (item != null) {
            return new ResponseEntity<>(item, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/order/{supplierSKUCode}/{description}")
    public ResponseEntity<Item> getItemBySupplierSKUCodeAndDescription(@PathVariable String supplierSKUCode,
            @PathVariable String description) {
        Item item = itemService.getItemBySupplierAndDescription(supplierSKUCode, description);
        if (item != null) {
            return new ResponseEntity<>(item, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") Long id) {
        System.out.println("deleted");
        itemService.deleteItemById(id);
    }

    @GetMapping("/{itemId}/orders")
    public ResponseEntity<?> getOrdersByItemId(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getOrdersByItemId(itemId));
    }

}
