package com.example.inventorygenius.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
    private int qty;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToMany(mappedBy = "stockInwards")
    private List<Stock> stocks;

    public StockInward() {

    }

    public StockInward(Long stockInwardId, Date date, String skucode, int qty) {
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

}
