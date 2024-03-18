package com.example.inventorygenius.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "bom")
public class Bom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bom_id")
    private Long bomId;

    @Column(name = "SKUCode", length = 100)
    private String SKUCode;

    @Column(name = "bom-item")
    private String bomItem;

    @Column(name = "qty")
    private double qty;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bom_items", joinColumns = @JoinColumn(name = "bom_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
    // @JsonManagedReference(value = "bom")
    private List<Item> bomItems = new ArrayList<>();

    public Bom() {

    }

    public Bom(Long bomId, String sKUCode, String bomItem, double qty) {
        this.bomId = bomId;
        this.SKUCode = sKUCode;
        this.bomItem = bomItem;
        this.qty = qty;
    }

    public Long getBomId() {
        return bomId;
    }

    public void setBomId(Long bomId) {
        this.bomId = bomId;
    }

    public String getSKUCode() {
        return SKUCode;
    }

    public void setSKUCode(String sKUCode) {
        SKUCode = sKUCode;
    }

    public String getBomItem() {
        return bomItem;
    }

    public void setBomItem(String bomItem) {
        this.bomItem = bomItem;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public List<Item> getBomItems() {
        return bomItems;
    }

    public void setBomItems(List<Item> bomItems) {
        this.bomItems = bomItems;
    }

}
