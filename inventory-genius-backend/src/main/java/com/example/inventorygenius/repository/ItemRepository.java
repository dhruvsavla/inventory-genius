package com.example.inventorygenius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.Item;

import com.example.inventorygenius.entity.Supplier;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
   // Optional<Item> findBySKUCodeAndItemSuppliersSupplier(String skuCode, Supplier supplier);

   Item findBySuppliers_SupplierIdAndSellerSKUCode(Long supplierId, String sellerSKUCode);
   List<Item> findBySuppliersSupplierId(Long supplierId);
   Item findBySellerSKUCodeAndDescriptionContaining(String sellerSKUCode, String description);
   Item findBySellerSKUCode(String sellerSKUCode);
   Item findByDescriptionContaining(String description);
   Item findBySKUCode(String sKUCode);

}
