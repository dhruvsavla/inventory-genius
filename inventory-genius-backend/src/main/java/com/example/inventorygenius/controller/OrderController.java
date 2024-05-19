package com.example.inventorygenius.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.inventorygenius.entity.Bom;
import com.example.inventorygenius.entity.Item;
import com.example.inventorygenius.entity.Order;
import com.example.inventorygenius.entity.Stock;
import com.example.inventorygenius.entity.StockCount;
import com.example.inventorygenius.repository.OrderRepository;
import com.example.inventorygenius.service.ItemSupplierService;
import com.example.inventorygenius.service.OrderService;
import com.example.inventorygenius.service.StockCountService;
import com.example.inventorygenius.service.StockService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired 
    private StockService stockService;

    @Autowired 
    private StockCountService stockCountService;

    @Autowired
    private ItemSupplierService itemSupplierService;

    @PostMapping
    public ResponseEntity<Order> addItem(@RequestBody Order order) {
        Order newOrder = orderService.addOrder(order);
        return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllItems() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long orderId, @RequestBody Order updatedOrder) {
    Optional<Order> existingOrderOptional = orderRepository.findById(orderId);
    
    if (existingOrderOptional.isPresent()) {
        Order existingOrder = existingOrderOptional.get();
        // Update properties of the existing item with the new values
        existingOrder.setCourier(updatedOrder.getCourier());
        existingOrder.setDate(updatedOrder.getDate());
        existingOrder.setDispatched(updatedOrder.getDispatched());
        existingOrder.setOrderNo(updatedOrder.getOrderNo());
        existingOrder.setPortal(updatedOrder.getPortal());
        existingOrder.setPortalOrderLineId(updatedOrder.getPortalOrderLineId());
        existingOrder.setPortalOrderNo(updatedOrder.getPortalOrderNo());
        existingOrder.setPortalSKU(updatedOrder.getPortalSKU());
        existingOrder.setProductDescription(updatedOrder.getProductDescription());
        existingOrder.setQty(updatedOrder.getQty());
        existingOrder.setShipByDate(updatedOrder.getShipByDate());
        existingOrder.setSellerSKU(updatedOrder.getSellerSKU());
        existingOrder.setCancel(updatedOrder.getCancel());
        existingOrder.setItems(updatedOrder.getItems());
        
        // Check if the order has been canceled
        if (updatedOrder.getCancel().equals("Order Canceled") && updatedOrder.getPicklist() == null) {
            // Create a new stock object
            Stock stock = new Stock();
        Item item = itemSupplierService.getItemBySKUCode(updatedOrder.getItems().get(0).getSKUCode());
        if(item.getBoms().size() > 0){
            stock.setDate(new Date());
            stock.setSkucode(item.getParentSKU());
            stock.setSubQty("0");
            for (Bom bom : item.getBoms()){
                if (bom.getBomItem().equals(item.getParentSKU())){
                    stock.setAddQty(String.valueOf(updatedOrder.getQty() * bom.getQty()));
                }
                if (!bom.getBomItem().equals(item.getParentSKU())){
                    Stock s = new Stock();
                    s.setDate(new Date());
                    s.setSubQty("0");
                    s.setItem(item);
                    s.setAddQty(String.valueOf(updatedOrder.getQty() * bom.getQty()));
                    s.setSkucode(bom.getBomItem());
                    s.setSource("Order");
                    s.setMessage("Order Cancelled");
                    s.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
                    stockService.addStock(s);
                }
            }
            
            stock.setItem(updatedOrder.getItems().get(0));

            stock.setSource("Order");
            stock.setMessage("Order Cancelled");
            stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
        }
        else {
            stock.setDate(new Date());
            stock.setSkucode(updatedOrder.getItems().get(0).getSKUCode());
            stock.setSubQty("0");
            stock.setAddQty(String.valueOf(updatedOrder.getQty()));
            stock.setItem(updatedOrder.getItems().get(0));
    
            stock.setSource("Order");
            stock.setMessage("Order Cancelled");
            stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
        }        

        stockService.addStock(stock);
            StockCount sc = new StockCount();
            if(updatedOrder.getItems().get(0).getBoms().size() > 0) {
                for (Bom b : updatedOrder.getItems().get(0).getBoms()){
                    if (b.getBomItem().equals(updatedOrder.getItems().get(0).getParentSKU())){
                        sc = stockCountService.getStockCountBySKUCode(updatedOrder.getItems().get(0).getParentSKU());
                        sc.setCount(sc.getCount() + updatedOrder.getQty() * b.getQty());
                    }
                    else{
                        StockCount scBom = new StockCount();
                        scBom = stockCountService.getStockCountBySKUCode(b.getBomItem());
                        scBom.setCount(scBom.getCount() + updatedOrder.getQty() * b.getQty());
                        stockCountService.updateStockCount(scBom);
                    }  
                }
            }
            else{
                sc = stockCountService.getStockCountBySKUCode(updatedOrder.getItems().get(0).getSKUCode());
                sc.setCount(sc.getCount() + updatedOrder.getQty());
            }
            stockCountService.updateStockCount(sc);
        }


        if (updatedOrder.getCancel().equals("Order Not Canceled")) {
            // Create a new stock object
            Stock stock = new Stock();
        Item item = itemSupplierService.getItemBySKUCode(updatedOrder.getItems().get(0).getSKUCode());
        if(item.getBoms().size() > 0){
            stock.setDate(new Date());
            stock.setSkucode(item.getParentSKU());
            stock.setAddQty("0");
            for (Bom bom : item.getBoms()){
                if (bom.getBomItem().equals(item.getParentSKU())){
                    stock.setSubQty(String.valueOf(updatedOrder.getQty() * bom.getQty()));
                }
                if (!bom.getBomItem().equals(item.getParentSKU())){
                    Stock s = new Stock();
                    s.setDate(new Date());
                    s.setAddQty("0");
                    s.setItem(item);
                    s.setSubQty(String.valueOf(updatedOrder.getQty() * bom.getQty()));
                    s.setSkucode(bom.getBomItem());
                    s.setSource("Order");
                    s.setMessage("Order Not Cancelled");
                    s.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
                    stockService.addStock(s);
                }
            }
            
            stock.setItem(updatedOrder.getItems().get(0));

            stock.setSource("Order");
            stock.setMessage("Order Not Cancelled");
            stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
        }
        else {
            stock.setDate(new Date());
            stock.setSkucode(updatedOrder.getItems().get(0).getSKUCode());
            stock.setAddQty("0");
            stock.setSubQty(String.valueOf(updatedOrder.getQty()));
            stock.setItem(updatedOrder.getItems().get(0));
    
            stock.setSource("Order");
            stock.setMessage("Order Not Cancelled");
            stock.setNumber("Order Number = " + String.valueOf(updatedOrder.getOrderNo()));
        }        

        stockService.addStock(stock);
            
            StockCount sc = stockCountService.getStockCountBySKUCode(updatedOrder.getItems().get(0).getSKUCode());
            sc.setCount(sc.getCount() - updatedOrder.getQty());
            stockCountService.updateStockCount(sc);
        }
        
        // Save the updated item
        Order savedOrder = orderRepository.save(existingOrder);
        return ResponseEntity.ok(savedOrder);
    } else {
        return ResponseEntity.notFound().build();
    }
}



    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable("id") Long id) {
        System.out.println("deleted");
        orderService.deleteOrderById(id);
    }

    private final OrderRepository orderRepository;

     @Autowired
    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/with-items")
    public List<Order> getOrdersWithItems() {
        return orderRepository.findAllWithItems();
    }

    @GetMapping("/not/generated/orders")
    public List<Order> getNotGeneratedOrders() {
        return orderService.getAllNotGeneratedOrders();
    }  

    @GetMapping("/not/generated/packinglist/orders")
    public List<Order> getNotGeneratedPackingListOrders() {
        return orderService.getAllNotGeneratedPackListOrders();
    }
    
}
