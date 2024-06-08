package com.example.inventorygenius.entity;

import java.util.ArrayList;
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
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Long storageId;

    @Column(name = "bin_no")
    private String binNumber;

    @Column(name = "rack_no")
    private String rackNumber;

    @Column(name = "skucode")
    private String skucode;

    @Column(name = "qty")
    private String qty;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "storage_item",
        joinColumns = @JoinColumn(name = "storage_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();

    public Storage() {

    }

    public Storage(Long storageId, String binNumber, String rackNumber, String qty) {
        this.storageId = storageId;
        this.binNumber = binNumber;
        this.rackNumber = rackNumber;
        this.qty = qty;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public String getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }

    public String getSkucode() {
        return skucode;
    }

    public void setSkucode(String skucode) {
        this.skucode = skucode;
    }

    public String getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(String rackNumber) {
        this.rackNumber = rackNumber;
    }

    @JsonIgnore
    public List<Item> getItemsInStorage() {
        return items;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setItemsInStorage(List<Item> items) {
        this.items = items;
    }

    @JsonIgnore
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

}
