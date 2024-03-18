package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Supplier;
import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public Supplier addSupplier(Supplier supplier) {
        List<Item> newItems = new ArrayList<>();
        for (Item item : supplier.getItemsSupplier()) {
            if (item.getItemId() == null) { // Check if item is new
                newItems.add(item);
            }
        }

        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier updateSupplier(Long id, Supplier bomDetails) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Supplier not found with id: " + id));

        supplier.setAddress(bomDetails.getAddress());
        supplier.setPhonel(bomDetails.getPhonel());
        supplier.setSupplierName(bomDetails.getSupplierName());

        return supplierRepository.save(supplier);
    }

    public void deleteSupplierById(Long id) {
        supplierRepository.deleteById(id);
    }

}
