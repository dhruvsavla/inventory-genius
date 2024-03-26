package com.example.inventorygenius.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "stock_stock_inward",
            joinColumns = @JoinColumn(name = "stock_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_inward_id")
    )
    private List<StockInward> stockInwards;

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

    public List<StockInward> getStockInwards() {
        return stockInwards;
    }

    public void setStockInwards(List<StockInward> stockInwards) {
        this.stockInwards = stockInwards;
    }

}
