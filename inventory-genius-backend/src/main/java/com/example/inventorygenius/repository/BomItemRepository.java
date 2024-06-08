package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.BomItem;

@Repository
public interface BomItemRepository extends JpaRepository<BomItem, Long> {

}
