package com.example.inventorygenius.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.controller.ItemController;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.repository.ItemRepository;

@Service
public class ItemSupplierService {
    @Autowired
    ItemRepository itemRepository;

    public List<Item> getAllItems(){
        return itemRepository.findAll();
    }
    
    public Item getItemBySKUCode(String skuCode) {
        System.out.println(itemRepository.findBySKUCode(skuCode));
        return itemRepository.findBySKUCode(skuCode);
    }
}
