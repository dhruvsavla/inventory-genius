package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.StockRepository;
import com.example.inventorygenius.service.StockCountService;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockCountService stockCountService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    // Method to add a new item
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // Method to get all items
    public List<Stock> getAllStock() {
        return stockRepository.findAll();
    }

    @Transactional
    public void deleteStockById(Long id) {
        Optional<Stock> stockOptional = stockRepository.findById(id);
        
        if (stockOptional.isPresent()) {
            Stock stock = stockOptional.get();
            StockCount sc = stockCountService.getStockCountBySKUCode(stock.getSkucode());
            Double prevCount = sc.getCount();
    
            if (Double.parseDouble(stock.getAddQty()) > 0) {
                sc.setCount(prevCount - Double.parseDouble(stock.getAddQty()));
            } else if (Double.parseDouble(stock.getSubQty()) > 0) {
                sc.setCount(prevCount + Double.parseDouble(stock.getSubQty()));
            }
    
            stockCountService.updateStockCount(sc);
            stockRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Stock with ID " + id + " not found");
        }
    }
    

    public Stock updateStock(Long id, Stock stockDetails) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BOM not found with id: " + id));

                stock.setDate(stockDetails.getDate());
                stock.setAddQty(stockDetails.getAddQty());
                stock.setSubQty(stockDetails.getSubQty());
                stock.setSkucode(stockDetails.getSkucode());


        return stockRepository.save(stock);
    }

    public Stock findBySkucode(String skucode) {
        return stockRepository.findBySkucode(skucode);
    }

    public Map<String, List<Stock>> groupStocksBySkucode(List<Stock> stocks) {
        return stocks.stream().collect(Collectors.groupingBy(Stock::getSkucode));
    }

    // Method to print grouped stocks and calculate counts
    public Map<String, Double> printGroupedStocksAndCalculateCounts() {
        Map<String, List<Stock>> groupedStocks = groupStocksBySkucode(getAllStock());
        Map<String, Double> skucodeCounts = new HashMap<>();

        groupedStocks.forEach((skucode, stockList) -> {
            double countChange = 0.0;

            for (Stock stock : stockList) {
                if (Double.parseDouble(stock.getAddQty()) > 0) {
                    countChange += Double.parseDouble(stock.getAddQty());
                } else if (Double.parseDouble(stock.getSubQty()) > 0) {
                    countChange -= Double.parseDouble(stock.getSubQty());
                }
            }

            skucodeCounts.put(skucode, skucodeCounts.getOrDefault(skucode, 0.0) + countChange);

            // Print the skucode and its stocks
            System.out.println("Skucode: " + skucode);
            stockList.forEach(s -> System.out.println("    " + s));
        });

        // Update StockCount based on skucodeCounts
        skucodeCounts.forEach((skucode, countChange) -> {
            StockCount stockCount = stockCountService.getStockCountBySKUCode(skucode);
            if (stockCount != null) {
                stockCount.setCount(countChange);
                stockCountService.updateStockCount(stockCount);
            } else {
                // Handle case where StockCount does not exist for the skucode
                System.out.println("StockCount not found for skucode: " + skucode);
            }
        });

        return skucodeCounts;
    }

}
