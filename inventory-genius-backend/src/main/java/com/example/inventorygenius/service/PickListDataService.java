package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.inventorygenius.repository.PickListDataRepository;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.entity.PickListData;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.StorageService;
import com.example.inventorygenius.service.ItemSupplierService;

import java.util.List;

@Service
public class PickListDataService {

    @Autowired
    private PickListDataRepository pickListDataRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    // Get all picklist data
    public List<PickListData> getAllPickListData() {
        return pickListDataRepository.findAll();
    }

    // Get picklist data by ID
    public PickListData getPickListDataById(Long pickListId) {
        return pickListDataRepository.findById(pickListId).orElse(null);
    }

    // Add new picklist data
    public PickListData addPickListData(PickListData pickListData) {
        List<Order> orders = orderService.findByOrderNo(pickListData.getOrderNo());
        for(Order o : orders){
            pickListData.setOrder(o);
            break;
        }
        Item item = itemSupplierService.findItemsBySellerSKUAndDescription(pickListData.getSellerSKU(), pickListData.getDescription());
        System.out.println("picklist item sku = " + item.getSKUCode());
        Storage storage = storageService.getStorageByBinAndRack(pickListData.getBinNumber(), pickListData.getRackNumber(), item.getSKUCode());
        System.out.println("picklist storage sku = " + storage.getSkucode());
        pickListData.setStorage(storage);
        pickListData.setItem(item);
        return pickListDataRepository.save(pickListData);
    }

    // Update picklist data
    public PickListData updatePickListData(Long pickListId, PickListData pickListData) {
        pickListData.setPickListId(pickListId);
        return pickListDataRepository.save(pickListData);
    }

    // Delete picklist data
    public void deletePickListData(Long pickListId) {
        pickListDataRepository.deleteById(pickListId);
    }

    public List<PickListData> deletePickListDataByPickListNumber(Long pickListNumber) {
        // Find the list of PickListData objects by pickListNumber
        List<PickListData> pickListDataList = pickListDataRepository.findByPickListNumber(pickListNumber);
        
        // Check if the list is not empty
        if (!pickListDataList.isEmpty()) {
            // Delete all PickListData objects in the list
            pickListDataRepository.deleteAll(pickListDataList);
            
            // Return the list of deleted PickListData objects
            return pickListDataList;
        } else {
            // Throw an exception if no PickListData objects are found with the given pickListNumber
            throw new IllegalArgumentException("No picklist data found with the provided picklist number: " + pickListNumber);
        }
    }
    
}
