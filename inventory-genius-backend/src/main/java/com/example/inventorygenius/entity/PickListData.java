package com.example.inventorygenius.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "picklist_data")
public class PickListData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pick_list_id")
    private Long pickListId;

    @Column(name = "pick_list_number")
    private Long pickListNumber;

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

    @Column(name = "bin_number")
    private String binNumber;

    @Column(name = "rack_number")
    private String rackNumber;

    @Column(name = "pick_qty")
    private Double pickQty;

    @ManyToOne()
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne()
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @ManyToOne()
    @JoinColumn(name = "item_id")
    private Item item;

    public PickListData() {
    }

    public PickListData(Long pickListId, Long pickListNumber, LocalDate date, String portalOrderNo, String orderNo,
                        String bomCode, String portal, String sellerSKU, Double qty, String description, String binNumber,
                        String rackNumber, Double pickQty) {
        this.pickListId = pickListId;
        this.pickListNumber = pickListNumber;
        this.date = date;
        this.portalOrderNo = portalOrderNo;
        this.orderNo = orderNo;
        this.bomCode = bomCode;
        this.portal = portal;
        this.sellerSKU = sellerSKU;
        this.qty = qty;
        this.description = description;
        this.binNumber = binNumber;
        this.rackNumber = rackNumber;
        this.pickQty = pickQty;
    }

    public Long getPickListId() {
        return pickListId;
    }

    public void setPickListId(Long pickListId) {
        this.pickListId = pickListId;
    }

    public Long getPickListNumber() {
        return pickListNumber;
    }

    public void setPickListNumber(Long pickListNumber) {
        this.pickListNumber = pickListNumber;
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

    public String getBinNumber() {
        return binNumber;
    }

    public void setBinNumber(String binNumber) {
        this.binNumber = binNumber;
    }

    public String getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(String rackNumber) {
        this.rackNumber = rackNumber;
    }

    public Double getPickQty() {
        return pickQty;
    }

    public void setPickQty(Double pickQty) {
        this.pickQty = pickQty;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Storage getStorage() {
        return storage;
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
