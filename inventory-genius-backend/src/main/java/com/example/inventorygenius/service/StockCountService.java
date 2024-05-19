package com.example.inventorygenius.service;

import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.repository.StockCountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockCountService {

    @Autowired
    StockCountRepository stockCountRepository;

    public StockCount saveStockCount(StockCount stockCount) {
        return stockCountRepository.save(stockCount);
    }

    // Method to retrieve a StockCount entity by its ID
    public Optional<StockCount> getStockCountById(Long id) {
        return stockCountRepository.findById(id);
    }

    // Method to retrieve all StockCount entities
    public List<StockCount> getAllStockCounts() {
        return stockCountRepository.findAll();
    }

    // Method to update a StockCount entity
    public StockCount updateStockCount(StockCount stockCount) {
        return stockCountRepository.save(stockCount);
    }

    // Method to delete a StockCount entity by its ID
    public void deleteStockCountById(Long id) {
        stockCountRepository.deleteById(id);
    }

    public StockCount getStockCountBySKUCode(String skuCode) {
        return stockCountRepository.findByItem_SKUCode(skuCode);
    }
}