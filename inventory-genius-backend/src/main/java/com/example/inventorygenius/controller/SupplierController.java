package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Supplier;
import com.example.inventorygenius.service.SupplierService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<Supplier> addSupplier(@RequestBody Supplier supplier) {
        Supplier newSupplier = supplierService.addSupplier(supplier);
        return new ResponseEntity<>(newSupplier, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplir(@PathVariable Long id, @RequestBody Supplier bomDetails) {
        Supplier updatedSupplier = supplierService.updateSupplier(id, bomDetails);
        return new ResponseEntity<>(updatedSupplier, HttpStatus.OK);
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Supplier> deleteSupplier(@PathVariable Long id, @RequestBody Supplier bomDetails) {
    //     Supplier deletedSupplier = supplierService.deleteSupplierById(id);
    //     return new ResponseEntity<>(deletedSupplier, HttpStatus.NO_CONTENT);
    // }

    @DeleteMapping("/{id}")
   public void deleteSupplier(@PathVariable("id") Long id) {
    System.out.println("deleted");
      supplierService.deleteSupplierById(id);
   }
}
