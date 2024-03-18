package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventorygenius.entity.StockInward;

public interface StockInwardRepository extends JpaRepository<StockInward, Long> {

}