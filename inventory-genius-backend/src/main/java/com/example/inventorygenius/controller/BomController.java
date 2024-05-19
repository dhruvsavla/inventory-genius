package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.service.BomService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/boms")
public class BomController {

    @Autowired
    private BomService bomService;

    @PostMapping
    public ResponseEntity<Bom> addBom(@RequestBody Bom bom) {
        Bom newBom = bomService.addBom(bom);
        return new ResponseEntity<>(newBom, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Bom>> getAllBoms() {
        List<Bom> boms = bomService.getAllBoms();
        return new ResponseEntity<>(boms, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bom> updateBom(@PathVariable Long id, @RequestBody Bom bomDetails) {
        Bom updatedBom = bomService.updateBom(id, bomDetails);
        return new ResponseEntity<>(updatedBom, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteBom(@PathVariable("id") Long id) {
        System.out.println("deleted");
        bomService.deleteBomById(id);
    }
}
