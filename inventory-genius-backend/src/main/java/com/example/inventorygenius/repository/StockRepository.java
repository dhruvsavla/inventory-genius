package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Stock findBySkucode(String skucode);
}
