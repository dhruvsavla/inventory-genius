package com.example.inventorygenius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.inventorygenius.entity.ItemPortalMapping;

@Repository
public interface ItemPortalMappingRepository extends JpaRepository<ItemPortalMapping, Long> {
    // Define additional methods if needed
}
