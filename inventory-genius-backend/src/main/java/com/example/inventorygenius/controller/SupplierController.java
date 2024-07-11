package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Supplier;
import com.example.inventorygenius.service.SupplierService;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<?> addSupplier(@RequestBody Supplier supplier) {
        Supplier savedSupplier = supplierService.addSupplier(supplier);
        return new ResponseEntity<>(savedSupplier, HttpStatus.CREATED);
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

   @GetMapping("/{supplierName}/{phonel}")
   public Supplier getSupplierByNameAndPhone(@PathVariable String supplierName, @PathVariable String phonel) {
       return supplierService.getSupplierByNameAndPhone(supplierName, phonel);
   }

   @GetMapping("/id/{supplierName}/{phonel}")
   public Long getSupplierIdByNameAndPhone(@PathVariable String supplierName, @PathVariable String phonel) {
       return supplierService.getSupplierIdByNameAndPhone(supplierName, phonel);
   }

   @GetMapping("/name/{supplierName}")
    public ResponseEntity<Supplier> getSupplierByName(@PathVariable String supplierName) {
        Supplier supplier = supplierService.getSupplierByName(supplierName);
        if (supplier != null) {
            return new ResponseEntity<>(supplier, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
}

   
}
