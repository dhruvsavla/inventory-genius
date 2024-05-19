package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.repository.StorageRepository;

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
        for (Item item : storage.getItemsInStorage()) {
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
    private ItemService itemService; // Assuming you have an ItemService

    

    public Storage addStorageWithItem(Storage storage, Long itemId) {
        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            
            // Add the storage to the item's list of storages
            item.getStorages().add(storage);
    
            // Save the changes
            storageRepository.save(storage);
            
            // Return the updated Storage entity
            return storage;
        } else {
            throw new RuntimeException("Item not found with ID: " + itemId);
        }
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
