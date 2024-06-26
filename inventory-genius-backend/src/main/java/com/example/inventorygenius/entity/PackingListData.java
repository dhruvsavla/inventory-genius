package com.example.inventorygenius.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "packinglist_data")
public class PackingListData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pack_list_id")
    private Long packListId;

    @Column(name = "pack_list_number")
    private Long packListNumber;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "portal_order_no")
    private String portalOrderNo;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "bom_code")
    private String bomCode;

    @Column(name = "portal")
    private String portal;

    @Column(name = "seller_sku")
    private String sellerSKU;

    @Column(name = "qty")
    private Double qty;

    @Column(name = "description")
    private String description;

    @Column(name = "pack_qty")
    private Double packQty;

    @ManyToOne()
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne()
    @JoinColumn(name = "order_id")
    private Order order;

    public PackingListData() {
        
    }

    public PackingListData(Long packListId, Long packListNumber, LocalDate date, String portalOrderNo, String orderNo,
            String bomCode, String portal, String sellerSKU, Double qty, String description, Double packQty) {
        this.packListId = packListId;
        this.packListNumber = packListNumber;
        this.date = date;
        this.portalOrderNo = portalOrderNo;
        this.orderNo = orderNo;
        this.bomCode = bomCode;
        this.portal = portal;
        this.sellerSKU = sellerSKU;
        this.qty = qty;
        this.description = description;
        this.packQty = packQty;
    }

    public Long getPackListId() {
        return packListId;
    }

    public void setPackListId(Long packListId) {
        this.packListId = packListId;
    }

    public Long getPackListNumber() {
        return packListNumber;
    }

    public void setPackListNumber(Long packListNumber) {
        this.packListNumber = packListNumber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPortalOrderNo() {
        return portalOrderNo;
    }

    public void setPortalOrderNo(String portalOrderNo) {
        this.portalOrderNo = portalOrderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getBomCode() {
        return bomCode;
    }

    public void setBomCode(String bomCode) {
        this.bomCode = bomCode;
    }

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public String getSellerSKU() {
        return sellerSKU;
    }

    public void setSellerSKU(String sellerSKU) {
        this.sellerSKU = sellerSKU;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPackQty() {
        return packQty;
    }

    public void setPackQty(Double packQty) {
        this.packQty = packQty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
    
}
