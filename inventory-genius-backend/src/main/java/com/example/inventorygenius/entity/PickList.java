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
@Table(name = "picklist")
public class PickList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picklist_id")
    private Long picklistId;

    @Column(name = "picklist_number")
    private Long pickListNumber; 

    @OneToMany()
    private List<Order> orders = new ArrayList<>();

    public PickList () {

    }

    public PickList(Long picklistId) {
        this.picklistId = picklistId;
    }

    public Long getPicklistId() {
        return picklistId;
    }

    public void setPicklistId(Long picklistId) {
        this.picklistId = picklistId;
    }
    
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Long getPickListNumber() {
        return pickListNumber;
    }

    public void setPickListNumber(Long pickListNumber) {
        this.pickListNumber = pickListNumber;
    }
    
}
