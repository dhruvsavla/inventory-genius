package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.inventorygenius.repository.PackingListDataRepository;
import com.example.inventorygenius.entity.PackingListData;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.ItemSupplierService;


import java.util.List;

@Service
public class PackingListDataService {

    @Autowired
    private PackingListDataRepository packingListDataRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    // Get all picklist data
    public List<PackingListData> getAllPackingListData() {
        return packingListDataRepository.findAll();
    }

    // Get picklist data by ID
    public PackingListData getPackingListDataById(Long packListId) {
        return packingListDataRepository.findById(packListId).orElse(null);
    }

    // Add new picklist data
    public PackingListData addPackingListData(PackingListData packingListData) {
        List<Order> orders = orderService.findByOrderNo(packingListData.getOrderNo());
        for(Order o : orders){
            packingListData.setOrder(o);
            break;
        }
        System.out.println("sellerSKU = " + packingListData.getSellerSKU());
        System.out.println("description = " + packingListData.getDescription());
        Item item = itemSupplierService.findItemsBySellerSKUAndDescription(packingListData.getSellerSKU(), packingListData.getDescription());
        System.out.println("picklist item sku = " + item.getSKUCode());
        packingListData.setItem(item);
        return packingListDataRepository.save(packingListData);
    }

    // Update picklist data
    public PackingListData updatePackingListData(Long packListId, PackingListData packListData) {
        packListData.setPackListId(packListId);
        return packingListDataRepository.save(packListData);
    }

    // Delete picklist data
    public void deletePackListData(Long packListId) {
        packingListDataRepository.deleteById(packListId);
    }

    public List<PackingListData> deletePickListDataByPickListNumber(Long pickListNumber) {
        // Find the list of PickListData objects by pickListNumber
        List<PackingListData> packListDataList = packingListDataRepository.findByPackListNumber(pickListNumber);
        
        // Check if the list is not empty
        if (!packListDataList.isEmpty()) {
            // Delete all PickListData objects in the list
            packingListDataRepository.deleteAll(packListDataList);
            
            // Return the list of deleted PickListData objects
            return packListDataList;
        } else {
            // Throw an exception if no PickListData objects are found with the given pickListNumber
            throw new IllegalArgumentException("No packlist data found with the provided packlist number: " + pickListNumber);
        }
    }
    
}
