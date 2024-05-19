package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventorygenius.entity.Supplier;
import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.ItemRepository;
import com.example.inventorygenius.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ItemRepository itemRepository;

//     public Supplier addSupplier(Supplier supplier) {
//     List<Item> newItems = new ArrayList<>();
//     for (ItemSupplier itemSupplier : supplier.getItemSuppliers()) {
//         if (itemSupplier.getItem().getItemId() == null) { // Check if item is new
//             newItems.add(itemSupplier.getItem());
//         }
//     }

//     // Save new items before saving the supplier
//     for (Item newItem : newItems) {
//         itemRepository.save(newItem);
//     }

//     return supplierRepository.save(supplier);
// }

public Supplier addSupplier(Supplier supplier) {
    // Perform any necessary validation or business logic checks here
    
    // Save the supplier using the repository
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

    public Supplier getSupplierByNameAndPhone(String name, String phone){
        return supplierRepository.findBySupplierNameAndPhonel(name, phone);
    }

    public Long getSupplierIdByNameAndPhone(String name, String phone){
        Supplier s = supplierRepository.findBySupplierNameAndPhonel(name, phone);
        return s.getSupplierId();
    }

    public Supplier getSupplierByName(String supplierName) {
        return supplierRepository.findBySupplierName(supplierName);
    }
    

}
