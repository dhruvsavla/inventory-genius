package com.example.inventorygenius.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    private int binNumber;

    @Column(name = "rack_no")
    private int rackNumber;

    @Column(name = "skucode")
    private String skucode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storage", cascade = CascadeType.PERSIST)
    private List<Item> itemsInStorage = new ArrayList<>();

    public Storage() {

    }

    public Storage(Long storageId, int binNumber, int rackNumber) {
        this.storageId = storageId;
        this.binNumber = binNumber;
        this.rackNumber = rackNumber;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public int getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(int binNumber) {
        this.binNumber = binNumber;
    }

    public String getSkucode() {
        return skucode;
    }

    public void setSkucode(String skucode) {
        this.skucode = skucode;
    }

    public int getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(int rackNumber) {
        this.rackNumber = rackNumber;
    }

    public List<Item> getItemsInStorage() {
        return itemsInStorage;
    }

    public void setItemsInStorage(List<Item> itemsInStorage) {
        this.itemsInStorage = itemsInStorage;
    }

    public void addItemToStorage(Item item) {
        itemsInStorage.add(item);  // Add the item to the list of items in storage
        item.setStorage(this);  // Set the storage for the item
    }
}
