package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.example.inventorygenius.entity.PickList;
import com.example.inventorygenius.entity.OrderData;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.BomItem;
import com.example.inventorygenius.entity.PickListData;

import com.example.inventorygenius.entity.Storage;

import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.BomService;
import com.example.inventorygenius.service.StockService;
import com.example.inventorygenius.service.StockCountService;
import com.example.inventorygenius.service.PickListDataService;
import com.example.inventorygenius.service.StorageService;
import com.example.inventorygenius.service.ItemSupplierService;

import com.example.inventorygenius.repository.PickListRepository;

@Service
public class PickListService {

    @Autowired
    private PickListRepository pickListRepository;

    @Autowired
    private ItemSupplierService itemSupplierService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BomService bomService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockCountService stockCountService;

    @Autowired
    private PickListDataService pickListDataService;
    

    public List<PickList> getAllPickLists() {
        return pickListRepository.findAll();
    }

    public Optional<PickList> getPickListById(Long id) {
        return pickListRepository.findById(id);
    }

    public PickList createPickList(PickList pickList) {
        return pickListRepository.save(pickList);
    }

    public PickList updatePickList(Long id, PickList updatedPickList) {
        // Check if the picklist with the given id exists
        if (pickListRepository.existsById(id)) {
            updatedPickList.setPicklistId(id);
            return pickListRepository.save(updatedPickList);
        } else {
            throw new RuntimeException("PickList not found with id: " + id);
        }
    }

    public void deletePickList(Long id) {
        // Check if the picklist with the given id exists
        if (pickListRepository.existsById(id)) {
            pickListRepository.deleteById(id);
        } else {
            throw new RuntimeException("PickList not found with id: " + id);
        }
    }

    public List<Order> getAllNotGeneratedOrders() {
        List<Order> notGeneratedOrders = new ArrayList<>();
        List<Order> allOrders = orderService.getAllOrders();
        List<PickList> allPickLists = getAllPickLists();
        
        // Create a set to store order numbers that are in pick lists
        Set<String> orderNumbersInPickLists = new HashSet<>();
        
        // Iterate through all pick lists and collect order numbers
        for (PickList p : allPickLists) {
            for (Order or : p.getOrders()) {
                orderNumbersInPickLists.add(or.getOrderNo());
            }
        }
        
        // Iterate through all orders and add those that are not in any pick list and are not canceled
        for (Order o : allOrders) {
            if (!orderNumbersInPickLists.contains(o.getOrderNo()) && 
                !"Order Canceled".equals(o.getCancel())) { // Check if order is not in any pick list and not canceled
                notGeneratedOrders.add(o);
            }
        }
        
        return notGeneratedOrders;
    }
     

    public List<OrderData> getOrderData(Bom bom) {
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
                        orderData.setBomCode(bom.getBomCode());
                        if (item.getBoms().size() > 0){
                            System.out.println("size = " + item.getBoms().size());
                        
                            for (BomItem bomItem : bom.getItemsInBom()){
                            if (bomItem.getBomItem().equals(item.getParentSKU())) {
                                orderData.setPickQty((order.getQty()) * Double.parseDouble(bomItem.getQty()));
                                totalQty += order.getQty() * Double.parseDouble(bomItem.getQty());

                                orderData.setSellerSKU(itemSupplierService.getItemBySKUCode(item.getParentSKU()).getSellerSKUCode());
                                orderData.setDescription(itemSupplierService.getItemBySKUCode(item.getParentSKU()).getDescription());
                                
                            }
                            
                            else if (!bomItem.getBomItem().equals(item.getParentSKU())){
                                OrderData o = new OrderData();
                                Item i = itemSupplierService.getItemBySKUCode(bomItem.getBomItem());
                                o.setDate(order.getDate());
                                o.setOrderNo(order.getOrderNo());
                                o.setPortal(order.getPortal());
                                o.setSellerSKU(i.getSellerSKUCode());
                                o.setImg(i.getImg());
                                o.setBomCode(bom.getBomCode());
                                totalQty += order.getQty() * Double.parseDouble(bomItem.getQty());
                                o.setPickQty(order.getQty() * Double.parseDouble(bomItem.getQty()));
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

    public List<OrderData> getOrderDatas() {
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
                            for (BomItem bomItem : bom.getItemsInBom()){
                            if (bomItem.getBomItem().equals(item.getParentSKU())) {
                                orderData.setPickQty((order.getQty()) * Double.parseDouble(bomItem.getQty()));
                                totalQty += order.getQty() * Double.parseDouble(bomItem.getQty());

                                orderData.setSellerSKU(itemSupplierService.getItemBySKUCode(item.getParentSKU()).getSellerSKUCode());
                                orderData.setDescription(itemSupplierService.getItemBySKUCode(item.getParentSKU()).getDescription());
                                
                            }
                            
                            else if (!bomItem.getBomItem().equals(item.getParentSKU())){
                                OrderData o = new OrderData();
                                Item i = itemSupplierService.getItemBySKUCode(bomItem.getBomItem());
                                o.setDate(order.getDate());
                                o.setOrderNo(order.getOrderNo());
                                o.setPortal(order.getPortal());
                                o.setSellerSKU(i.getSellerSKUCode());
                                o.setImg(i.getImg());
                                totalQty += order.getQty() * Double.parseDouble(bomItem.getQty());
                                o.setPickQty(order.getQty() * Double.parseDouble(bomItem.getQty()));
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
        for (PickList pickList : getAllPickLists()){
            for (Order o : pickList.getOrders()){
                if (o.getOrderNo().equals(order.getOrderNo())){
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<PickListData> getData(){
        return pickListDataService.getAllPickListData();
    }
    

    public List<Bom> getOrdersWithBom(String OrderNo){
        List<Order> orders = orderService.findByOrderNo(OrderNo);
        List<Bom> bomList = new ArrayList<>();
        for(Order order : orders){
            for(Item item : order.getItems()){
                bomList.addAll(item.getBoms());
            }
        }
        return bomList;
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
    
    public String getDefaultBomCode (String orderNo){
        String bomC = "";
        List<Order> orders = orderService.findByOrderNo(orderNo);
       for(Order order : orders){
            for(Item item : order.getItems()){
                for (Bom bom : item.getBoms()){
                    if (isCurrentDateBetween(bom)){
                        bomC = bom.getBomCode();
                    }
                }
            }
        } 
        return bomC;
    }

    public ResponseEntity<Void> deletePickListByPickListNumber(Long pickListNumber, String bomCode) {
        // Find the picklist by pickListNumber
        PickList pickList = pickListRepository.findByPickListNumber(pickListNumber);

        if (pickList != null) {
        for (Order order : pickList.getOrders()){    
            Stock stock = new Stock();
            Item item = itemSupplierService.getItemBySKUCode(order.getItems().get(0).getSKUCode());
            if(item.getBoms().size() > 0){
                stock.setDate(new Date());
                stock.setSkucode(item.getParentSKU());
                stock.setSubQty("0");
                Bom bom = bomService.getBomByBomCode(bomCode);
                    for (BomItem bomItem : bom.getItemsInBom()){
                    if (bomItem.getBomItem().equals(item.getParentSKU())){
                        stock.setAddQty(String.valueOf(order.getQty() * Double.parseDouble(bomItem.getQty())));
                    }
                    if (!bomItem.getBomItem().equals(item.getParentSKU())){
                        Stock s = new Stock();
                        s.setDate(new Date());
                        s.setSubQty("0");
                        s.setItem(item);
                        s.setAddQty(String.valueOf(order.getQty() * Double.parseDouble(bomItem.getQty())));
                        s.setSkucode(bomItem.getBomItem());
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
                Bom bom = bomService.getBomByBomCode(bomCode);
                    for(BomItem bomItem : bom.getItemsInBom()){
                    if (bomItem.getBomItem().equals(order.getItems().get(0).getParentSKU())){
                        additionalCount = Double.parseDouble(String.valueOf(order.getQty())) * Double.parseDouble(bomItem.getQty());
                    }
                    else {
                        Double c = Double.parseDouble(String.valueOf(order.getQty())) * Double.parseDouble(bomItem.getQty());
                        StockCount sc = stockCountService.getStockCountBySKUCode(bomItem.getBomItem());
                        sc.setCount(c);
                        stockCountService.updateStockCount(sc);
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

        // Check if pickList is not null (i.e., picklist exists)
            pickListRepository.delete(pickList);
            return ResponseEntity.noContent().build();
        } else {
            throw new IllegalArgumentException("No picklist data found with the provided picklist number: " + pickListNumber);
        }
    }
    
}




