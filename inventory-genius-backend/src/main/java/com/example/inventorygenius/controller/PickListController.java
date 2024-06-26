package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.inventorygenius.Exception.CustomStockException;
import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.OrderData;
import com.example.inventorygenius.entity.PickList;
import com.example.inventorygenius.entity.PickListData;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.entity.BomItem;

import com.example.inventorygenius.repository.PickListRepository;
import com.example.inventorygenius.service.BomService;
import com.example.inventorygenius.service.ItemSupplierService;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.PickListDataService;
import com.example.inventorygenius.service.PickListService;
import com.example.inventorygenius.service.StockCountService;
import com.example.inventorygenius.service.StockService;
import com.example.inventorygenius.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import com.example.inventorygenius.Exception.CustomStockException;


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

    @Autowired 
    private StorageService storageService;

    @Autowired 
    private PickListDataService pickListDataService;

    @GetMapping("/not/generated/orders")
    public List<Order> getNotGeneratedOrders() {
        return pickListService.getAllNotGeneratedOrders();
    } 

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
    @Transactional()
public ResponseEntity<PickList> createPickList(@RequestBody PickList pickList) {

     PickList createdPickList = pickListService.createPickList(pickList);

     return ResponseEntity.status(HttpStatus.CREATED).body(createdPickList);
}

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletePickList(@PathVariable Long id) {

        Optional<PickList> pickListOptional = pickListRepository.findById(id);

        PickList pickList = pickListOptional.orElseThrow(() -> new IllegalArgumentException("PickList not found with id: " + id));

        pickListService.deletePickList(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orderData")
    public List<OrderData> getOrdersData() {
        return pickListService.getOrderDatas();
    }


    @GetMapping("/getData")
    public List<PickListData> getData() {
        return pickListService.getData();
    }
    
    public boolean isCurrentDateBetween(Bom bom) {
        LocalDate currentDate = LocalDate.now();
    
        Date startDate = bom.getDefaultStartDate();
        Date endDate = bom.getDefaultEndDate();
    
        LocalDate localStartDate = startDate != null ? convertToLocalDate(startDate) : null;
        LocalDate localEndDate = endDate != null ? convertToLocalDate(endDate) : null;
    
        if (localStartDate != null && localEndDate != null) {
            return (currentDate.isEqual(localStartDate) || currentDate.isAfter(localStartDate)) &&
                   (currentDate.isEqual(localEndDate) || currentDate.isBefore(localEndDate));
        } else if (localStartDate != null) {
            return currentDate.isEqual(localStartDate) || currentDate.isAfter(localStartDate);
        } else if (localEndDate != null) {
            return currentDate.isEqual(localEndDate) || currentDate.isBefore(localEndDate);
        } else {
            return false; // Or handle the case where both dates are null if necessary
        }
    }
    
    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }
    

    @GetMapping("/getSelectedOrderData")
    public List<OrderData> getMethodName(@RequestParam String orderNo, @RequestParam String bomCode) {
       String bomC = "";
        List<Order> orders = orderService.findByOrderNo(orderNo);
       for(Order order : orders){
            for(Item item : order.getItems()){
                for (Bom bom : item.getBoms()){
                    if (item.getBoms().size() > 0 && bomCode.equals("")){
                        throw new IllegalArgumentException("Select a bomCode");
                    }
                    if(item.getBoms().size() > 0){
                        
                        
                       
                            bomC = bomCode;
                        
                    }
                    if (item.getBoms().size() == 0) {
                        bomC = "";
                    }
                    
                }
            }
       }
       System.out.println("bom in picklist merge rows = " + bomC);
       List<OrderData> oo = new ArrayList<>();
       if(bomC.length() > 0){
        Bom bom = bomService.getBomByBomCode(bomC);
        System.out.println("calling orderData with bom");
        oo = pickListService.getOrderData(bom);
       }
       else {
        System.out.println("calling orderDatas");
        oo = pickListService.getOrderDatas();
       }
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
        List<PickListData> allPickListDatas = pickListDataService.getAllPickListData();
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
                String sellerSKU = p.getItem().getSellerSKUCode();
                double qty = p.getQty();
                double pickQty = p.getPickQty();
                LocalDate date = p.getDate();
                String description = p.getItem().getDescription();
                String bin = p.getStorage().getBinNumber();
                String rack = p.getStorage().getRackNumber();
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
    
    @GetMapping("/boms/{orderNo}")
    public List<Bom> getBomWithOrderNo(@PathVariable String orderNo){
        return pickListService.getOrdersWithBom(orderNo);
    }

    @GetMapping("/bom/default/bomCode")
    public String getMethodName(@RequestParam String orderNo) {
        return pickListService.getDefaultBomCode(orderNo);
    }
    
}

