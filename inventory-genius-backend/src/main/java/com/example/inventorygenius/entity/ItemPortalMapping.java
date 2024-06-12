package com.example.inventorygenius.entity;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_portal_mapping")
public class ItemPortalMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_portal_mapping_id")
    private Long id;

    @Column(name = "portal")
    private String portal;

    @Column(name = "seller_sku_code")
    private String sellerSkuCode;

    @Column(name = "portal_sku_code")
    private String portalSkuCode;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne()
    @JoinColumn(name = "supplier_id") // Assuming this is the foreign key column in item_portal_mapping table
    private Supplier supplier;

    @OneToMany(mappedBy = "itemPortalMapping", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Order> orders;

    public ItemPortalMapping() {

    }

    public ItemPortalMapping(Long id, String portal, String sellerSkuCode, String portalSkuCode,
            Item item, Supplier supplier) {
        this.id = id;
        this.portal = portal;
        this.sellerSkuCode = sellerSkuCode;
        this.portalSkuCode = portalSkuCode;
        this.item = item;
        this.supplier = supplier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }
    

    public String getSellerSkuCode() {
        return sellerSkuCode;
    }

    public void setSellerSkuCode(String sellerSkuCode) {
        this.sellerSkuCode = sellerSkuCode;
    }

    public String getPortalSkuCode() {
        return portalSkuCode;
    }

    public void setPortalSkuCode(String portalSkuCode) {
        this.portalSkuCode = portalSkuCode;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
