package com.example.inventorygenius.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "Date")
    private Date Date;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "portal")
    private String portal;

    @Column(name = "portal_order_no")
    private String portalOrderNo;

    @Column(name = "portal_order_line_id")
    private String portalOrderLineId;

    @Column(name = "portal_sku")
    private String portalSKU;

    @Column(name = "seller_sku")
    private String sellerSKU;

    @Column(name = "product_description")
    private String productDescription;

    @Column(name = "quantity")
    private double qty;

    @Column(name = "ship_by_date")
    private Date shipByDate;

    @Column(name = "dispatched")
    private String dispatched;

    @Column(name = "courier")
    private String courier;

    @Column(name = "cancel")
    private String cancel;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "awb_no")
    private String awbNo;
    

    @ManyToMany()
    @JoinTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "picklist_id")
    private PickList picklist;

    @ManyToOne()
    @JoinColumn(name = "id")
    private ItemPortalMapping itemPortalMapping;

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private List<PickListData> pickListData = new ArrayList<>();

    @OneToMany(mappedBy = "order")
    @JsonIgnore
    private List<PackingListData> packingListData = new ArrayList<>();

    public Order() {

    }

    public Order(Long orderId, Date Date, String orderNo, String portal, String portalOrderNo, String portalOrderLineId,
            String portalSKU, String sellerSKU, String productDescription, double qty, Date shipByDate, String dispatched,
            String courier, String cancel, String awbNo, String orderStatus) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.portal = portal;
        this.portalOrderNo = portalOrderNo;
        this.portalOrderLineId = portalOrderLineId;
        this.portalSKU = portalSKU;
        this.sellerSKU = sellerSKU;
        this.productDescription = productDescription;
        this.qty = qty;
        this.shipByDate = shipByDate;
        this.dispatched = dispatched;
        this.courier = courier;
        this.Date = Date;
        this.cancel = cancel;
        this.orderStatus = orderStatus;
        this.awbNo = awbNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    // public List<OrderItem> getOrderItems() {
    // return orderItems;
    // }

    // public void setOrderItems(List<OrderItem> orderItems) {
    // this.orderItems = orderItems;
    // }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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

    public String getPortalOrderNo() {
        return portalOrderNo;
    }

    public void setPortalOrderNo(String portalOrderNo) {
        this.portalOrderNo = portalOrderNo;
    }

    public String getPortalOrderLineId() {
        return portalOrderLineId;
    }

    public void setPortalOrderLineId(String portalOrderLineId) {
        this.portalOrderLineId = portalOrderLineId;
    }

    public String getPortalSKU() {
        return portalSKU;
    }

    public void setPortalSKU(String portalSKU) {
        this.portalSKU = portalSKU;
    }

    public String getSellerSKU() {
        return sellerSKU;
    }

    public void setSellerSKU(String sellerSKU) {
        this.sellerSKU = sellerSKU;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public Date getShipByDate() {
        return shipByDate;
    }

    public void setShipByDate(Date shipByDate) {
        this.shipByDate = shipByDate;
    }

    public String getDispatched() {
        return dispatched;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public void setDispatched(String dispatched) {
        this.dispatched = dispatched;
    }

    public String getCourier() {
        return courier;
    }

    public void setCourier(String courier) {
        this.courier = courier;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public PickList getPicklist() {
        return picklist;
    }

    public void setPicklist(PickList picklist) {
        this.picklist = picklist;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAwbNo() {
        return awbNo;
    }

    public void setAwbNo(String awbNo) {
        this.awbNo = awbNo;
    }

    public ItemPortalMapping getItemPortalMapping() {
        return itemPortalMapping;
    }

    public void setItemPortalMapping(ItemPortalMapping itemPortalMapping) {
        this.itemPortalMapping = itemPortalMapping;
    }

    public List<PickListData> getPickListData() {
        return pickListData;
    }

    public void setPickListData(List<PickListData> pickListData) {
        this.pickListData = pickListData;
    }

    public List<PackingListData> getPackingListData() {
        return packingListData;
    }

    public void setPackingListData(List<PackingListData> packingListData) {
        this.packingListData = packingListData;
    }
    
}
