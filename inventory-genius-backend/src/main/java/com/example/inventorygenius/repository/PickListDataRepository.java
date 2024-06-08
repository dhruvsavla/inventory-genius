package com.example.inventorygenius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.PickListData;

@Repository
public interface PickListDataRepository extends JpaRepository<PickListData, Long> {
    List<PickListData> findByPickListNumber(Long pickListNumber);

}
