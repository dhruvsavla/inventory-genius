package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.PackingListData;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.OrderData;
import com.example.inventorygenius.entity.PackingList;
import com.example.inventorygenius.entity.PickList;
import com.example.inventorygenius.entity.BomItem;
import com.example.inventorygenius.entity.PickListData;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.service.ItemSupplierService;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.PackingListService;

import com.example.inventorygenius.service.PackingListDataService;
import com.example.inventorygenius.service.PickListService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/packinglist")
public class PackingListController {

    @Autowired
    private PackingListService packingListService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PickListService pickListService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    @Autowired
    private PackingListDataService packingListDataService;

    @GetMapping("/not/generated/packinglist/orders")
    public List<Order> getNotGeneratedPackingListOrders() {
        return packingListService.getAllNotGeneratedPackListOrders();
    }

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
        for (Order order : pickList.getOrders()){
            order.setOrderStatus("packinglist generated");
            orderService.updateOrder(order.getOrderId(), order); 
        }
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

    // @GetMapping("/orderData")
    // public List<OrderData> getOrdersData() {
    //     List<PickList> pickLists = pickListService.getAllPickLists(); // Get orders from service/repository
    //     List<OrderData> orderDataList = new ArrayList<>(); // List to hold order data
    //     int totalQty = 0;

    //     for (PickList pickList : pickLists) {
    //         for (Order order : pickList.getOrders()){
    //         Boolean generatedOrder = generated(order);
    //         if (!generatedOrder && !order.getCancel().equals("Order Canceled")){
    //             if (order.getItems() != null) {
    //                 OrderData orderData = new OrderData();

    //                 for (Item item : order.getItems()) {
    //                     orderData.setDate(order.getDate());
    //                     orderData.setOrderNo(order.getOrderNo());
    //                     orderData.setPortal(order.getPortal());
    //                     orderData.setSellerSKU(item.getSellerSKUCode());
    //                     orderData.setImg(item.getImg());
    //                     if (item.getBoms().size() > 0){
    //                     for (Bom bom : item.getBoms()){
    //                         for (BomItem bomItem : bom.getItemsInBom()) {
    //                         if (bomItem.getBomItem().equals(item.getParentSKU())) {
    //                             orderData.setPickQty(order.getQty() * Double.parseDouble(bomItem.getQty()));
    //                             totalQty += order.getQty() * Double.parseDouble(bomItem.getQty());
    //                         }
    //                         else if (!bomItem.getBomItem().equals(item.getParentSKU())){
    //                             OrderData o = new OrderData();
    //                             Item i = itemSupplierService.getItemBySKUCode(bomItem.getBomItem());
    //                             o.setDate(order.getDate());
    //                             o.setOrderNo(order.getOrderNo());
    //                             o.setPortal(order.getPortal());
    //                             o.setSellerSKU(i.getSellerSKUCode());
    //                             o.setImg(i.getImg());
    //                             totalQty += order.getQty() * Double.parseDouble(bomItem.getQty());
    //                             o.setPickQty(order.getQty() * Double.parseDouble(bomItem.getQty()));
    //                             if (getBestStorage(i, totalQty).size() > 0){
    //                                 String bin = "";
    //                                 String rack = "";
    //                                 for (Storage s : getBestStorage(i, totalQty)){
    //                                     System.out.println(s.getSkucode() + "-" + "bin = " + s.getBinNumber() + " rack = "+ s.getRackNumber() + "qty = " + s.getQty());
    //                                     bin += s.getBinNumber() + ",";
    //                                     rack += s.getRackNumber() + ",";
    //                                 }
    //                                 o.setBinNumber(bin);
    //                                 o.setRackNumber(rack);
    //                             }
    //                             else {
    //                                 o.setBinNumber("NA");
    //                                 o.setRackNumber("NA");
    //                             }
                            
    //                             o.setQty(order.getQty());
    //                             o.setDescription(i.getDescription());
    //                             orderDataList.add(o);
    //                             //totalQty = 0;
    //                         }
                            
    //                     }
    //                 }
    //                 }
    //                 else {
    //                     orderData.setPickQty(order.getQty());
    //                     totalQty += order.getQty();
    //                 }

    //                     if (getBestStorage(item, totalQty).size() > 0){
    //                         String bin = "";
    //                         String rack = "";
    //                         for (Storage s : getBestStorage(item, totalQty)){
    //                             System.out.println(s.getSkucode() + "-" + "bin = " + s.getBinNumber() + " rack = "+ s.getRackNumber() + "qty = " + s.getQty());
    //                             bin += s.getBinNumber() + ",";
    //                             rack += s.getRackNumber() + ",";
    //                         }
    //                         orderData.setBinNumber(bin);
    //                         orderData.setRackNumber(rack);
    //                     }
    //                     else {
    //                         orderData.setBinNumber("NA");
    //                         orderData.setRackNumber("NA");
    //                     }
                        
                        
    //                 }
    //                 orderData.setQty(order.getQty());
    //                     orderData.setDescription(order.getProductDescription());
    //                     orderDataList.add(orderData);

    //                     totalQty = 0;

    //             }
    //         }
    //     }
    //     }

    //     return orderDataList;
    // }

    @GetMapping("/orderData")
    public List<OrderData> getOrdersData() {
        List <PickListData> pickListDatas = pickListService.getData();
        List<OrderData> orderDataList = new ArrayList<>(); // List to hold order data

        for (PickListData p : pickListDatas){
            OrderData o = new OrderData();
            o.setOrderNo(p.getOrderNo());
            o.setDescription(p.getDescription());
            o.setDate(p.getDate());
            o.setPortal(p.getPortal());
            o.setQty(p.getQty());
            o.setPickQty(p.getPickQty());
            orderDataList.add(o);
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
    public List<PackingListData> getData() {
        return packingListDataService.getAllPackingListData();
    }

    @GetMapping("/selected/orderData")
    public List<OrderData> getSelectedOrderData(@RequestParam String orderNo) {
        List<OrderData> selectedOrderData = new ArrayList<>();
        for (OrderData o : getOrdersData()){
            if(o.getOrderNo().equals(orderNo)){
                selectedOrderData.add(o);
            }
        }
        return selectedOrderData;
    }
    
}

