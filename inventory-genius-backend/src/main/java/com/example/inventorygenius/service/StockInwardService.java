package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.StockInward;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.StockInwardRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StockInwardService {

    @Autowired
    private StockInwardRepository stockInwardRepository;

    // Method to add a new item
    public StockInward addStockInward(StockInward stock) {
        return stockInwardRepository.save(stock);
    }

    // Method to get all items
    public List<StockInward> getAllStockInward() {
        return stockInwardRepository.findAll();
    }

    public void deleteStockInwardById(Long id) {
        stockInwardRepository.deleteById(id);
    }

    public StockInward updateStockInward(Long id, StockInward stockInwardDetails) {
        StockInward stockInward = stockInwardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BOM not found with id: " + id));

                stockInward.setDate(stockInwardDetails.getDate());
                stockInward.setSkucode(stockInwardDetails.getSkucode());
                stockInward.setQty(stockInwardDetails.getQty());


        return stockInwardRepository.save(stockInward);
    }

}
