package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.OrderData;
import com.example.inventorygenius.entity.PackingList;
import com.example.inventorygenius.entity.PickList;
import com.example.inventorygenius.entity.PickListData;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.service.ItemSupplierService;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.PackingListService;
import com.example.inventorygenius.service.PickListService;

@RestController
@RequestMapping("/packinglist")
public class PackingListController {

    @Autowired
    private PackingListService packingListService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    @GetMapping
    public ResponseEntity<List<PackingList>> getAllPickLists() {
        List<PackingList> pickLists = packingListService.getAllPickLists();
        return ResponseEntity.ok(pickLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackingList> getPickListById(@PathVariable Long id) {
        Optional<PackingList> pickList = packingListService.getPickListById(id);
        return pickList.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PackingList> createPickList(@RequestBody PackingList pickList) {
        PackingList createdPickList = packingListService.createPickList(pickList);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPickList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackingList> updatePickList(@PathVariable Long id, @RequestBody PackingList pickList) {
        PackingList updatedPickList = packingListService.updatePickList(id, pickList);
        return ResponseEntity.ok(updatedPickList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePickList(@PathVariable Long id) {
        packingListService.deletePickList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orderData")
    public List<OrderData> getOrdersData() {
        List<Order> orders = orderService.getAllOrders(); // Get orders from service/repository
        List<OrderData> orderDataList = new ArrayList<>(); // List to hold order data
        int totalQty = 0;

        for (Order order : orders) {
            Boolean generatedOrder = generated(order);
            if (!generatedOrder){
                if (order.getItems() != null) {
                    OrderData orderData = new OrderData();

                    for (Item item : order.getItems()) {
                        orderData.setDate(order.getDate());
                        orderData.setOrderNo(order.getOrderNo());
                        orderData.setPortal(order.getPortal());
                        orderData.setSellerSKU(item.getSellerSKUCode());
                        orderData.setImg(item.getImg());
                        if (item.getBoms().size() > 0){
                        for (Bom bom : item.getBoms()){
                            if (bom.getSKUCode().equals(item.getSKUCode()) && bom.getBomItem().equals(item.getParentSKU())) {
                                orderData.setPickQty(order.getQty() * bom.getQty());
                                totalQty += order.getQty() * bom.getQty();
                            }
                            else if (bom.getSKUCode().equals(item.getSKUCode()) && !bom.getBomItem().equals(item.getParentSKU())){
                                OrderData o = new OrderData();
                                Item i = itemSupplierService.getItemBySKUCode(bom.getBomItem());
                                o.setDate(order.getDate());
                                o.setOrderNo(order.getOrderNo());
                                o.setPortal(order.getPortal());
                                o.setSellerSKU(i.getSellerSKUCode());
                                o.setImg(i.getImg());
                                totalQty += order.getQty() * bom.getQty();
                                o.setPickQty(order.getQty() * bom.getQty());
                                if (getBestStorage(i, totalQty).size() > 0){
                                    String bin = "";
                                    String rack = "";
                                    for (Storage s : getBestStorage(i, totalQty)){
                                        System.out.println(s.getSkucode() + "-" + "bin = " + s.getBinNumber() + " rack = "+ s.getRackNumber() + "qty = " + s.getQty());
                                        bin += s.getBinNumber() + ",";
                                        rack += s.getRackNumber() + ",";
                                    }
                                    o.setBinNumber(bin);
                                    o.setRackNumber(rack);
                                }
                                else {
                                    o.setBinNumber("NA");
                                    o.setRackNumber("NA");
                                }
                            
                                o.setQty(order.getQty());
                                o.setDescription(i.getDescription());
                                orderDataList.add(o);
                                //totalQty = 0;
                            }
                            
                        }
                    }
                    else {
                        orderData.setPickQty(order.getQty());
                        totalQty += order.getQty();
                    }

                        if (getBestStorage(item, totalQty).size() > 0){
                            String bin = "";
                            String rack = "";
                            for (Storage s : getBestStorage(item, totalQty)){
                                System.out.println(s.getSkucode() + "-" + "bin = " + s.getBinNumber() + " rack = "+ s.getRackNumber() + "qty = " + s.getQty());
                                bin += s.getBinNumber() + ",";
                                rack += s.getRackNumber() + ",";
                            }
                            orderData.setBinNumber(bin);
                            orderData.setRackNumber(rack);
                        }
                        else {
                            orderData.setBinNumber("NA");
                            orderData.setRackNumber("NA");
                        }
                        
                        
                    }
                    orderData.setQty(order.getQty());
                        orderData.setDescription(order.getProductDescription());
                        orderDataList.add(orderData);

                        totalQty = 0;

                }
            }
        }

        return orderDataList;
    }

    public List<Storage> getBestStorage (Item item, int totalQty){
        List<Storage> storages = new ArrayList<>();
        int sum = 0;
        for (Storage storage : item.getStorages()){
            if (Integer.parseInt(storage.getQty()) >= totalQty){
                storages.clear();
                storages.add(storage);
                return storages;
            }
            else {
                storages.add(storage);
                sum += Integer.parseInt(storage.getQty());
                if (sum >= totalQty){
                    return storages;
                }
            }
        }
        return storages;
    }

    public boolean generated(Order order){
        for (PackingList pickList : packingListService.getAllPickLists()){
            for (Order o : pickList.getOrders()){
                if (o.getOrderNo().equals(order.getOrderNo())){
                    return true;
                }
            }
        }
        return false;
    }

    @GetMapping("/getData")
    public List<PickListData> getData() {
        List<PackingList> pickLists = packingListService.getAllPickLists();
        List<PickListData> pickListDataList = new ArrayList<>();
        int totalQty = 0;

        for (PackingList p : pickLists){
            for (Order order : p.getOrders()){
                PickListData pickListData = new PickListData();
                pickListData.setPickListNumber(p.getPackingListNumber());
                    if (order.getItems() != null) {
                        for (Item item : order.getItems()) {
                            pickListData.setDate(order.getDate());
                            pickListData.setOrderNo(order.getOrderNo());
                            pickListData.setPortal(order.getPortal());
                            pickListData.setSellerSKU(item.getSellerSKUCode());
                            pickListData.setPortalOrderNo(order.getPortalOrderNo());
                            pickListData.setPickListId(p.getPackinglistId());
                            if (item.getBoms().size() > 0){
                            for (Bom bom : item.getBoms()){
                                if (bom.getSKUCode().equals(item.getSKUCode()) && bom.getBomItem().equals(item.getParentSKU())) {
                                    pickListData.setPickQty(order.getQty() * bom.getQty());
                                    totalQty += order.getQty() * bom.getQty();
                                }
                                else if (bom.getSKUCode().equals(item.getSKUCode()) && !bom.getBomItem().equals(item.getParentSKU())){
                                    PickListData o = new PickListData();
                                    Item i = itemSupplierService.getItemBySKUCode(bom.getBomItem());
                                    o.setPickListNumber(p.getPackingListNumber());
                                    o.setDate(order.getDate());
                                    o.setOrderNo(order.getOrderNo());
                                    o.setPortal(order.getPortal());
                                    o.setSellerSKU(i.getSellerSKUCode());
                                    o.setPortalOrderNo(order.getPortalOrderNo());
                                    o.setPickListId(p.getPackinglistId());
                                    totalQty += order.getQty() * bom.getQty();
                                    o.setPickQty(order.getQty() * bom.getQty());
                                    if (getBestStorage(i, totalQty).size() > 0){
                                        String bin = "";
                                        String rack = "";
                                        for (Storage s : getBestStorage(i, totalQty)){
                                            System.out.println(s.getSkucode() + "-" + "bin = " + s.getBinNumber() + " rack = "+ s.getRackNumber() + "qty = " + s.getQty());
                                            bin += s.getBinNumber() + ",";
                                            rack += s.getRackNumber() + ",";
                                        }
                                        o.setBinNumber(bin);
                                        o.setRackNumber(rack);
                                    }
                                    else {
                                        o.setBinNumber("NA");
                                        o.setRackNumber("NA");
                                    }
                                
                                    o.setQty(order.getQty());
                                    o.setDescription(i.getDescription());
                                    pickListDataList.add(o);
                                    //totalQty = 0;
                                }
                                
                            }
                        }
                        else {
                            pickListData.setPickQty(order.getQty());
                            totalQty += order.getQty();
                        }
    
                            if (getBestStorage(item, totalQty).size() > 0){
                                String bin = "";
                                String rack = "";
                                for (Storage s : getBestStorage(item, totalQty)){
                                    System.out.println(s.getSkucode() + "-" + "bin = " + s.getBinNumber() + " rack = "+ s.getRackNumber() + "qty = " + s.getQty());
                                    bin += s.getBinNumber() + ",";
                                    rack += s.getRackNumber() + ",";
                                }
                                pickListData.setBinNumber(bin);
                                pickListData.setRackNumber(rack);
                            }
                            else {
                                pickListData.setBinNumber("NA");
                                pickListData.setRackNumber("NA");
                            }
                            
                            pickListData.setQty(order.getQty());
                            pickListData.setDescription(order.getProductDescription());
                            pickListDataList.add(pickListData);
        
                            totalQty = 0; 
                        }
                }
                
            }
            
        }
        return pickListDataList;
    }

}

