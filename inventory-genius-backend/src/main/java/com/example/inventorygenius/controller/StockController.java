package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.service.StockService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        Stock newStock = stockService.addStock(stock);
        return new ResponseEntity<>(newStock, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStock() {
        List<Stock> stocks = stockService.getAllStock();
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable("id") Long id) {
        System.out.println("deleted");
        stockService.deleteStockById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateBom(@PathVariable Long id, @RequestBody Stock stockDetails) {
        Stock updatedStorage = stockService.updateStock(id, stockDetails);
        return new ResponseEntity<>(updatedStorage, HttpStatus.OK);
    }

    @GetMapping("/update-counts")
    public Map<String, Double> updateStockCounts() {
        return stockService.printGroupedStocksAndCalculateCounts();
    }
}
