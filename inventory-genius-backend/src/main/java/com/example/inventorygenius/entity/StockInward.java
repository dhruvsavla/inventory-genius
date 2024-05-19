package com.example.inventorygenius.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock-inward")
public class StockInward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockInwardId;

    @Column(name = "date")
    private Date date;

    @Column(name = "skucode")
    private String skucode;

    @Column(name = "qty")
    private String qty;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "item_id")
    private Item item;

     @OneToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    public StockInward() {

    }

    public StockInward(Long stockInwardId, Date date, String skucode, String qty) {
        this.stockInwardId = stockInwardId;
        this.date = date;
        this.skucode = skucode;
        this.qty = qty;
    }

    public Long getStockInwardId() {
        return stockInwardId;
    }

    public void setStockInwardId(Long stockInwardId) {
        this.stockInwardId = stockInwardId;
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

}
