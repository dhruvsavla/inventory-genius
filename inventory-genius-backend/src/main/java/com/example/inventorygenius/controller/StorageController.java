package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.service.StorageService;

import java.util.List;

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping
    public ResponseEntity<Storage> addStorage(@RequestBody Storage storage) {
        Storage newStorage = storageService.addStorage(storage);
        return new ResponseEntity<>(newStorage, HttpStatus.CREATED);
    }

    @PostMapping(path = "/{itemId}", consumes = { "application/json" })
    public ResponseEntity<Storage> addStorageWithItem(@RequestBody Storage storage, @PathVariable Long itemId) {
        Storage newStorage = storageService.addStorageWithItem(storage, itemId);
        if (newStorage != null) {
            return new ResponseEntity<>(newStorage, HttpStatus.CREATED);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Storage> updateBom(@PathVariable Long id, @RequestBody Storage storageDetails) {
        Storage updatedStorage = storageService.updateStorage(id, storageDetails);
        return new ResponseEntity<>(updatedStorage, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Storage>> getAllStorage() {
        List<Storage> storage = storageService.getAllStorage();
        return new ResponseEntity<>(storage, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteStorage(@PathVariable("id") Long id) {
        System.out.println("deleted");
        storageService.deleteStorageById(id);
    }
}
