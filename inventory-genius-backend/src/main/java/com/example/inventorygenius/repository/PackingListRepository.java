package com.example.inventorygenius.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.inventorygenius.entity.PackingList;
import com.example.inventorygenius.entity.PickList;

@Repository
public interface PackingListRepository extends JpaRepository<PackingList, Long>{
    PackingList findByPackingListNumber(Long packListNumber);
}
