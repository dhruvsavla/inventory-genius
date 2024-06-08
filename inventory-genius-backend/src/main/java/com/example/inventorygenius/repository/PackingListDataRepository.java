package com.example.inventorygenius.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.inventorygenius.entity.PackingListData;

@Repository
public interface PackingListDataRepository extends JpaRepository<PackingListData, Long> {
    List<PackingListData> findByPackListNumber(Long packListNumber);
}

