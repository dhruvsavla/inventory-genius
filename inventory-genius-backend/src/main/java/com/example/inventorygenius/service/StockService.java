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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockCountService stockCountService;

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

}
