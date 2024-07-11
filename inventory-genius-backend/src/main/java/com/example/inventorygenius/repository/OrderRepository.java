package com.example.inventorygenius.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.inventorygenius.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items")
    List<Order> findAllWithItems();
    List<Order> findByOrderNo(String orderNo);
    List<Order> findByAwbNo(String awbNo);
}
