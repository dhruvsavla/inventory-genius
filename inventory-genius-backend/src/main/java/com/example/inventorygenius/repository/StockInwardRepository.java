package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.StockInward;

@Repository
public interface StockInwardRepository extends JpaRepository<StockInward, Long> {

}
