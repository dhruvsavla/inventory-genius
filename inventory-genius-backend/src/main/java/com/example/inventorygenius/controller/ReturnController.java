package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Return;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.service.ReturnService;
import com.example.inventorygenius.service.StockService;

import java.util.List;

@RestController
@RequestMapping("/return")
public class ReturnController {

    @Autowired
    private ReturnService returnService;

    @PostMapping
    public ResponseEntity<Return> addStock(@RequestBody Return stock) {
        Return newStock = returnService.addReturn(stock);
        return new ResponseEntity<>(newStock, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Return>> getAllStock() {
        List<Return> stocks = returnService.getAllReturns();
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteStock(@PathVariable("id") Long id) {
        System.out.println("deleted");
        returnService.deleteReturnById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Return> updateBom(@PathVariable Long id, @RequestBody Return stockDetails) {
        Return updatedStorage = returnService.updateStock(id, stockDetails);
        return new ResponseEntity<>(updatedStorage, HttpStatus.OK);
    }

}
