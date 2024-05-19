package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.controller.ItemController;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.repository.ItemRepository;

@Service
public class ItemSupplierService {
    @Autowired
    ItemRepository itemRepository;
    
    public Item getItemBySKUCode(String skuCode) {
        System.out.println(itemRepository.findBySKUCode(skuCode));
        return itemRepository.findBySKUCode(skuCode);
    }
}
