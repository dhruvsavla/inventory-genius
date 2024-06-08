package com.example.inventorygenius.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bomItem")
public class BomItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bomItem_id")
    private Long bomItemId;

    @Column(name = "bom_item")
    private String bomItem;

    @Column(name = "qty")
    private String qty;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bom_id")
    private Bom bom;

    public BomItem () {

    }

    public BomItem(Long bomItemId, String bomItem, String qty, Bom bom) {
        this.bomItemId = bomItemId;
        this.bomItem = bomItem;
        this.qty = qty;
        this.bom = bom;
    }

    public Long getBomItemId() {
        return bomItemId;
    }

    public void setBomItemId(Long bomItemId) {
        this.bomItemId = bomItemId;
    }

    public String getBomItem() {
        return bomItem;
    }

    public void setBomItem(String bomItem) {
        this.bomItem = bomItem;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Bom getBom() {
        return bom;
    }

    public void setBom(Bom bom) {
        this.bom = bom;
    }

}
