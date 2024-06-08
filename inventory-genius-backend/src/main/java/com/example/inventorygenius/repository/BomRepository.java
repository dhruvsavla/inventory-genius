package com.example.inventorygenius.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.Bom;

@Repository
public interface BomRepository extends JpaRepository<Bom, Long> {
    Optional<Bom> findByBomCode(String bomCode);
}
