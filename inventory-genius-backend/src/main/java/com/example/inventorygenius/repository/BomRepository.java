package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.Bom;

@Repository
public interface BomRepository extends JpaRepository<Bom, Long> {

}
