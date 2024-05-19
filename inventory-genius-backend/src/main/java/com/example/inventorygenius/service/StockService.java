package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.StockRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    // Method to add a new item
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    // Method to get all items
    public List<Stock> getAllStock() {
        return stockRepository.findAll();
    }

    public void deleteStockById(Long id) {
        stockRepository.deleteById(id);
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
