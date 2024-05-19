package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.ItemPortalMapping;
import com.example.inventorygenius.entity.StockInward;
import com.example.inventorygenius.repository.ItemPortalMappingRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemPortalMappingService {

    @Autowired
    private ItemPortalMappingRepository itemportalmappingRepository;

    // Method to add a new item
    public ItemPortalMapping addItem(ItemPortalMapping item) {
        return itemportalmappingRepository.save(item);
    }

    // Method to get all items
    public List<ItemPortalMapping> getAllItems() {
        return itemportalmappingRepository.findAll();
    }

    public void deleteIMPById(Long id) {
        itemportalmappingRepository.deleteById(id);
    }

    public ItemPortalMapping updateIPM(Long id, ItemPortalMapping ipmDetails) {
        ItemPortalMapping ipm = itemportalmappingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BOM not found with id: " + id));

                ipm.setPortal(ipmDetails.getPortal());
                ipm.setPortalSkuCode(ipmDetails.getPortalSkuCode());
                ipm.setSellerSkuCode(ipmDetails.getSellerSkuCode());
                ipm.setSupplier(ipmDetails.getSupplier());


        return itemportalmappingRepository.save(ipm);
    }
}
