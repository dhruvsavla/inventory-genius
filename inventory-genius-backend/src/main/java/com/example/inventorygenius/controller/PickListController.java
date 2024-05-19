package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.OrderData;
import com.example.inventorygenius.entity.PickList;
import com.example.inventorygenius.entity.PickListData;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.repository.PickListRepository;
import com.example.inventorygenius.service.BomService;
import com.example.inventorygenius.service.ItemSupplierService;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.PickListService;
import com.example.inventorygenius.service.StockCountService;
import com.example.inventorygenius.service.StockService;
import com.example.inventorygenius.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/picklists")
public class PickListController {

    @Autowired
    private PickListService pickListService;

    @Autowired
    private StockService stockService;

    @Autowired
    private PickListRepository pickListRepository;

    @Autowired
    private StockCountService stockCountService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BomService bomService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    @Autowired StorageService storageService;

    @GetMapping
    public ResponseEntity<List<PickList>> getAllPickLists() {
        List<PickList> pickLists = pickListService.getAllPickLists();
        return ResponseEntity.ok(pickLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PickList> getPickListById(@PathVariable Long id) {
        Optional<PickList> pickList = pickListService.getPickListById(id);
        return pickList.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
public ResponseEntity<PickList> createPickList(@RequestBody PickList pickList) {

     PickList createdPickList = pickListService.createPickList(pickList);
    
     for (Order order : pickList.getOrders()){    
        Stock stock = new Stock();
        Item item = itemSupplierService.getItemBySKUCode(order.getItems().get(0).getSKUCode());
        if(item.getBoms().size() > 0){
            stock.setDate(new Date());
            stock.setSkucode(item.getParentSKU());
            stock.setAddQty("0");
            for (Bom bom : item.getBoms()){
                if (bom.getBomItem().equals(item.getParentSKU())){
                    stock.setSubQty(String.valueOf(order.getQty() * bom.getQty()));
                }
                if (!bom.getBomItem().equals(item.getParentSKU())){
                    Stock s = new Stock();
                    s.setDate(new Date());
                    s.setAddQty("0");
                    s.setItem(item);
                    s.setSubQty(String.valueOf(order.getQty() * bom.getQty()));
                    s.setSkucode(bom.getBomItem());
                    s.setSource("picklist/order");
                    s.setMessage("pickList generated for order");
                    s.setNumber("pickList Number = " + pickList.getPickListNumber() + " order no = " + String.valueOf(order.getOrderNo()));
                    stockService.addStock(s);
                }
            }
            
            stock.setItem(order.getItems().get(0));

            stock.setSource("picklist/order");
            stock.setMessage("pickList generated for order");
            stock.setNumber("pickList Number = " + pickList.getPickListNumber() + " order no = " + String.valueOf(order.getOrderNo()));
        }
        else {
            stock.setDate(new Date());
            stock.setSkucode(order.getItems().get(0).getSKUCode());
            stock.setAddQty("0");
            stock.setSubQty(String.valueOf(order.getQty()));
            stock.setItem(order.getItems().get(0));
    
            stock.setSource("picklist/order");
            stock.setMessage("pickList generated for order");
            stock.setNumber("pickList Number = " + pickList.getPickListNumber() + " order no = " + String.valueOf(order.getOrderNo()));
        }        

        stockService.addStock(stock);

        String skuCode = "";
        double additionalCount = 0;

        if (order.getItems().get(0).getBoms().size() > 0){
            skuCode += order.getItems().get(0).getParentSKU();
            for (Bom bom : order.getItems().get(0).getBoms()){
                if (bom.getBomItem().equals(order.getItems().get(0).getParentSKU())){
                    additionalCount = Double.parseDouble(String.valueOf(order.getQty())) * bom.getQty();
                }
                if (!(bom.getBomItem().equals(order.getItems().get(0).getParentSKU()))){
                    StockCount scBom = new StockCount();
                    scBom = stockCountService.getStockCountBySKUCode(bom.getBomItem());

                    Double c = Double.parseDouble(String.valueOf(order.getQty())) * bom.getQty();
                    double currentCount = scBom.getCount();
                    scBom.setCount(currentCount - c);

                    stockCountService.updateStockCount(scBom);
                }
            }
        }
        else {
            skuCode += order.getItems().get(0).getSKUCode();
            additionalCount = Double.parseDouble(String.valueOf(order.getQty()));
        }
        StockCount stockCount = stockCountService.getStockCountBySKUCode(skuCode);

        double currentCount = stockCount.getCount();
        stockCount.setCount(currentCount - additionalCount);

        stockCountService.updateStockCount(stockCount);
     }

    // // Return a ResponseEntity with the original pickList
     return ResponseEntity.status(HttpStatus.CREATED).body(createdPickList);
}



    @PutMapping("/{id}")
    public ResponseEntity<PickList> updatePickList(@PathVariable Long id, @RequestBody PickList pickList) {
        PickList updatedPickList = pickListService.updatePickList(id, pickList);
        return ResponseEntity.ok(updatedPickList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePickList(@PathVariable Long id) {

        Optional<PickList> pickListOptional = pickListRepository.findById(id);

        PickList pickList = pickListOptional.orElseThrow(() -> new IllegalArgumentException("PickList not found with id: " + id));

        for (Order order : pickList.getOrders()){    
            Stock stock = new Stock();
            Item item = itemSupplierService.getItemBySKUCode(order.getItems().get(0).getSKUCode());
            if(item.getBoms().size() > 0){
                stock.setDate(new Date());
                stock.setSkucode(item.getParentSKU());
                stock.setSubQty("0");
                for (Bom bom : item.getBoms()){
                    if (bom.getBomItem().equals(item.getParentSKU())){
                        stock.setAddQty(String.valueOf(order.getQty() * bom.getQty()));
                    }
                    if (!bom.getBomItem().equals(item.getParentSKU())){
                        Stock s = new Stock();
                        s.setDate(new Date());
                        s.setSubQty("0");
                        s.setItem(item);
                        s.setAddQty(String.valueOf(order.getQty() * bom.getQty()));
                        s.setSkucode(bom.getBomItem());
                        s.setSource("picklist/order");
                        s.setMessage("pickList deleted for order");
                        s.setNumber("pickList Number = " + pickList.getPickListNumber() + " order no = " + String.valueOf(order.getOrderNo()));
                        stockService.addStock(s);
                    }
                }
                
                stock.setItem(order.getItems().get(0));
    
                stock.setSource("picklist/order");
                stock.setMessage("pickList deleted for order");
                stock.setNumber("pickList Number = " + pickList.getPickListNumber() + " order no = " + String.valueOf(order.getOrderNo()));
            }
            else {
                stock.setDate(new Date());
                stock.setSkucode(order.getItems().get(0).getSKUCode());
                stock.setSubQty("0");
                stock.setAddQty(String.valueOf(order.getQty()));
                stock.setItem(order.getItems().get(0));
        
                stock.setSource("picklist/order");
                stock.setMessage("pickList deleted for order");
                stock.setNumber("pickList Number = " + pickList.getPickListNumber() + " order no = " + String.valueOf(order.getOrderNo()));
            }        
    
            stockService.addStock(stock);

            String skuCode = "";
            double additionalCount = 0;
    
            if (order.getItems().get(0).getBoms().size() > 0){
                skuCode += order.getItems().get(0).getParentSKU();
                for (Bom bom : order.getItems().get(0).getBoms()){
                    if (bom.getBomItem().equals(order.getItems().get(0).getParentSKU())){
                        additionalCount = Double.parseDouble(String.valueOf(order.getQty())) * bom.getQty();
                    }
                }
            }
            else {
                skuCode += order.getItems().get(0).getSKUCode();
                additionalCount = Double.parseDouble(String.valueOf(order.getQty()));
            }
            StockCount stockCount = stockCountService.getStockCountBySKUCode(skuCode);
    
            double currentCount = stockCount.getCount();
            stockCount.setCount(currentCount + additionalCount);
    
            stockCountService.updateStockCount(stockCount);
        }

        pickListService.deletePickList(id);

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
                    for (Item item : order.getItems()) {
                        OrderData orderData = new OrderData();
                        orderData.setDate(order.getDate());
                        orderData.setOrderNo(order.getOrderNo());
                        orderData.setPortal(order.getPortal());
                        orderData.setSellerSKU(item.getSellerSKUCode());
                        orderData.setDescription(order.getProductDescription());
                        orderData.setImg(item.getImg());
                        if (item.getBoms().size() > 0){
                            System.out.println("size = " + item.getBoms().size());
                        for (Bom bom : item.getBoms()){
                            if (bom.getSKUCode().equals(item.getSKUCode()) && bom.getBomItem().equals(item.getParentSKU())) {
                                orderData.setPickQty(order.getQty() * bom.getQty());
                                System.out.println("inside if");
                                totalQty += order.getQty() * bom.getQty();
                                System.out.println(itemSupplierService.getItemBySKUCode(item.getParentSKU()).getSellerSKUCode());

                                orderData.setSellerSKU(itemSupplierService.getItemBySKUCode(item.getParentSKU()).getSellerSKUCode());
                                orderData.setDescription(itemSupplierService.getItemBySKUCode(item.getParentSKU()).getDescription());
                                
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
                                        bin += s.getBinNumber();
                                        rack += s.getRackNumber();
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
                            bin += s.getBinNumber();
                            rack += s.getRackNumber();
                        }
                        orderData.setBinNumber(bin);
                        orderData.setRackNumber(rack);
                    }
                    else {
                        orderData.setBinNumber("NA");
                        orderData.setRackNumber("NA");
                    }
                    
                    orderData.setQty(order.getQty());
                    orderDataList.add(orderData);
                        totalQty = 0;
                }

                }
            }
        }

        return orderDataList;
    }

    public List<Storage> getBestStorage (Item item, int totalQty){
        List<Storage> storages = new ArrayList<>();
        int sum = 0;
        if(item.getBoms().size() > 0){
            item = itemSupplierService.getItemBySKUCode(item.getParentSKU());
        }
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
        for (PickList pickList : pickListService.getAllPickLists()){
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
        List<PickList> pickLists = pickListService.getAllPickLists();
        List<PickListData> pickListDataList = new ArrayList<>();
        int totalQty = 0;
        for (PickList p : pickLists){
            for (Order order : p.getOrders()){
                PickListData pickListData = new PickListData();
                pickListData.setPickListNumber(p.getPickListNumber());
                System.out.println("portal order number = " + order.getPortalOrderNo());
                    if (order.getItems() != null) {
                        for (Item item : order.getItems()) {
                            pickListData.setDate(order.getDate());
                            pickListData.setOrderNo(order.getOrderNo());
                            pickListData.setPortal(order.getPortal());
                            pickListData.setSellerSKU(item.getSellerSKUCode());
                            pickListData.setPortalOrderNo(order.getPortalOrderNo());
                            pickListData.setPickListId(p.getPicklistId());

                            if (item.getBoms().size() > 0){
                            for (Bom bom : item.getBoms()){
                                if (bom.getSKUCode().equals(item.getSKUCode()) && bom.getBomItem().equals(item.getParentSKU())) {
                                    pickListData.setPickQty(order.getQty() * bom.getQty());
                                    totalQty += order.getQty() * bom.getQty();
                                    pickListData.setDate(order.getDate());
                                    pickListData.setOrderNo(order.getOrderNo());
                                    pickListData.setPortal(order.getPortal());
                                    Item it = itemSupplierService.getItemBySKUCode(item.getParentSKU());
                                    pickListData.setSellerSKU(it.getSellerSKUCode());
                                    pickListData.setPortalOrderNo(order.getPortalOrderNo());
                                    pickListData.setPickListId(p.getPicklistId());
                                }
                                else if (bom.getSKUCode().equals(item.getSKUCode()) && !bom.getBomItem().equals(item.getParentSKU())){
                                    PickListData o = new PickListData();
                                    Item i = itemSupplierService.getItemBySKUCode(bom.getBomItem());
                                    o.setPickListNumber(p.getPickListNumber());
                                    o.setDate(order.getDate());
                                    o.setOrderNo(order.getOrderNo());
                                    o.setPortal(order.getPortal());
                                    o.setSellerSKU(i.getSellerSKUCode());
                                    o.setPortalOrderNo(order.getPortalOrderNo());
                                    o.setPickListId(p.getPicklistId());
                                    totalQty += order.getQty() * bom.getQty();
                                    o.setPickQty(order.getQty() * bom.getQty());
                                    if (getBestStorage(i, totalQty).size() > 0){
                                        String bin = "";
                                        String rack = "";
                                        for (Storage s : getBestStorage(i, totalQty)){
                                            bin += s.getBinNumber();
                                            rack += s.getRackNumber();
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
                                    bin += s.getBinNumber();
                                    rack += s.getRackNumber();
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


    @GetMapping("/getSelectedOrderData")
    public List<OrderData> getMethodName(@RequestParam String orderNo) {
        List<OrderData> oo = getOrdersData();
        List<OrderData> orderDataList = new ArrayList<>();
        for (OrderData o : oo){
            if (o.getOrderNo().equals(orderNo)){
                orderDataList.add(o);
            }
        }
        return orderDataList;
    }


    @GetMapping("/merged/picklist")
    public List<PickListData> mergedPickListDatas() {
        List<PickListData> allPickListDatas = getData();
        List<PickListData> mergedPickListData = new ArrayList<>();
    
        // Map to store PickListData grouped by picklistNumber
        Map<Long, List<PickListData>> groupedByPicklistNumber = new HashMap<>();
    
        // Group PickListData by picklistNumber
        for (PickListData p : allPickListDatas) {
            groupedByPicklistNumber
                .computeIfAbsent(p.getPickListNumber(), k -> new ArrayList<>())
                .add(p);
        }
    
        // Iterate over groups
        for (List<PickListData> group : groupedByPicklistNumber.values()) {
            // Map to store aggregated qty and pickQty by sellerSKU
            Map<String, PickListData> aggregatedDataBySellerSKU = new HashMap<>();
    
            // Aggregate qty and pickQty for each sellerSKU within the group
            for (PickListData p : group) {
                String sellerSKU = p.getSellerSKU();
                double qty = p.getQty();
                double pickQty = p.getPickQty();
                Date date = new Date();
                String description = p.getDescription();
                String bin = p.getBinNumber();
                String rack = p.getRackNumber();
                Long pickListId = p.getPickListId();
    
                if (aggregatedDataBySellerSKU.containsKey(sellerSKU)) {
                    PickListData existingData = aggregatedDataBySellerSKU.get(sellerSKU);
                    existingData.setQty(existingData.getQty() + qty);
                    existingData.setPickQty(existingData.getPickQty() + pickQty);
                } else {
                    // Create a new PickListData if sellerSKU not found
                    PickListData newData = new PickListData();
                    newData.setPickListNumber(p.getPickListNumber());
                    newData.setSellerSKU(sellerSKU);
                    newData.setDate(date);
                    newData.setDescription(description);
                    newData.setQty(qty);
                    newData.setBinNumber(bin);
                    newData.setRackNumber(rack);
                    newData.setPickQty(pickQty);
                    newData.setPickListId(pickListId);
                    aggregatedDataBySellerSKU.put(sellerSKU, newData);
                }
            }
    
            // Add aggregated data to mergedPickListData
            mergedPickListData.addAll(aggregatedDataBySellerSKU.values());
        }
    
        return mergedPickListData;
    }    

}

