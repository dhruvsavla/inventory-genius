package com.example.inventorygenius.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "stock-count")
public class StockCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stockCount_id")
    private Long stockCountId;

    @Column(name = "count")
    private double count;

    @OneToOne
    @JoinColumn(name = "item_id") // Foreign key in StockCount table
    private Item item;

    public StockCount() {

    }

    public StockCount(Long stockCountId, double count) {
        this.stockCountId = stockCountId;
        this.count = count;
    }

    public Long getStockCountId() {
        return stockCountId;
    }

    public void setStockCountId(Long stockCountId) {
        this.stockCountId = stockCountId;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
