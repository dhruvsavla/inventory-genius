package com.example.inventorygenius.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "packinglist")
public class PackingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "packinglist_id")
    private Long packinglistId;

    @Column(name = "packinglist_number")
    private Long packingListNumber; 

    @OneToMany()
    private List<Order> orders = new ArrayList<>();

    public PackingList () {

    }

    public PackingList(Long packinglistId) {
        this.packinglistId = packinglistId;
        
    }

    public Long getPackinglistId() {
        return packinglistId;
    }

    public void setPackinglistId(Long packinglistId) {
        this.packinglistId = packinglistId;
    }

    
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Long getPackingListNumber() {
        return packingListNumber;
    }

    public void setPackingListNumber(Long packingListNumber) {
        this.packingListNumber = packingListNumber;
    }
    
}
