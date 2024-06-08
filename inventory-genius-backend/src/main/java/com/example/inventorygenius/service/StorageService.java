package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.repository.StorageRepository;
import com.example.inventorygenius.service.ItemService;

import com.example.inventorygenius.controller.ItemSupplierController;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;


    public Storage addStorage(Storage storage) {
        List<Item> newItems = new ArrayList<>();
        for (Item item : storage.getItems()) {
            if (item.getItemId() == null) { // Check if item is new
                newItems.add(item);
            }
        }

        return storageRepository.save(storage);
    }

    public List<Storage> getAllStorage() {
        return storageRepository.findAll();
    }

    public void deleteStorageById(Long id) {
        storageRepository.deleteById(id);
    }

    @Autowired
    private ItemSupplierController itemService;
    
    @Autowired
    private ItemSupplierService itemService1;// Assuming you have an ItemService

    

    public Storage addStorageWithItem(Storage storage, Long itemId) {
        Item item = itemService.findItem(itemId);
    
            // Add the item to the storage's list of items
            storage.getItems().add(item);
    
            // Add the storage to the item's list of storages
            item.getStorages().add(storage);
    
            // Save the storage first
            storageRepository.save(storage);
    
            // Update the item
            itemService.updateItem(item.getItemId(), item);
    
            return storage;
    }
    


    public Storage updateStorage(Long id, Storage storageDetails) {
        Storage storage = storageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Storage not found with id: " + id));

                storage.setBinNumber(storageDetails.getBinNumber());
                storage.setRackNumber(storageDetails.getRackNumber());
                storage.setSkucode(storageDetails.getSkucode());
                storage.setQty(storageDetails.getQty());


        return storageRepository.save(storage);
    }

    public Storage getStorageWithSkucode(String skucode) {
        return storageRepository.findBySkucode(skucode);
    }

    public Storage getStorageByBinAndRack(String binNumber, String rackNumber, String skucode) {
        return storageRepository.findByBinNumberAndRackNumberAndSkucode(binNumber, rackNumber, skucode);
    }

}
