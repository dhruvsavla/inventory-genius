package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventorygenius.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
