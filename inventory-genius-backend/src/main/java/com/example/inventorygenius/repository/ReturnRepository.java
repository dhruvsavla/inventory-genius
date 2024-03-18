package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventorygenius.entity.Return;

public interface ReturnRepository extends JpaRepository<Return, Long> {

}