package com.example.inventorygenius.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "skucode")
    private String skucode;

    @Column(name = "add_qty")
    private String addQty;

    @Column(name = "sub_qty")
    private String subQty;

    @Column(name = "source")
    private String source;

    @Column(name = "message")
    private String message;

    @Column(name = "number")
    private String number;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne(mappedBy = "stock", cascade = CascadeType.ALL)
    @JsonIgnore
    private StockInward stockInward;

    public Stock() {

    }

    public Stock(Long stockId, LocalDate date, String skucode, String addQty, String subQty, String source, String message, String number) {
        this.stockId = stockId;
        this.date = date;
        this.skucode = skucode;
        this.addQty = addQty;
        this.subQty = subQty;
        this.source = source;
        this.message = message;
        this.number = number;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSkucode() {
        return skucode;
    }

    public void setSkucode(String skucode) {
        this.skucode = skucode;
    }

    public String getAddQty() {
        return addQty;
    }

    public void setAddQty(String addQty) {
        this.addQty = addQty;
    }

    public String getSubQty() {
        return subQty;
    }

    public void setSubQty(String subQty) {
        this.subQty = subQty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public StockInward getStockInward() {
        return stockInward;
    }

    public void setStockInward(StockInward stockInward) {
        this.stockInward = stockInward;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
