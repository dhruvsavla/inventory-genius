package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.Storage;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

}
