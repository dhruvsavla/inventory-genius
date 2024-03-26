package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockInward;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.service.StockInwardService;
import com.example.inventorygenius.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/stockInward")
public class StockInwardController {

    @Autowired
    private StockInwardService stockInwardService;

    @Autowired
    private StockService stockService;

    @PostMapping
    public ResponseEntity<StockInward> addStockInward(@RequestBody StockInward stockInward) {
        StockInward newStock = stockInwardService.addStockInward(stockInward);

        Stock stock = new Stock();
        stock.setDate(stockInward.getDate());
        stock.setSkucode(stockInward.getSkucode());
        stock.setAddQty(stockInward.getQty());
        stock.setSubQty(0);
        stock.setItem(stockInward.getItem());

        stockService.addStock(stock);

        return new ResponseEntity<>(newStock, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StockInward>> getAllStock() {
        List<StockInward> stocks = stockInwardService.getAllStockInward();
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteStockInward(@PathVariable("id") Long id) {
        System.out.println("deleted");
        stockInwardService.deleteStockInwardById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockInward> updateBom(@PathVariable Long id, @RequestBody StockInward stockInwardDetails) {
        StockInward updatedStockInward = stockInwardService.updateStockInward(id, stockInwardDetails);
        return new ResponseEntity<>(updatedStockInward, HttpStatus.OK);
    }

}
