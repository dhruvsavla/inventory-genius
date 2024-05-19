package com.example.inventorygenius.entity;

import java.util.Date;

public class PickListData {
    
    private Long pickListId;
    private Long pickListNumber;
    private Date date;
    private String portalOrderNo;
    private String orderNo;
    private String portal;
    private String sellerSKU;
    private Double qty;
    private String description;
    private String binNumber;
    private String rackNumber;
    private Double pickQty;

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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
    public Long getPickListNumber() {
        return pickListNumber;
    }
    public void setPickListNumber(Long pickListNumber) {
        this.pickListNumber = pickListNumber;
    }
    public Long getPickListId() {
        return pickListId;
    }
    public void setPickListId(Long pickListId) {
        this.pickListId = pickListId;
    }
    public String getPortalOrderNo() {
        return portalOrderNo;
    }
    public void setPortalOrderNo(String portalOrderNo) {
        this.portalOrderNo = portalOrderNo;
    }

}
