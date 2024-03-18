package com.example.inventorygenius.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class PickList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picklist_id")
    private Long orderId;

    @Column(name = "date")
    private Date date;

    @Column(name = "seller_sku")
    private String sellerSku;

    @Column(name = "rack_no")
    private int rackNo;

    @Column(name = "bin_no")
    private int binNo;

    @Column(name = "qty")
    private double qty;

    @Column(name = "portal")
    private String portal;
}
