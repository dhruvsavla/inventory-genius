package com.example.inventorygenius.controller;

import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.service.StockCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock-counts")
public class StockCountController {

    @Autowired
    private StockCountService stockCountService;

    @GetMapping("/{id}")
    public ResponseEntity<StockCount> getStockCountById(@PathVariable Long id) {
        return stockCountService.getStockCountById(id)
                .map(stockCount -> new ResponseEntity<>(stockCount, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StockCount>> getAllStockCounts() {
        List<StockCount> stockCounts = stockCountService.getAllStockCounts();
        return new ResponseEntity<>(stockCounts, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<StockCount> addStockCount(@RequestBody StockCount stockCount) {
        StockCount savedStockCount = stockCountService.saveStockCount(stockCount);
        return new ResponseEntity<>(savedStockCount, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<StockCount> updateStockCount(@RequestBody StockCount stockCount) {
        StockCount updatedStockCount = stockCountService.updateStockCount(stockCount);
        return new ResponseEntity<>(updatedStockCount, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStockCountById(@PathVariable Long id) {
        stockCountService.deleteStockCountById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search/skucode/{skucode}")
    public ResponseEntity<StockCount> getStockCountBySKUCode(@PathVariable String skucode) {
        StockCount stockCount = stockCountService.getStockCountBySKUCode(skucode);
        return stockCount != null
                ? new ResponseEntity<>(stockCount, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

