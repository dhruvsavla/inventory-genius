package com.example.inventorygenius.repository;

import com.example.inventorygenius.entity.Portal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortalRepository extends JpaRepository<Portal, Long> {
}
