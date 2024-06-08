package com.example.inventorygenius.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.OneToMany;
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

    @Column(name = "bomCode", length = 100)
    private String bomCode;

    // @Column(name = "bom-item")
    // private String bomItem;

    // @Column(name = "qty")
    // private double qty;

    @Column (name = "default_start_date")
    private Date defaultStartDate;

    @Column (name = "default_end_date")
    private Date defaultEndDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(name = "bom_items", joinColumns = @JoinColumn(name = "bom_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
    // @JsonManagedReference(value = "bom")
    private List<Item> bomItems = new ArrayList<>();

    @OneToMany(mappedBy = "bom", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<BomItem> itemsInBom = new ArrayList<>();

    public Bom() {

    }

    public Bom(Long bomId, String sKUCode, Date defaultStartDate, Date defaultEndDate, String bomCode) {
        this.bomId = bomId;
        this.SKUCode = sKUCode;
        this.defaultStartDate = defaultStartDate;
        this.bomCode = bomCode;
        this.defaultEndDate = defaultEndDate;
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

    public List<Item> getBomItems() {
        return bomItems;
    }

    public void setBomItems(List<Item> bomItems) {
        this.bomItems = bomItems;
    }

    public Date getDefaultStartDate() {
        return defaultStartDate;
    }

    public void setDefaultStartDate(Date defaultStartDate) {
        this.defaultStartDate = defaultStartDate;
    }

    public Date getDefaultEndDate() {
        return defaultEndDate;
    }

    public void setDefaultEndDate(Date defaultEndDate) {
        this.defaultEndDate = defaultEndDate;
    }

    public List<BomItem> getItemsInBom() {
        return itemsInBom;
    }

    public void setItemsInBom(List<BomItem> itemsInBom) {
        this.itemsInBom = itemsInBom;
    }

    public String getBomCode() {
        return bomCode;
    }

    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }
    
}
