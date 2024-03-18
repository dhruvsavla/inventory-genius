package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findBySKUCode(String skuCode);

    Item findBySellerSKUCodeAndSupplier_supplierName(String sellerSKUCode, String supplierName);

    Item findBySellerSKUCodeAndDescription(String supplierSkuCode, String description);

}
