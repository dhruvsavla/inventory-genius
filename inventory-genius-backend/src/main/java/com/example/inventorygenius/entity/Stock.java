package com.example.inventorygenius.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "date")
    private Date date;

    @Column(name = "skucode")
    private String skucode;

    @Column(name = "add_qty")
    private double addQty;

    @Column(name = "sub_qty")
    private double subQty;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public Stock() {

    }

    public Stock(Long stockId, Date date, String skucode, double addQty, double subQty) {
        this.stockId = stockId;
        this.date = date;
        this.skucode = skucode;
        this.addQty = addQty;
        this.subQty = subQty;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSkucode() {
        return skucode;
    }

    public void setSkucode(String skucode) {
        this.skucode = skucode;
    }

    public double getAddQty() {
        return addQty;
    }

    public void setAddQty(double addQty) {
        this.addQty = addQty;
    }

    public double getSubQty() {
        return subQty;
    }

    public void setSubQty(double subQty) {
        this.subQty = subQty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
