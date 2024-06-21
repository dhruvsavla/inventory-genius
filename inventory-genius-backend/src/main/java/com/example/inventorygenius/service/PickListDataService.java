package com.example.inventorygenius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.inventorygenius.repository.PickListDataRepository;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.BomItem;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Storage;
import com.example.inventorygenius.entity.PickListData;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.StorageService;
import com.example.inventorygenius.service.ItemSupplierService;
import com.example.inventorygenius.service.BomService;
import com.example.inventorygenius.service.StockService;
import com.example.inventorygenius.service.StockCountService;

import java.util.Date;
import java.util.List;

@Service
public class PickListDataService {

    @Autowired
    private PickListDataRepository pickListDataRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    @Autowired
    private BomService bomService;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockCountService stockCountService;

    // Get all picklist data
    public List<PickListData> getAllPickListData() {
        return pickListDataRepository.findAll();
    }

    // Get picklist data by ID
    public PickListData getPickListDataById(Long pickListId) {
        return pickListDataRepository.findById(pickListId).orElse(null);
    }

    // Add new picklist data
    @Transactional
    public PickListData addPickListData(PickListData pickListData) {
        List<Order> orders = orderService.findByOrderNo(pickListData.getOrderNo());
        for(Order o : orders){
            pickListData.setOrder(o);
            break;
        }
        Item itemP = itemSupplierService.findItemsBySellerSKUAndDescription(pickListData.getSellerSKU(), pickListData.getDescription());
        System.out.println("picklist item sku = " + itemP);
        Storage storage = storageService.getStorageByBinAndRack(pickListData.getBinNumber(), pickListData.getRackNumber(), itemP.getSKUCode());
        //System.out.println("picklist storage sku = " + storage.getS());
        pickListData.setStorage(storage);
        pickListData.setItem(itemP);


        List<Order> orderList = orderService.findByOrderNo(pickListData.getOrder().getOrderNo());
        for (Order order : orderList){
            Stock stock = new Stock();
            stock.setItem(itemP);
            stock.setSkucode(itemP.getSKUCode());
            stock.setDate(new Date());
            stock.setAddQty("0");
            stock.setSubQty(String.valueOf(pickListData.getPickQty()));
            stock.setSource("picklist/order");
            stock.setMessage("pickList generated for order");
            stock.setNumber("pickList Number = " + pickListData.getPickListNumber() + " order no = " + String.valueOf(order.getOrderNo()));

            stockService.addStock(stock);

            StockCount stockCount = stockCountService.getStockCountBySKUCode(itemP.getSKUCode());
            Double prevCount = stockCount.getCount();
            stockCount.setCount(prevCount - pickListData.getPickQty());
            stockCountService.updateStockCount(stockCount);
    }

        return pickListDataRepository.save(pickListData);
    }

    // Update picklist data
    public PickListData updatePickListData(Long pickListId, PickListData pickListData) {
        pickListData.setPickListId(pickListId);
        return pickListDataRepository.save(pickListData);
    }

    // Delete picklist data
    public void deletePickListData(Long pickListId) {
        pickListDataRepository.deleteById(pickListId);
    }

    @Transactional
    public List<PickListData> deletePickListDataByPickListNumber(Long pickListNumber) {
        // Find the list of PickListData objects by pickListNumber
        List<PickListData> pickListDataList = pickListDataRepository.findByPickListNumber(pickListNumber);
        
        
        if (!pickListDataList.isEmpty()) {
        for(PickListData p : pickListDataList){
            Item itemP = p.getItem();
            
                List<Order> orders = orderService.findByOrderNo(p.getOrderNo());
                for(Order o : orders){
                    o.setOrderStatus("Order Received");
                    orderService.updateOrder(o.getOrderId(), o);
                }

                Stock stock = new Stock();
                stock.setItem(itemP);
                stock.setSkucode(itemP.getSKUCode());
                stock.setDate(new Date());
                stock.setAddQty(String.valueOf(p.getPickQty()));
                stock.setSubQty("0");
                stock.setSource("picklist/order");
                stock.setMessage("pickList deleted");
                stock.setNumber("pickList Number = " + p.getPickListNumber() + " order no = " + String.valueOf(p.getOrder().getOrderNo()));

                stockService.addStock(stock);

                StockCount stockCount = stockCountService.getStockCountBySKUCode(itemP.getSKUCode());
                Double prevCount = stockCount.getCount();
                stockCount.setCount(prevCount + p.getPickQty());
                stockCountService.updateStockCount(stockCount);

                pickListDataRepository.delete(p);
                
            }
                        
            return pickListDataList;
        } else {
            throw new IllegalArgumentException("No picklist data found with the provided picklist number: " + pickListNumber);
        }
    }
    
}
       
