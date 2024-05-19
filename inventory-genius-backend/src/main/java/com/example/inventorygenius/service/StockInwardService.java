package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.entity.StockInward;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.StockInwardRepository;
import com.example.inventorygenius.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StockInwardService {

    @Autowired
    private StockInwardRepository stockInwardRepository;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockCountService stockCountService;

    // Method to add a new item
    public StockInward addStockInward(StockInward stockInward) {
        // Save the StockInward
        return stockInwardRepository.save(stockInward);
    }
    

    // Method to get all items
    public List<StockInward> getAllStockInward() {
        return stockInwardRepository.findAll();
    }

    public StockInward findById(Long id) {
        return stockInwardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stock Inward not found with id: " + id));
    }

    public void deleteStockInwardById(Long id) {
        StockInward SI = findById(id);
    
        // Create a new Stock object
        Stock stock = new Stock();
        stock.setDate(SI.getDate());
        stock.setSkucode(SI.getSkucode());
        stock.setAddQty("0"); // Assuming when deleting, there's no addition
        stock.setSubQty(SI.getQty()); // Set subQty to the qty of the StockInward being deleted
        stock.setItem(SI.getItem()); // Set the item associated with the StockInward
        stock.setSource("stock inward");
        stock.setMessage("stock inward deleted");
        stock.setNumber("id = " + String.valueOf(id));
        // Add the Stock to the Stock table
        stockService.addStock(stock);

        String skuCode = SI.getSkucode();
        StockCount stockCount = stockCountService.getStockCountBySKUCode(skuCode);

        double currentCount = stockCount.getCount();
        double additionalCount = Double.parseDouble(SI.getQty());
        stockCount.setCount(currentCount - additionalCount);
        // Delete the StockInward
        stockInwardRepository.deleteById(id);
    }
    

    public StockInward updateStockInward(Long id, StockInward stockInwardDetails) {
        StockInward stockInward = stockInwardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stock Inward not found with id: " + id));
        
        // Update stock inward details
        stockInward.setDate(stockInwardDetails.getDate());
        stockInward.setSkucode(stockInwardDetails.getSkucode());
        stockInward.setQty(stockInwardDetails.getQty());
        stockInward.setStock(stockInwardDetails.getStock());

    
        return stockInwardRepository.save(stockInward);
    }

}
