package com.example.inventorygenius.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.entity.Supplier;
import com.example.inventorygenius.repository.ItemRepository;
import com.example.inventorygenius.repository.SupplierRepository;
import com.example.inventorygenius.service.StockCountService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/item/supplier")
public class ItemSupplierController {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    StockCountService stockCountService;

    @PostMapping
    public Item saveItemWithExistingSupplier(@RequestBody Item item) {
        // Check if suppliers list is empty
        if (item.getSuppliers().isEmpty()) {
            // Handle scenario where no suppliers are provided (e.g., return an error response)
            // Here, we simply return null to indicate failure
            return itemRepository.save(item);
        }
        
        // Find the existing supplier by its ID or other unique identifier
        Optional<Supplier> existingSupplier = supplierRepository.findById(item.getSuppliers().get(0).getSupplierId());
    
        // Ensure the existing supplier is present before setting it on the item
        if (existingSupplier.isPresent()) {
            // Create a list to hold the existing supplier
            List<Supplier> existingSuppliers = new ArrayList<>();
            existingSuppliers.add(existingSupplier.get());
    
            // Set the list of existing suppliers on the item
            item.setSuppliers(existingSuppliers);
    
            // Save the item with associated existing suppliers
            return itemRepository.save(item);
        } else {
            // Handle scenario where supplier doesn't exist (e.g., return an error response)
            // Here, we simply return null to indicate failure
            return null;
        }

    }
    

    @GetMapping
    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping("/{itemId}")
    public Item findItem(@PathVariable Long itemId){
        return itemRepository.findById(itemId).orElse(null);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<Item> updateItem(@PathVariable Long itemId, @RequestBody Item updatedItem) {
        Optional<Item> existingItemOptional = itemRepository.findById(itemId);
        
        if (existingItemOptional.isPresent()) {
            Item existingItem = existingItemOptional.get();
            // Update properties of the existing item with the new values
            existingItem.setDescription(updatedItem.getDescription());
            existingItem.setPackOf(updatedItem.getPackOf());
            existingItem.setParentSKU(updatedItem.getParentSKU());
            existingItem.setBarcode(updatedItem.getBarcode());
            existingItem.setSKUCode(updatedItem.getSKUCode());
            existingItem.setGroup1(updatedItem.getGroup1());
            existingItem.setGroup2(updatedItem.getGroup2());
            existingItem.setGroup3(updatedItem.getGroup3());
            existingItem.setSizeRange(updatedItem.getSizeRange());
            existingItem.setUnit(updatedItem.getUnit());
            existingItem.setSellerSKUCode(updatedItem.getSellerSKUCode());
            existingItem.setSellingPrice(updatedItem.getSellingPrice());
            existingItem.setMrp(updatedItem.getMrp());
            existingItem.setImg(updatedItem.getImg());
            existingItem.setSuppliers(updatedItem.getSuppliers());
            existingItem.setStorages(updatedItem.getStorages());
            existingItem.setBoms(updatedItem.getBoms());
            existingItem.setStockEntries(updatedItem.getStockEntries());
            existingItem.setOrders(updatedItem.getOrders());
            
            // Save the updated item
            Item savedItem = itemRepository.save(existingItem);
            return ResponseEntity.ok(savedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an existing item
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        
        if (itemOptional.isPresent()) {
            itemRepository.deleteById(itemId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/{supplierId}/{sellerSKUCode}")
    public Item findItemsBySupplierAndSellerSKUCode(
            @PathVariable("supplierId") Long supplierId,
            @PathVariable("sellerSKUCode") String sellerSKUCode) {
        // Use the ItemRepository to find items based on the supplied parameters
        return itemRepository.findBySuppliers_SupplierIdAndSellerSKUCode(supplierId, sellerSKUCode);
    }

    @GetMapping("/search/{supplierId}")
public List<String> findSellerSKUCodesBySupplier(
        @PathVariable("supplierId") Long supplierId) {
    // Use the ItemRepository to find items based on the supplied supplierId
    List<Item> items = itemRepository.findBySuppliersSupplierId(supplierId);
    
    // Extract sellerSKUCode from the found items and return as a list
    List<String> sellerSKUCodes = new ArrayList<>();
    for (Item item : items) {
        sellerSKUCodes.add(item.getSellerSKUCode());
    }
    
    return sellerSKUCodes;
}

@GetMapping("/order/search/{sellerSKUCode}/{description}")
public Item findItemsBySellerSKUCodeAndDescription(
        @PathVariable(value = "sellerSKUCode") String sellerSKUCode,
        @PathVariable(value = "description") String description) {
    // Check if both parameters are provided
    return itemRepository.findBySellerSKUCodeAndDescriptionContaining(sellerSKUCode, description);
}

@GetMapping("/search/skucode/{skucode}")
public Item getMethodName(@PathVariable String skucode) {
    return itemRepository.findBySKUCode(skucode);
}

@GetMapping("/search/seller/{sellerName}")
public ResponseEntity<String> findSellerSKUCodesBySellerName(@PathVariable String sellerName) {
    Supplier supplier = supplierRepository.findBySupplierName(sellerName);

    if (supplier == null) {
        return ResponseEntity.notFound().build();
    }

    StringBuilder sellerSKUCodeBuilder = new StringBuilder();

    List<Item> items = itemRepository.findBySuppliersSupplierId(supplier.getSupplierId());

    for (Item item : items) {
        sellerSKUCodeBuilder.append(item.getSellerSKUCode()).append(", ");
    }

    String sellerSKUCode = sellerSKUCodeBuilder.toString().replaceAll(", $", "");

    return ResponseEntity.ok(sellerSKUCode);
}

}
