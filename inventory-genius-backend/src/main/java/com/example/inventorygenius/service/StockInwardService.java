package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.entity.StockInward;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.StockInwardRepository;
import com.example.inventorygenius.repository.StockRepository;
import com.example.inventorygenius.repository.StorageRepository;

import java.time.LocalDate;
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

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private StorageService storageService;


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
        if (SI == null) {
            throw new IllegalArgumentException("StockInward with id " + id + " not found.");
        }

        // Check if SKU code is present in any storage
        for (Storage storage : storageService.getAllStorage()) {
            if (storage.getSkucode().equals(SI.getItem().getSKUCode())) {
                throw new IllegalStateException("Cannot delete StockInward because the SKU code is present in Storage.");
            }
        }

        // Create a new Stock object
        Stock stock = new Stock();
        stock.setDate(LocalDate.now());
        stock.setSkucode(SI.getItem().getSKUCode());
        stock.setAddQty("0");
        stock.setSubQty(SI.getQty());
        stock.setItem(SI.getItem());
        stock.setSource("stock inward");
        stock.setMessage("stock inward deleted");
        stock.setNumber("id = " + id);

        stockService.addStock(stock);

        String skuCode = SI.getItem().getSKUCode();
        StockCount stockCount = stockCountService.getStockCountBySKUCode(skuCode);
        if (stockCount != null) {
            double currentCount = stockCount.getCount();
            double subtractedCount = Double.parseDouble(SI.getQty());
            stockCount.setCount(currentCount - subtractedCount);
            stockCountService.updateStockCount(stockCount);
        } else {
            throw new IllegalStateException("Stock count for SKU code " + skuCode + " not found.");
        }

        stockInwardRepository.deleteById(id);
        System.out.println("StockInward with id " + id + " deleted successfully.");
    }

    public StockInward updateStockInward(Long id, StockInward stockInwardDetails) {
        StockInward stockInward = stockInwardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Stock Inward not found with id: " + id));

        // Update stock inward details
        stockInward.setDate(stockInwardDetails.getDate());
        stockInward.setSkucode(stockInwardDetails.getSkucode());
        stockInward.setQty(stockInwardDetails.getQty());
        stockInward.setStock(stockInwardDetails.getStock());
        stockInward.setItem(stockInwardDetails.getItem());

        return stockInwardRepository.save(stockInward);
    }
}
