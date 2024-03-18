package com.example.inventorygenius.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_portal_mapping")
public class ItemPortalMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String portal;

    @Column(nullable = false)
    private String supplier;

    @Column(name = "seller_sku_code", nullable = false)
    private String sellerSkuCode;

    @Column(name = "portal_sku_code", nullable = false)
    private String portalSkuCode;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public ItemPortalMapping() {

    }

    public ItemPortalMapping(Long id, String portal, String supplier, String sellerSkuCode, String portalSkuCode,
            Item item) {
        this.id = id;
        this.portal = portal;
        this.supplier = supplier;
        this.sellerSkuCode = sellerSkuCode;
        this.portalSkuCode = portalSkuCode;
        this.item = item;
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

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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
}
