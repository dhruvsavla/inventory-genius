package com.example.inventorygenius.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
public class Return {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long returnId;

        @Column(name = "date")
        private Date date;

        @Column(name = "skucode")
        private String skucode;

        @Column(name = "portal")
        private String portal;

        @Column(name = "orderNo")
        private String orderNo;

        @Column(name = "returnCode")
        private String returnCode;

        @Column(name = "trackingNumber")
        private String trackingNumber;

        @Column(name = "okStock")
        private String okStock;

        @Column(name = "sentForRaisingTicketOn")
        private String sentForRaisingTicketOn;

        @Column(name = "sentForTicketOn")
        private String sentForTicketOn;
    
        @OneToOne(mappedBy = "order")
        private Item item;
    
    
        public Return() {
        }
    
        public Return(Date date, String skuCode, String portal, String orderNo, String returnCode, String trackingNumber, String okStock, String sentForRaisingTicketOn, String sentForTicketOn, Item item) {
            this.date = date;
            this.skucode = skuCode;
            this.portal = portal;
            this.orderNo = orderNo;
            this.returnCode = returnCode;
            this.trackingNumber = trackingNumber;
            this.okStock = okStock;
            this.sentForRaisingTicketOn = sentForRaisingTicketOn;
            this.sentForTicketOn = sentForTicketOn;
            this.item = item;
        }

        public Long getId() {
            return returnId;
        }

        public void setId(Long id) {
            this.returnId = id;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getSkucode() {
            return skucode;
        }

        public void setSkucode(String skucode) {
            this.skucode = skucode;
        }

        public String getPortal() {
            return portal;
        }

        public void setPortal(String portal) {
            this.portal = portal;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getReturnCode() {
            return returnCode;
        }

        public void setReturnCode(String returnCode) {
            this.returnCode = returnCode;
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getOkStock() {
            return okStock;
        }

        public void setOkStock(String okStock) {
            this.okStock = okStock;
        }

        public String getSentForRaisingTicketOn() {
            return sentForRaisingTicketOn;
        }

        public void setSentForRaisingTicketOn(String sentForRaisingTicketOn) {
            this.sentForRaisingTicketOn = sentForRaisingTicketOn;
        }

        public String getSentForTicketOn() {
            return sentForTicketOn;
        }

        public void setSentForTicketOn(String sentForTicketOn) {
            this.sentForTicketOn = sentForTicketOn;
        }

        public Item getItem() {
            return item;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public Long getReturnId() {
            return returnId;
        }

        public void setReturnId(Long returnId) {
            this.returnId = returnId;
        }
    
        
    
    
    


}
